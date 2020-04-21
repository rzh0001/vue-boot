package org.jeecg.modules.pay.service.requestPayUrl.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.jeecg.common.system.vo.DictModel;
import org.jeecg.common.util.RedisUtil;
import org.jeecg.common.util.UUIDGenerator;
import org.jeecg.modules.pay.entity.GtpaiAlipayParam;
import org.jeecg.modules.pay.entity.OrderInfoEntity;
import org.jeecg.modules.pay.entity.UserBusinessEntity;
import org.jeecg.modules.pay.externalUtils.antUtil.GtpaiUtil;
import org.jeecg.modules.pay.service.factory.PayServiceFactory;
import org.jeecg.modules.pay.service.requestPayUrl.RequestPayUrl;
import org.jeecg.modules.system.service.ISysDictService;
import org.jeecg.modules.util.BaseConstant;
import org.jeecg.modules.util.HttpResult;
import org.jeecg.modules.util.HttpUtils;
import org.jeecg.modules.util.R;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @Author: heihei
 * @Date: 2020/4/20s 21:13
 */
@Service
@Slf4j
public class GtpaiAlipayImpy implements
    RequestPayUrl<OrderInfoEntity, String, String, String, String, UserBusinessEntity, Object>, InitializingBean {
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private RequestUrlUtils utils;
    @Autowired
    public ISysDictService dictService;
    private static final String CALLBACK_URL="/callBack/gtpaiAlipayCallback";
    private static final String pay_html = "<html><head><title></title><script language=\"JavaScript\" type=\"text/JavaScript\">  function doSubmit() {document.toJiupai.submit(); }</script><meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" />    	</head>    <body onload=\"doSubmit()\"> 	 <form name=\"toJiupai\" id=\"toJiupai\" action=\"http://shsuperhero.com:8089/ckpayops/gatewayPayment\" method=\"post\"> 	 		<table><input type=\"hidden\" value=\"mch_id_temp\" name=\"mch_id\"/><input type=\"hidden\" value=\"store_id_temp\" name=\"store_id\"/><input type=\"hidden\" value=\"pay_type_temp\" name=\"pay_type\"/><input type=\"hidden\" value=\"trans_amt_temp\" name=\"trans_amt\"/><input type=\"hidden\" value=\"bank_english_code_temp\" name=\"bank_english_code\"/><input type=\"hidden\" value=\"card_type_temp\" name=\"card_type\"/><input type=\"hidden\" value=\"out_trade_no_temp\" name=\"out_trade_no\"/><input type=\"hidden\" value=\"notify_url_temp\" name=\"notify_url\"/> <input type=\"hidden\" value=\"body_temp\" name=\"body\"/><input type=\"hidden\" value=\"sign_temp\" name=\"sign\"/></table></form></body></html>";
    private static final String store_id = "9999kkf";
    private static final String pay_type = "53";

    @Override
    public R requestPayUrl(OrderInfoEntity order, String userName, String url, String key, String callbackUrl,
        UserBusinessEntity userBusiness) throws Exception {
        GtpaiAlipayParam param = new GtpaiAlipayParam();
        param.setMch_id(userBusiness.getBusinessCode());
        param.setStore_id(store_id);
        param.setPay_type(pay_type);
        param.setBody("");
        param.setTrans_amt(order.getSubmitAmount().multiply(new BigDecimal("100")).toString());
        param.setOut_trade_no(order.getOrderId());
        param.setNotify_url(getDomain()+CALLBACK_URL);
        String paramStr = JSON.toJSONString(param);
        log.info("==>GT派支付支付宝，请求入参为：{}",paramStr);
        TreeMap<String, String> map =  new TreeMap<String,String>();
        map.put("body", "");
        map.put("mch_id", userBusiness.getBusinessCode());
        map.put("notify_url", getDomain()+CALLBACK_URL);
        map.put("out_trade_no", order.getOrderId());
        map.put("pay_type", pay_type);
        map.put("store_id", store_id);//0000000
        map.put("trans_amt", order.getSubmitAmount().multiply(new BigDecimal("100")).toString());

        String sign = GtpaiUtil.generateSignature(map,userBusiness.getApiKey());

        log.info("==>GT派支付支付宝，请求签名为：{}",sign);
        param.setSign(sign);

        Map<String, Object> mapTmp = (Map<String, Object>) param;
        HttpResult result = HttpUtils.doPost(url, mapTmp);
        String body = result.getBody();
        log.info("==>GT派支付支付宝，请求结果为：{}",body);
        JSONObject bodyResult = JSON.parseObject(body);
        String payUrl = bodyResult.getString("code_url");
        redisUtil.del(order.getOuterOrderId());
        return R.ok().put("url", payUrl);


        /*String submitHtml = pay_html;
        submitHtml = StringUtils.replace(submitHtml, "mch_id_temp", param.getMch_id());
        submitHtml = StringUtils.replace(submitHtml, "store_id_temp", store_id);
        submitHtml = StringUtils.replace(submitHtml, "pay_type_temp", pay_type);
        submitHtml = StringUtils.replace(submitHtml, "trans_amt_temp", param.getTrans_amt());
        submitHtml = StringUtils.replace(submitHtml, "bank_english_code_temp", "");
        submitHtml = StringUtils.replace(submitHtml, "card_type_temp", "");
        submitHtml = StringUtils.replace(submitHtml, "out_trade_no_temp", param.getOut_trade_no());
        submitHtml = StringUtils.replace(submitHtml, "notify_url_temp", param.getNotify_url());
        submitHtml = StringUtils.replace(submitHtml, "body_temp", param.getBody());
        submitHtml = StringUtils.replace(submitHtml, "sign_temp", sign);

        PrintWriter out = resp.getWriter();
        out.println(submitHtml);
        out.flush();
        out.close();

        return  R.ok().put("url", "");*/
    }

    private String getDomain(){
        String domain = null;
        List<DictModel> apiKey = dictService.queryDictItemsByCode(BaseConstant.DOMAIN);
        for (DictModel k : apiKey) {
            if (BaseConstant.DOMAIN.equals(k.getText())) {
                domain = k.getValue();
            }
        }
        return domain;
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
        PayServiceFactory.register(BaseConstant.REQUEST_GTPAI_ALIPAY, this);
        PayServiceFactory.registerUrl(BaseConstant.REQUEST_GTPAI_ALIPAY, utils.getRequestUrl(BaseConstant.REQUEST_GTPAI_ALIPAY));
    }
}
