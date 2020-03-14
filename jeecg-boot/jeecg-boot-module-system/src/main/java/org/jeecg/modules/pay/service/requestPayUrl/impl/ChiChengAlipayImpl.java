package org.jeecg.modules.pay.service.requestPayUrl.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.util.RedisUtil;
import org.jeecg.modules.pay.entity.ChiChengAlipayParam;
import org.jeecg.modules.pay.entity.OrderInfoEntity;
import org.jeecg.modules.pay.entity.UserBusinessEntity;
import org.jeecg.modules.pay.service.IOrderInfoEntityService;
import org.jeecg.modules.pay.service.factory.PayServiceFactory;
import org.jeecg.modules.pay.service.requestPayUrl.RequestPayUrl;
import org.jeecg.modules.util.BaseConstant;
import org.jeecg.modules.util.HttpResult;
import org.jeecg.modules.util.HttpUtils;
import org.jeecg.modules.util.R;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * @Author: wangjianbin
 * @Date: 2020/3/13 20:05
 */
@Service
@Slf4j
public class ChiChengAlipayImpl implements RequestPayUrl<OrderInfoEntity, String, String, String, String, UserBusinessEntity,
    Object>, InitializingBean{
    @Autowired
    private IOrderInfoEntityService orderInfoEntityService;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private RequestUrlUtils utils;
    @Override
    public R requestPayUrl(OrderInfoEntity order, String userName, String url, String key, String callbackUrl,
        UserBusinessEntity userBusiness) throws Exception {
        ChiChengAlipayParam param = new ChiChengAlipayParam();
        param.setPay_memberid(userBusiness.getBusinessCode());
        param.setPay_orderid(order.getOrderId());
        param.setPay_applydate(LocalDateTime.now().toString());
        param.setPay_bankcode("903");
        param.setPay_notifyurl(callbackUrl);
        param.setPay_callbackurl(null);
        param.setPay_amount(order.getSubmitAmount().toString());
        param.setPay_attach(BaseConstant.REQUEST_CHICHENG_ALIPAY);
        param.setPay_productname("支付宝个码");
        StringBuilder md5buffer = new StringBuilder();
        //pay_amount=pay_amount&pay_applydate=pay_applydate&pay_bankcode=pay_bankcode&pay_callbackurl=pay_callbackurl&pay_memberid=pay_memberid
        // &pay_notifyurl=pay_notifyurl&pay_orderid=pay_orderid&key=key
        md5buffer.append("pay_amount=").append(param.getPay_amount())
            .append("&pay_applydate=").append(param.getPay_applydate())
            .append("&pay_bankcode=").append(param.getPay_bankcode())
            .append("&pay_callbackurl=").append(param.getPay_callbackurl())
            .append("&pay_memberid=").append(param.getPay_memberid())
            .append("pay_notifyurl=").append(param.getPay_notifyurl())
            .append("&pay_orderid=").append(param.getPay_orderid())
            .append("&key=").append(userBusiness.getApiKey());
        log.info("==>赤诚支付 MD5为加密值为：{}",md5buffer.toString());
        //String md5 = this.md5(md5buffer.toString()).toUpperCase();
      //  log.info("==>赤诚支付 MD5加密值为：{} ",md5);
      //  param.setPay_md5sign(md5);
        return null;
    }

    public static void main(String[] args) throws Exception {
        ChiChengAlipayParam param = new ChiChengAlipayParam();
        param.setPay_memberid("10120");
        param.setPay_orderid("1234567891");
        param.setPay_applydate("2016-12-26 18:18:18");
        param.setPay_bankcode("903");
        param.setPay_notifyurl("http://www.baidu.com");
        param.setPay_callbackurl("http://www.baidu.com");
        param.setPay_amount("300");
        param.setPay_type("get");
        param.setPay_attach(BaseConstant.REQUEST_CHICHENG_ALIPAY);
        param.setPay_productname("alipay");
        StringBuilder md5buffer = new StringBuilder();
        md5buffer.append("pay_amount=").append(param.getPay_amount())
            .append("&pay_applydate=").append(param.getPay_applydate())
            .append("&pay_bankcode=").append(param.getPay_bankcode())
            .append("&pay_callbackurl=").append(param.getPay_callbackurl())
            .append("&pay_memberid=").append(param.getPay_memberid())
            .append("&pay_notifyurl=").append(param.getPay_notifyurl())
            .append("&pay_orderid=").append(param.getPay_orderid())
            .append("&key=").append("3e9n9v27lrp7pl98t477pwbrjvvpn4lc").append("");
        log.info("==>赤诚支付 MD5为加密值为：{}",md5buffer.toString());
        String md5 = md5(md5buffer.toString()).toUpperCase();
        log.info("==>赤诚支付 MD5加密值为：{} ",md5);
        param.setPay_md5sign(md5);
        String josn = JSONObject.toJSONString(param);
        Map mapTypes = JSON.parseObject(josn);
        String url = "http://www.chichengzhifu.cn/Pay_Index.html";
        log.info("==>请求地址：{}，请求入参：{}",url,mapTypes);
        HttpResult result = HttpUtils.doPost(url, mapTypes);
        System.out.println(result.getBody());
    }
    public static   String md5(String str) throws NoSuchAlgorithmException {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(str.getBytes());
            byte[] byteDigest = md.digest();
            int i;
            //字符数组转换成字符串
            StringBuffer buf = new StringBuffer("");
            for (int offset = 0; offset < byteDigest.length; offset++) {
                i = byteDigest[offset];
                if (i < 0)
                    i += 256;
                if (i < 16)
                    buf.append("0");
                buf.append(Integer.toHexString(i));
            }
            // 32位加密
            return buf.toString().toUpperCase();
            // 16位的加密
            //return buf.toString().substring(8, 24).toUpperCase();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean orderInfoOk(OrderInfoEntity order, String url, UserBusinessEntity userBusiness)
        throws Exception {
        return false;
    }

    @Override
    public boolean notifyOrderFinish(OrderInfoEntity order, String key, UserBusinessEntity userBusiness, String url)
        throws Exception {
        return false;
    }

    @Override
    public Object callBack(Object object) throws Exception {
        return null;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        PayServiceFactory.register(BaseConstant.REQUEST_CHICHENG_ALIPAY, this);
        PayServiceFactory.registerUrl(BaseConstant.REQUEST_CHICHENG_ALIPAY, utils.getRequestUrl(BaseConstant.REQUEST_CHICHENG_ALIPAY));
    }
}
