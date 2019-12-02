package org.jeecg.modules.df.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONException;
import cn.hutool.json.JSONObject;
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

/**
 * @author ruanzh
 * @since 2019/11/17
 */
@Slf4j
@Service
public class ApiServiceImpl implements IApiService {

  @Autowired private ISysUserService userService;

  @Autowired private IUserAmountEntityService amountService;

  @Autowired private IPayOrderService payOrderService;

  @Override
  @Transactional(rollbackFor = Exception.class)
  public ApiResponseBody<PayOrderResult> createOrder(ApiRequestBody req) {
    log.info("\n=======>调用订单创建接口：检查参数");
    // 检查商户状态
    SysUser user = userService.getUserByName(req.getUsername());
    if (BeanUtil.isEmpty(user)) {
      log.error("\n=======>调用订单创建接口：商户不存在，请检查参数");
      throw new AccountAbnormalException("商户不存在，请检查参数");
    }

    // 验签
    if (!req.verifySignature(user.getApiKey())) {
      log.error("\n=======>调用订单创建接口：签名失败");
      throw new SignatureException("签名失败");
    }
    log.info("\n=======>调用订单创建接口：验签成功");

    if (!CommonConstant.USER_UNFREEZE.equals(user.getStatus())) {
      log.error("\n=======>调用订单创建接口：商户状态异常，请联系管理员！");
      throw new AccountAbnormalException("商户状态异常，请联系管理员！");
    }

    // 检查代理状态
    SysUser agent = userService.getUserById(user.getAgentId());
    if (!CommonConstant.USER_UNFREEZE.equals(agent.getStatus())) {
      log.error("\n=======>调用订单创建接口：商户上级代理状态异常，请联系管理员！");
      throw new AccountAbnormalException("商户上级代理状态异常，请联系管理员！");
    }
    // 解密数据
    String data = req.decodeData(user.getApiKey());
    JSONObject jsonObject = JSONUtil.parseObj(data);
    PayOrderData payOrderData = jsonObject.toBean(PayOrderData.class);
    log.info("\n=======>订单[{}]：解密成功", payOrderData.getBizOrderNo());

    // 检查订单信息
    PayOrder order = payOrderData.toPayOrder(user);
    PayOrderResult result = payOrderService.apiOrder(order);
    log.info(result.toJsonString());
    log.info("\n=======>订单[{}]：创建成功", order.getOuterOrderId());
    ApiResponseBody resp = ApiResponseBody.ok();
    resp.setData(result);
    return resp;
  }

  @Override
  public ApiResponseBody queryOrder(ApiRequestBody req) {
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
    ApiResponseBody resp = ApiResponseBody.ok();
    resp.setData(result);
    return resp;
  }

  @Override
  public boolean callback(String orderId) {
    PayOrder order = payOrderService.getById(orderId);
    if (!order.getStatus().equals("2") && !order.getStatus().equals("3")) {
      throw new RRException("订单未完结，不允许回调");
    }

    SysUser user = userService.getById(order.getUserId());

    PayOrderResult result = PayOrderResult.fromPayOrder(order);
    CallbackBody body = new CallbackBody();
    body.setUsername(user.getUsername());
    body.setTimestamp(System.currentTimeMillis());
    body.setData(result.encodeData(user.getApiKey()));
    body.setSign(body.sign(user.getApiKey()));

    log.info("\n=======>订单[{}]：发送异步回调", order.getOuterOrderId());
    String post = HttpUtil.post(order.getCallbackUrl(), body.toJsonString());
    // 先更新为已发送未返回
    order.setCallbackStatus("1");
    payOrderService.updateById(order);

    log.info("\n=======>订单[{}]：异步回调返回报文:{}", order.getOuterOrderId(), post);

    JSONObject jsonObject = JSONUtil.parseObj(post);
    if (BeanUtil.isEmpty(jsonObject)) {
      log.error("\n=======>订单[{}]：异步回调返回报文解析失败", order.getOuterOrderId());
      throw new RRException("解析返回数据出错！请联系管理员！");
    }

    int code = (int) jsonObject.get("code");
    if (code == 0) {
      log.info("\n=======>订单[{}]：异步回调返回报文解析成功，更新回调状态", order.getOuterOrderId());
      order.setCallbackStatus("2");
      payOrderService.updateById(order);
    }

    return true;
  }
}
