package org.jeecg.modules.pay.service.requestPayUrl.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.util.RedisUtil;
import org.jeecg.modules.exception.RRException;
import org.jeecg.modules.pay.entity.BaiyitongParam;
import org.jeecg.modules.pay.entity.OrderInfoEntity;
import org.jeecg.modules.pay.entity.UserBusinessEntity;
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

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

/**
 * @title: 百易通-微信支付
 * @Description:
 * @author:
 * @create: 2019-11-05 14:20
 */
@Service
@Slf4j
public class BaiyitongWechatPayImpl implements RequestPayUrl<OrderInfoEntity, String, String, String,String, UserBusinessEntity,Object> {
    private static final String MD5_KEY="Pd3y8WTAJjacGlg6mxr3d3MYn57Vqyzq";
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private IOrderInfoEntityService orderInfoEntityService;
    @Autowired
    private ISysUserService userService;
    @Override
    public R requestPayUrl(OrderInfoEntity order, String userName, String url, String key, String callbackUrl,
                           UserBusinessEntity userBusiness) throws Exception {
        BaiyitongParam param = valueOf(order,userBusiness.getBusinessCode(),callbackUrl);
        String json = JSON.toJSONString(param);
        Map<String,Object> mapTypes = JSON.parseObject(json);
        log.info("===>请求百易通，获取支付链接，请求入参为：{}",json);
        HttpResult r = HttpUtils.doPost(url, mapTypes);
        log.info("===>请求百易通，获取支付链接,请求返回code为：{}，返回内容为：{}",r.getCode(),r.getBody());
        String payUrl = null;
        if(r != null && "200".equals(r.getCode().toString())){
            String body = r.getBody();
            JSONObject result = JSONObject.parseObject(body);
            if("200".equals(result.get("code").toString())){
                JSONObject data  = (JSONObject) result.get("data");
                payUrl = (String) data.get("payUrl");
            }else{
                log.info("===>订单为：{}，请求百易通平台，获取支付链接，返回的code为：{}",order.getOrderId(),result.get("code").toString());
                throw new RRException("设备产码失败，请联系商户");
            }
        }else{
            log.info("===>===>订单为：{}，请求百易通平台，获取支付链接，请求返回的状态码为：{}",order.getOrderId(),r.getCode());
            throw new RRException("设备产码失败，请联系商户");
        }
        redisUtil.del(order.getOuterOrderId());
        if(StringUtils.isEmpty(payUrl)){
            throw new RRException("获取支付地址失败");
        }
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
        String orderId = (String)map.get("out_trade_no");
        log.info("===>百易通回调四方，回调单号为：{}",orderId);
        SortedMap<Object,Object> params1 = new TreeMap<Object,Object>();
        params1.put("callbacks",map.get("callbacks"));
        params1.put("appid",map.get("appid"));
        params1.put("pay_type",map.get("pay_type"));
        params1.put("success_url",map.get("success_url"));
        params1.put("error_url",map.get("error_url"));
        params1.put("out_trade_no",map.get("out_trade_no"));
        params1.put("amount",map.get("amount"));
        log.info("===>百易通回调，订单号：{},服务端sign串为：{}",orderId,params1);
        String sign=signForInspiry(params1,MD5_KEY);
        log.info("===>百易通回调，订单号：{},服务端sign为：{}",orderId,sign);
        if(!sign.equals(map.get("sign"))){
            log.info("===>百易通回调,签名验证不通过，入参的签名为：{},本地签名为：{}",map.get("sign").toString(),sign);
            return "签名验证失败";
        }
        OrderInfoEntity order = orderInfoEntityService.queryOrderInfoByOrderId(orderId);
        if(order == null){
            log.info("===>百易通回调，根据订单号：{}，查询不到订单信息",orderId);
            return "订单不存在";
        }
        order.setStatus(BaseConstant.ORDER_STATUS_SUCCESS_NOT_RETURN);
        SysUser user = userService.getUserByName(order.getUserName());
        String payType = (String) map.get("out_uid");
        R r = orderInfoEntityService.notifyCustomer(order,user,payType);
        if("0".equals(r.get("code"))){
            log.info("==>百易通回调四方结束，返回信付为success，订单号为：{}",orderId);
            return "success";
        }else {
            log.info("==>百易通回调四方结束，fail，订单号为：{}",orderId);
            return "fail";
        }
    }

    private BaiyitongParam valueOf(OrderInfoEntity order,String businessCode,String callBackUrl){
        BaiyitongParam param = new BaiyitongParam();
        param.setReturn_type("app");
        param.setAppid(businessCode);
        param.setPay_type("wechat");
        param.setAmount(order.getSubmitAmount().toString());
        param.setCallback_url(callBackUrl);
        param.setOut_uid(BaseConstant.REQUEST_BAIYITONG_WECHAT);
        param.setOut_trade_no(order.getOrderId());
        SortedMap<Object,Object> map = new TreeMap<Object,Object>();
        map.put("appid",param.getAppid());
        map.put("pay_type",param.getPay_type());
        map.put("amount",param.getAmount());
        map.put("callback_url",param.getCallback_url());
        map.put("out_trade_no",param.getOut_trade_no());
        map.put("version","v1.1");
        param.setSign(signForInspiry(map,MD5_KEY));
        return param;
    }
    /**
     * 生成签名；
     *
     * @param params
     * @return
     */
     private String signForInspiry(Map params, String key) {
        StringBuilder sbkey = new StringBuilder();
        Set es = params.entrySet();
        Iterator it = es.iterator();

        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            String k = (String) entry.getKey();
            Object v = entry.getValue();
            //空值不传递，不参与签名组串
            if (null != v && !"".equals(v)) {
                sbkey.append(k + "=" + v + "&");
            }
        }
        sbkey = sbkey.append("key=" + key);
        log.info("==>请求百易通挂马平台，生成的签名串为：{}",sbkey);
        //MD5加密,结果转换为大写字符
        String sign = MD5(sbkey.toString()).toUpperCase();
        log.info("==>请求百易通挂马平台，生成的签名为：{}",sign);
        return sign;
    }
    /**
     * 对字符串进行MD5加密
     *
     * @param str 需要加密的字符串
     * @return 小写MD5字符串 32位
     */
     private String MD5(String str) {
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("MD5");
            digest.update(str.getBytes());
            return new BigInteger(1, digest.digest()).toString(16);
        } catch (NoSuchAlgorithmException e) {
            log.info("==>请求百易通挂马平台,生成签名异常。异常信息为：{}",e);
        }
        return null;
    }
}
