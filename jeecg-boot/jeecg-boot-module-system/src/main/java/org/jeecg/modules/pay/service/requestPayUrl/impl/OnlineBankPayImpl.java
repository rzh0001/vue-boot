package org.jeecg.modules.pay.service.requestPayUrl.impl;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.util.DateUtils;
import org.jeecg.common.util.RedisUtil;
import org.jeecg.modules.pay.entity.OnlineBankPayParam;
import org.jeecg.modules.pay.entity.OrderInfoEntity;
import org.jeecg.modules.pay.externalUtils.onlineBank.OnlineBankUtils;
import org.jeecg.modules.pay.service.factory.PayServiceFactory;
import org.jeecg.modules.pay.service.requestPayUrl.RequestPayUrl;
import org.jeecg.modules.system.service.ISysDictService;
import org.jeecg.modules.util.BaseConstant;
import org.jeecg.modules.util.HttpResult;
import org.jeecg.modules.util.HttpUtils;
import org.jeecg.modules.util.R;
import org.jeecg.modules.v2.entity.PayBusiness;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.TreeMap;

/**
 * @Author: wangjianbin
 * @Date: 2020/5/16 11:18
 */
@Service
@Slf4j
public class OnlineBankPayImpl implements
    RequestPayUrl<OrderInfoEntity, String, String, String, String, PayBusiness, Object, HttpServletResponse>, InitializingBean {
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private RequestUrlUtils utils;
    @Autowired
    public ISysDictService dictService;
    @Override
    public R requestPayUrl(OrderInfoEntity order, String userName, String url, String key, String callbackUrl,
        PayBusiness userBusiness,HttpServletResponse response) throws Exception {
        OnlineBankPayParam payParam = new OnlineBankPayParam();
        payParam.setPay_memberid(userBusiness.getBusinessCode());
        payParam.setPay_orderid(order.getOrderId());
        payParam.setPay_applydate(DateUtils.now());
        payParam.setPay_bankcode("907");
        payParam.setPay_notifyurl(callbackUrl);
        payParam.setPay_amount(order.getSubmitAmount().toString());
        payParam.setPay_attach(BaseConstant.REQUEST_ONLINE_BANK_PAY);
        payParam.setPay_productname(BaseConstant.REQUEST_ONLINE_BANK_PAY);
        payParam.setPay_productnum("1");
        payParam.setPay_productdesc(BaseConstant.REQUEST_ONLINE_BANK_PAY);
        payParam.setPay_producturl(callbackUrl);
        //签名
        String paramStr = JSON.toJSONString(payParam);
        log.info("==>网银转卡，请求入参为：{}",paramStr);
        StringBuilder md5str = new StringBuilder();
        md5str.append("pay_amount=").append(payParam.getPay_amount()).append("&pay_applydate=").append(payParam.getPay_applydate()).append("&pay_bankcode=").append(payParam.getPay_bankcode())
            .append("&pay_callbackurl=").append(payParam.getPay_callbackurl()).append("&pay_memberid=").append(payParam.getPay_memberid()).append("&pay_notifyurl").append(payParam.getPay_notifyurl())
            .append("&pay_orderid=").append(payParam.getPay_orderid()).append("&key=").append(userBusiness.getBusinessApiKey());
       String sign = OnlineBankUtils.md5(md5str.toString());
       log.info("==>网银转卡，签名串：{}，签名：{}",md5str.toString(),sign);
       payParam.setPay_md5sign(sign);
        String paramString = JSON.toJSONString(payParam);
        Map jsonObject = JSON.parseObject(paramString);
        HttpResult result = HttpUtils.doPost(url, jsonObject);
        String body = result.getBody();
        log.info("==>网银转卡返回值为：{}",body);
        redisUtil.del(order.getOuterOrderId());
        return null;
    }

    public static void main(String[] args) throws Exception {
        OnlineBankPayParam payParam = new OnlineBankPayParam();
        payParam.setPay_memberid("30180");
        payParam.setPay_orderid("123456");
        payParam.setPay_applydate(DateUtils.now());
        payParam.setPay_bankcode("907");
        payParam.setPay_notifyurl("http://baidu.com");
        payParam.setPay_amount("100");
        payParam.setPay_attach(BaseConstant.REQUEST_ONLINE_BANK_PAY);
        payParam.setPay_productname(BaseConstant.REQUEST_ONLINE_BANK_PAY);
        payParam.setPay_productnum("1");
        payParam.setPay_productdesc(BaseConstant.REQUEST_ONLINE_BANK_PAY);
        payParam.setPay_producturl("http://baidu.com");
        //签名
        String paramStr = JSON.toJSONString(payParam);
        log.info("==>网银转卡，请求入参为：{}",paramStr);
        StringBuilder md5str = new StringBuilder();
        md5str.append("pay_amount=").append(payParam.getPay_amount()).append("&pay_applydate=").append(payParam.getPay_applydate()).append("&pay_bankcode=").append(payParam.getPay_bankcode())
            .append("&pay_callbackurl=").append(payParam.getPay_callbackurl()).append("&pay_memberid=").append(payParam.getPay_memberid()).append("&pay_notifyurl").append(payParam.getPay_notifyurl())
            .append("&pay_orderid=").append(payParam.getPay_orderid()).append("&key=").append("yx4rqkp9tzbimbmataennzrfi9lqe1mj");
        String sign = OnlineBankUtils.md5(md5str.toString());
        log.info("==>网银转卡，签名串：{}，签名：{}",md5str.toString(),sign);
        payParam.setPay_md5sign(sign);
        String paramString = JSON.toJSONString(payParam);
        Map jsonObject = JSON.parseObject(paramString);
        log.info("==>请求入参：{}",jsonObject);
        HttpResult result = HttpUtils.doPost("http://23721.wl1g.cn:23721/Pay_Index.html", jsonObject);
        String body = result.getBody();
        System.out.println("返回值："+body);
    }
    @Override
    public boolean orderInfoOk(OrderInfoEntity order, String url, PayBusiness userBusiness) throws Exception {
        return false;
    }

    @Override
    public boolean notifyOrderFinish(OrderInfoEntity order, String key, PayBusiness userBusiness, String url)
        throws Exception {
        return false;
    }

    @Override
    public Object callBack(Object object) throws Exception {
        return null;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        PayServiceFactory.register(BaseConstant.REQUEST_ONLINE_BANK_PAY, this);
        PayServiceFactory.registerUrl(BaseConstant.REQUEST_ONLINE_BANK_PAY, utils.getRequestUrl(BaseConstant.REQUEST_ONLINE_BANK_PAY));
    }
}
