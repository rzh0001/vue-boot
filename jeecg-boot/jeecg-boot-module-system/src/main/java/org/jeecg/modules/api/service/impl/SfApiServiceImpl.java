package org.jeecg.modules.api.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.modules.api.entity.*;
import org.jeecg.modules.api.exception.AccountAbnormalException;
import org.jeecg.modules.api.exception.BusinessException;
import org.jeecg.modules.api.exception.SignatureException;
import org.jeecg.modules.api.extension.PayChannelContext;
import org.jeecg.modules.api.service.ISfApiService;
import org.jeecg.modules.exception.RRException;
import org.jeecg.modules.pay.entity.*;
import org.jeecg.modules.pay.service.IChannelEntityService;
import org.jeecg.modules.pay.service.IOrderInfoEntityService;
import org.jeecg.modules.pay.service.IOrderToolsService;
import org.jeecg.modules.pay.service.IProductService;
import org.jeecg.modules.system.entity.SysUser;
import org.jeecg.modules.system.service.ISysUserService;
import org.jeecg.modules.util.BaseConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class SfApiServiceImpl implements ISfApiService {

    @Autowired
    IOrderInfoEntityService orderService;

    @Autowired
    IOrderToolsService orderTools;

    @Autowired
    private IProductService productService;

    @Autowired
    private PayChannelContext payChannel;

    @Autowired
    private IChannelEntityService channelEntityService;

    @Autowired
    private ISysUserService userService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PayOrderUrlResponse createOrder(OrderInfoEntity orderInfo) throws Exception {
        String channelCode = this.verifyUserChannel(orderInfo);
        this.saveOrder(orderInfo, channelCode);
        // 请求外部平台,生成支付链接
        String payUrl = payChannel.request(orderInfo);
        return PayOrderUrlResponse.success(payUrl);
    }

	/**
	 * 保存订单信息
	 * @param orderInfo
	 * @param channelCode
	 */
    private void saveOrder(OrderInfoEntity orderInfo, String channelCode)throws Exception {
        // 计算手续费
        String rate = this.getRate(orderInfo.getUserName(), channelCode);
        BigDecimal commission =
            orderInfo.getSubmitAmount().multiply(new BigDecimal(rate)).setScale(2, BigDecimal.ROUND_HALF_UP);
        // 创建本平台订单
        orderInfo.setStatus(BaseConstant.ORDER_STATUS_NOT_PAY);
        orderInfo.setCreateTime(new Date());
        orderInfo.setCreateBy("api");
        orderInfo.setOrderId(orderTools.generateOrderId());
        orderInfo.setPoundage(commission);
        orderInfo.setActualAmount(orderInfo.getSubmitAmount().subtract(commission));
        orderService.save(orderInfo);
    }

    @Override
    public ApiResponseBody queryOrder(String outerOrderId, String username) {
        QueryWrapper<OrderInfoEntity> qw = new QueryWrapper<>();
        qw.lambda().eq(OrderInfoEntity::getOuterOrderId, outerOrderId).eq(OrderInfoEntity::getUserName, username);
        OrderInfoEntity orderInfo = orderService.getOne(qw);
        PayOrderResponseData data = PayOrderResponseData.fromPayOrder(orderInfo);

        return new ApiResponseBody(data);
    }

    /**
     * 校验金额合法性
     *
     * @param submitAmount
     * @param channel
     * @return
     */
    private boolean checkSubmitAmountLegal(BigDecimal submitAmount, UserChannelEntity channel) {
        if (channel.getLowerLimit() != null && submitAmount.compareTo(channel.getLowerLimit()) < 0) {
            return false;
        }
        if (channel.getUpperLimit() != null && submitAmount.compareTo(channel.getUpperLimit()) > 0) {
            return false;
        }
        return true;
    }

    /**
     * 获取用户的通道费率
     *
     * @param userName
     * @param channelCode
     * @return
     */
    private String getRate(String userName, String channelCode) {
        UserRateEntity userRate = orderTools.getUserRate(userName, channelCode);
        if (userRate == null) {
            // 如果用户未配置费率，则使用通道费率
            ChannelEntity channel = channelEntityService.queryChannelByCode(channelCode);
            return channel.getRate();
        }
        return userRate.getUserRate();
    }

    @Override
    public Object callback(String payType, String orderId) throws Exception {
        log.info("==>回调，回调通道为：{}，订单号为：{}",payType,orderId);
        return payChannel.callback(payType, orderId);
    }

    @Override
    public SysUser verifyUser(ApiRequestBody reqBody) throws Exception {
        // 检查账户状态
        SysUser user = userService.getUserByName(reqBody.getUsername());
        if (BeanUtil.isEmpty(user)) {
            log.error("=======>商户[{}]创建订单：商户不存在，请检查参数", reqBody.getUsername());
            throw new AccountAbnormalException("商户不存在，请检查参数");
        }
        if (!BaseConstant.USER_MERCHANTS.equals(user.getMemberType())) {
            log.info("用户类型不是商户，无法提交订单，用户名为：{}", reqBody.getUsername());
            throw new RRException("用户类型不是商户，无法提交订单");
        }
        // 验签
        if (!reqBody.verifySignature(user.getApiKey())) {
            log.error("=======>商户[{}]创建订单：签名失败", reqBody.getUsername());
            throw new SignatureException("签名失败");
        }
        log.info("=======>商户[{}]创建订单：验签成功", reqBody.getUsername());

        if (!CommonConstant.USER_UNFREEZE.equals(user.getStatus())) {
            log.error("=======>商户[{}]创建订单：商户状态异常，请联系管理员！", reqBody.getUsername());
            throw new AccountAbnormalException("商户状态异常，请联系管理员！");
        }
        // 检查代理状态
        SysUser agent = userService.getUserById(user.getAgentId());
        if (!CommonConstant.USER_UNFREEZE.equals(agent.getStatus())) {
            log.error("=======>商户[{}]创建订单：商户上级代理[{}]状态异常，请联系管理员！", reqBody.getUsername(), agent.getUsername());
            throw new AccountAbnormalException("商户上级代理状态异常，请联系管理员！");
        }

        return user;
    }

    @Override
    public PayOrderRequestData decodeData(ApiRequestBody reqBody, SysUser user) throws Exception {
        // 解析数据，验证数据合法性
        String data = reqBody.decodeData(user.getApiKey());
        JSONObject jsonObject = JSONUtil.parseObj(data);
        PayOrderRequestData payOrderData = jsonObject.toBean(PayOrderRequestData.class);
        log.info("=======>商户[{}]创建订单[{}]：解密成功", reqBody.getUsername(), payOrderData.getOuterOrderId());
        log.info("=======>订单[{}]：{}", payOrderData.getOuterOrderId(), payOrderData.toJsonString());
        // 检查参数合法性
        payOrderData.checkData();
        return payOrderData;
    }

    @Override
    public String verifyUserChannel(OrderInfoEntity orderInfo) {
        // 重复订单校验
        orderTools.checkOuterOrderId(orderInfo.getUserName(), orderInfo.getOuterOrderId());
        // 检查产品、通道配置
        UserChannelEntity channel =
            productService.getChannelByProduct(orderInfo.getUserName(), orderInfo.getProductCode());
        UserBusinessEntity userChannelConfig = orderTools.getUserChannelConfig(orderInfo);
        // 交易金额检查
        if (!this.checkSubmitAmountLegal(orderInfo.getSubmitAmount(), channel)) {
            throw new BusinessException("单笔交易金额限额[" + channel.getLowerLimit() + "," + channel.getUpperLimit() + "]");
        }
        orderInfo.setPayType(channel.getChannelCode());
        orderInfo.setBusinessCode(userChannelConfig.getBusinessCode());
        return channel.getChannelCode();
    }
}
