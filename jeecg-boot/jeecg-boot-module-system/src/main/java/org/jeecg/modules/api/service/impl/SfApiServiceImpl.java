package org.jeecg.modules.api.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.modules.api.entity.*;
import org.jeecg.modules.api.exception.AccountAbnormalException;
import org.jeecg.modules.api.exception.BusinessException;
import org.jeecg.modules.api.exception.SignatureException;
import org.jeecg.modules.api.extension.PayChannelContext;
import org.jeecg.modules.api.service.ICommonApiService;
import org.jeecg.modules.api.service.ISfApiService;
import org.jeecg.modules.exception.RRException;
import org.jeecg.modules.pay.entity.*;
import org.jeecg.modules.pay.service.IOrderInfoEntityService;
import org.jeecg.modules.pay.service.IOrderToolsService;
import org.jeecg.modules.system.entity.SysUser;
import org.jeecg.modules.system.service.ISysUserService;
import org.jeecg.modules.util.BaseConstant;
import org.jeecg.modules.v2.entity.PayBusiness;
import org.jeecg.modules.v2.entity.PayChannel;
import org.jeecg.modules.v2.entity.PayUserChannel;
import org.jeecg.modules.v2.entity.PayUserProduct;
import org.jeecg.modules.v2.service.impl.PayBusinessServiceImpl;
import org.jeecg.modules.v2.service.impl.PayChannelServiceImpl;
import org.jeecg.modules.v2.service.impl.PayUserChannelServiceImpl;
import org.jeecg.modules.v2.service.impl.PayUserProductServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class SfApiServiceImpl implements ISfApiService {

    @Autowired
    IOrderInfoEntityService orderService;
    @Autowired
    IOrderToolsService orderTools;
    @Autowired
    private PayChannelContext payChannel;
    @Autowired
    private ISysUserService userService;
    @Autowired
    private ICommonApiService apiService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PayOrderUrlResponse createOrder(OrderInfoEntity orderInfo) throws Exception {
        // 重复订单校验
        orderTools.checkOuterOrderId(orderInfo.getUserName(), orderInfo.getOuterOrderId());
        apiService.checkProduct(orderInfo.getUserName(), orderInfo.getProductCode());
        PayUserChannel channel = apiService.findChannel(orderInfo.getUserName(), orderInfo.getProductCode());
        apiService.checkSubmitAmountLegal(orderInfo.getSubmitAmount(), channel);
        PayBusiness business =
                apiService.findBusiness(orderInfo.getAgentUsername(), channel.getChannelCode(), orderInfo.getProductCode());
        this.saveOrder(orderInfo, channel, business);
        // 请求外部平台,生成支付链接
        String payUrl = payChannel.request(orderInfo);
        return PayOrderUrlResponse.success(payUrl);
    }

    private void saveOrder(OrderInfoEntity orderInfo, PayUserChannel channel, PayBusiness business) throws Exception {
        // 计算手续费
        String rate = apiService.getRate(channel);
        BigDecimal commission =
            orderInfo.getSubmitAmount().multiply(new BigDecimal(rate)).setScale(2, BigDecimal.ROUND_HALF_UP);
        // 创建本平台订单
        orderInfo.setStatus(BaseConstant.ORDER_STATUS_NOT_PAY);
        orderInfo.setCreateTime(new Date());
        orderInfo.setCreateBy("api");
        orderInfo.setOrderId(orderTools.generateOrderId());
        orderInfo.setPoundage(commission);
        orderInfo.setActualAmount(orderInfo.getSubmitAmount().subtract(commission));
        orderInfo.setBusinessCode(business.getBusinessCode());
        orderInfo.setPayType(channel.getChannelCode());
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



    @Override
    public Object callback(String payType, String orderId) throws Exception {
        log.info("==>回调，回调通道为：{}，订单号为：{}", payType, orderId);
        return payChannel.callback(payType, orderId);
    }

    @Override
    public SysUser verifyAccountInfo(ApiRequestBody reqBody) throws Exception {
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
    public void verifySignature(ApiRequestBody reqBody, String apiKey) throws Exception {
        // 验签
        if (!reqBody.verifySignature(apiKey)) {
            log.error("=======>商户[{}]创建订单：签名失败", reqBody.getUsername());
            throw new SignatureException("签名失败");
        }
        log.info("=======>商户[{}]创建订单：验签成功", reqBody.getUsername());
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




}
