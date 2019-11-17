package org.jeecg.modules.df.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.modules.df.entity.*;
import org.jeecg.modules.df.service.IApiService;
import org.jeecg.modules.df.service.IPayOrderService;
import org.jeecg.modules.exception.AccountAbnormalException;
import org.jeecg.modules.exception.ApiException;
import org.jeecg.modules.exception.RRException;
import org.jeecg.modules.exception.SignatureException;
import org.jeecg.modules.system.entity.SysUser;
import org.jeecg.modules.system.service.ISysUserService;
import org.jeecg.modules.system.service.IUserAmountEntityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

/**
 * @author ruanzh
 * @since 2019/11/17
 */
@Slf4j
@Service
public class ApiServiceImpl implements IApiService {
    
    @Autowired
    private ISysUserService userService;
    
    @Autowired
    private IUserAmountEntityService amountService;
    
    @Autowired
    private IPayOrderService payOrderService;
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public CommonResponseBody createOrder(CommonRequestBody req) {
        
        // 检查商户状态
        SysUser user = userService.getUserByName(req.getUsername());
        if (BeanUtil.isEmpty(user)) {
            throw new AccountAbnormalException("商户不存在，请检查参数");
        }
        
        // 验签
        if (!req.verifySignature(user.getApiKey())) {
            throw new SignatureException("签名失败");
        }
        
        if (!CommonConstant.USER_UNFREEZE.equals(user.getStatus())) {
            throw new AccountAbnormalException("商户状态异常，请联系管理员！");
        }
        
        // 检查代理状态
        SysUser agent = userService.getUserById(user.getAgentId());
        if (!CommonConstant.USER_UNFREEZE.equals(agent.getStatus())) {
            throw new AccountAbnormalException("商户上级代理状态异常，请联系管理员！");
        }
        
        // 解密数据
        String data = req.decodeData(user.getApiKey());
        cn.hutool.json.JSONObject jsonObject = JSONUtil.parseObj(data);
        PayOrderData payOrderData = jsonObject.toBean(PayOrderData.class);
        
        // 检查余额
        BigDecimal userAmount = amountService.getUserAmount(user.getId());
        if (payOrderData.getAmount().compareTo(userAmount) > 0) {
            throw new ApiException(1004, "余额不足，当前余额[" + userAmount + "]");
        }
        
        // 检查订单信息
        
        PayOrder order = payOrderData.toPayOrder(user);
        PayOrderResult result = payOrderService.apiOrder(order);
        log.info(result.toJsonString());
        return CommonResponseBody.ok(result.toJsonString());
    }
    
    @Override
    public CommonResponseBody queryOrder(CommonRequestBody req) {
        // 检查商户状态
        SysUser user = userService.getUserByName(req.getUsername());
        if (BeanUtil.isEmpty(user)) {
            throw new AccountAbnormalException("商户不存在，请检查参数");
        }
        
        // 验签
        if (!req.verifySignature(user.getApiKey())) {
            throw new SignatureException("签名失败");
        }
        
        if (!CommonConstant.USER_UNFREEZE.equals(user.getStatus())) {
            throw new AccountAbnormalException("商户状态异常，请联系管理员！");
        }
        
        // 检查代理状态
        SysUser agent = userService.getUserById(user.getAgentId());
        if (!CommonConstant.USER_UNFREEZE.equals(agent.getStatus())) {
            throw new AccountAbnormalException("商户上级代理状态异常，请联系管理员！");
        }
        
        // 解密数据
        String data = req.decodeData(user.getApiKey());
        cn.hutool.json.JSONObject jsonObject = JSONUtil.parseObj(data);
        QueryOrderData queryOrderData = jsonObject.toBean(QueryOrderData.class);
        
        PayOrder order = payOrderService.getByOuterOrderId(queryOrderData.getBizOrderNo());
        if (BeanUtil.isEmpty(order)) {
            throw new ApiException(1007, "查无此订单[outerOrderId:" + queryOrderData.getBizOrderNo() + "]");
        }
        PayOrderResult result = PayOrderResult.fromPayOrder(order);
        
        return CommonResponseBody.ok(result.toJsonString());
    }
    
    @Override
    public CommonResponseBody callback(String orderId) {
        PayOrder order = payOrderService.getById(orderId);
        if (!order.getStatus().equals("2") && !order.getStatus().equals("3")) {
            throw new RRException("订单未完结，不允许回调");
        }
        
        SysUser user = userService.getById(order.getUserId());
        
        PayOrderResult result = PayOrderResult.fromPayOrder(order);
        CommonResponseBody body = new CommonResponseBody();
        body.setData(result.encodeData(user.getApiKey()));
        
        String post = HttpUtil.post(order.getCallbackUrl(), body.toJsonString());
        return body;
    }
    
    
}
