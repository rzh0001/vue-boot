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
    private static final String CALLBACK_URL="/callBack/order/gtpaiAlipay/out_trade_no";
    private static final String store_id = "9999kkf";
    private static final String pay_type = "53";

    @Override
    public R requestPayUrl(OrderInfoEntity order, String userName, String url, String key, String callbackUrl,
        UserBusinessEntity userBusiness) throws Exception {
        GtpaiAlipayParam param = new GtpaiAlipayParam();
        param.setMch_id(userBusiness.getBusinessCode());
        param.setStore_id(store_id);
        param.setPay_type(pay_type);
        param.setBody("alipay");
        param.setTrans_amt(order.getSubmitAmount().toString());
        param.setOut_trade_no(order.getOrderId());
        param.setNotify_url(getDomain()+CALLBACK_URL);
        String paramStr = JSON.toJSONString(param);
        log.info("==>GT派支付支付宝，请求入参为：{}",paramStr);
        TreeMap<String, Object> map =  new TreeMap<String,Object>();
        map.put("body", "alipay");
        map.put("mch_id", userBusiness.getBusinessCode());
        map.put("notify_url", getDomain()+CALLBACK_URL);
        map.put("out_trade_no", order.getOrderId());
        map.put("pay_type", pay_type);
        map.put("store_id", store_id);
        map.put("trans_amt", order.getSubmitAmount().toString());

        String sign = GtpaiUtil.generateSignature(map,userBusiness.getApiKey());

        log.info("==>GT派支付支付宝，请求签名为：{}",sign);
        param.setSign(sign);
        String paramString = JSON.toJSONString(param);
        Map jsonObject = JSON.parseObject(paramString);
        HttpResult result = HttpUtils.doPost(url, jsonObject);
        String body = result.getBody();
        log.info("==>GT派支付支付宝，请求结果为：{}",body);
        //<script>window.location.href='https://gttffp.com:8443/zhifpops/getToPayForAlipay2Person/1497872655967991fc04a9cd10221efa'</script>
        //JSONObject bodyResult = JSON.parseObject(body);
        String[] list = body.split("'");
        String payurl = list[1].split("'")[0];
        //String payUrl = bodyResult.getString("code_url");
        redisUtil.del(order.getOuterOrderId());
        return R.ok().put("url", payurl);

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
