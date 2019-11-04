package org.jeecg.modules.pay.service.requestPayUrl.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.jeecg.common.util.RedisUtil;
import org.jeecg.modules.exception.RRException;
import org.jeecg.modules.pay.entity.OrderInfoEntity;
import org.jeecg.modules.pay.entity.UserBusinessEntity;
import org.jeecg.modules.pay.entity.XinPayParam;
import org.jeecg.modules.pay.service.IOrderInfoEntityService;
import org.jeecg.modules.pay.service.requestPayUrl.RequestPayUrl;
import org.jeecg.modules.system.entity.SysUser;
import org.jeecg.modules.system.service.ISysUserService;
import org.jeecg.modules.util.BaseConstant;
import org.jeecg.modules.util.HttpResult;
import org.jeecg.modules.util.HttpUtils;
import org.jeecg.modules.util.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Map;

@Service
@Slf4j
public class XinPayImpl implements RequestPayUrl<OrderInfoEntity, String, String, String,String, UserBusinessEntity,Object> {
    private static final String MD5_KEY = "cc9d205602f87165866458eb79f1a254";
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private IOrderInfoEntityService orderInfoEntityService;
    @Autowired
    private ISysUserService userService;
    @Override
    public R requestPayUrl(OrderInfoEntity order, String userName, String url, String key, String callbackUrl, UserBusinessEntity userBusiness) throws Exception {
        XinPayParam payParam = new XinPayParam();
        payParam.setMerchantNum(userBusiness.getBusinessCode());
        payParam.setOrderNo(order.getOrderId());
        payParam.setAmount(order.getSubmitAmount().toString());
        payParam.setNotifyUrl(callbackUrl);
        payParam.setReturnUrl("");
        payParam.setPayType("alipay");
        //对应新四方的通道类型
        payParam.setAttch(BaseConstant.REQUEST_XINPAY_ALIPAY);
        StringBuilder sign = new StringBuilder();
        sign.append(payParam.getMerchantNum()).append(payParam.getOrderNo()).append(payParam.getAmount()).append(payParam.getNotifyUrl()).append(MD5_KEY);
        log.info("===>订单为：{}，请求信付支付平台，获取支付链接，原始签名串为：{}",order.getOrderId(),sign.toString());
        String signStr = DigestUtils.md5Hex(sign.toString());
        log.info("===>订单为：{}，请求信付支付平台，获取支付链接，签名sign为：{}",order.getOrderId(),signStr);
        payParam.setSign(signStr);
        String json = JSON.toJSONString(payParam);
        Map<String,Object> mapTypes = JSON.parseObject(json);
        log.info("===>订单为：{}，请求信付支付平台，获取支付链接，入参为：{}",order.getOrderId(),json);
        HttpResult r = HttpUtils.doPost(url, mapTypes);
        log.info("===>订单为：{}，请求信付支付平台，获取支付链接，返回结果为：{}",order.getOrderId(),r.getBody());
        String payUrl = null;
        if(r != null && "200".equals(r.getCode().toString())){
            String body = r.getBody();
            JSONObject result = JSONObject.parseObject(body);
            if("200".equals(result.get("code").toString())){
                JSONObject data  = (JSONObject) result.get("data");
                payUrl = (String) data.get("payUrl");
            }else{
                log.info("===>订单为：{}，请求信付支付平台，获取支付链接，返回的code为：{}",order.getOrderId(),result.get("code").toString());
                throw new RRException("设备产码失败，请联系商户");
            }
        }else{
            log.info("===>===>订单为：{}，请求信付支付平台，获取支付链接，请求返回的状态码为：{}",order.getOrderId(),r.getCode());
            throw new RRException("设备产码失败，请联系商户");
        }
        if(StringUtils.isEmpty(payUrl)){
            throw new RRException("获取支付地址失败");
        }
        redisUtil.del(order.getOuterOrderId());
        return R.ok().put("url", payUrl);
    }

    @Override
    public boolean orderInfoOk(OrderInfoEntity order, String url, UserBusinessEntity userBusiness) throws Exception {
        return false;
    }

    @Override
    public boolean notifyOrderFinish(OrderInfoEntity order, String key, UserBusinessEntity userBusiness, String url) throws Exception {
        return false;
    }

    @Override
    public Object callBack(Object object) throws Exception {
        Map<String, Object> map = (Map<String, Object>) object;
        String orderId =(String) map.get("orderNo");
        //验证签名： 订单状态+商户号+商户订单号+支付金额+商户秘钥
        StringBuilder sign = new StringBuilder();
        sign.append(map.get("state").toString()).append(map.get("merchantNum").toString()).append(orderId).append(map.get("amount").toString()).append(MD5_KEY);
        String signStr = DigestUtils.md5Hex(sign.toString());
        log.info("===>信付支付回调，签名串为：{},加密之后的签名为：{}",sign.toString(),signStr);
        if(!signStr.equals(map.get("sign").toString())){
            log.info("===>信付支付回调，入参的签名为：{},本地签名为：{}",map.get("sign").toString(),signStr);
            return "签名验证不通过";
        }
        OrderInfoEntity order = orderInfoEntityService.queryOrderInfoByOrderId(orderId);
        if(order == null){
            log.info("===>信付支付回调，根据订单号：{}，查询不到订单信息",orderId);
            return "订单不存在";
        }
        order.setStatus(BaseConstant.ORDER_STATUS_SUCCESS_NOT_RETURN);
        SysUser user = userService.getUserByName(order.getUserName());
        String payType = (String) map.get("attch");
        R r = orderInfoEntityService.notifyCustomer(order,user,payType);
        if("0".equals(r.get("code"))){
            return "success";
        }else {
            return "fail";
        }

    }
}
