package org.jeecg.modules.pay.service.requestPayUrl.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.system.vo.DictModel;
import org.jeecg.common.util.RedisUtil;
import org.jeecg.modules.pay.entity.OrderInfoEntity;
import org.jeecg.modules.pay.entity.UserBusinessEntity;
import org.jeecg.modules.pay.entity.YitongAlipayParam;
import org.jeecg.modules.pay.externalUtils.antUtil.YitongUtil;
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

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.MessageDigest;
import java.util.*;

/**
 * @Author: heihei
 * @Date: 2020/4/20s 21:13
 */
@Service
@Slf4j
public class YitongAlipayImpy implements
    RequestPayUrl<OrderInfoEntity, String, String, String, String, UserBusinessEntity, Object>, InitializingBean {
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private RequestUrlUtils utils;
    @Autowired
    public ISysDictService dictService;
    private static final String CALLBACK_URL="/callBack/yitongAlipayCallback";

    @Override
    public R requestPayUrl(OrderInfoEntity order, String userName, String url, String key, String callbackUrl,
        UserBusinessEntity userBusiness) throws Exception {
        YitongAlipayParam param = new YitongAlipayParam();
        param.setMch_id(userBusiness.getBusinessCode());
        param.setPtype("1");
        param.setFormat("json");
        String strTime = getUTCTimeStr();
        BigDecimal strMoney = new BigDecimal(order.getSubmitAmount().toString()).setScale(2, RoundingMode.HALF_UP);
        param.setTime(strTime);
        param.setMoney(strMoney.toString());
        param.setOrder_sn(order.getOrderId());
        param.setNotify_url(getDomain()+CALLBACK_URL);
        String paramStr = JSON.toJSONString(param);
        log.info("==>易通支付支付宝，请求入参为：{}",paramStr);
        TreeMap<String, Object> map =  new TreeMap<String,Object>();
        map.put("client_ip", "127.0.0.1");
        map.put("extend_one", "");
        map.put("extend_two", "");
        map.put("format", "json");
        map.put("goods_desc", "goods");
        map.put("mch_id", userBusiness.getBusinessCode());
        map.put("money", strMoney.toString());
        map.put("notify_url", getDomain()+CALLBACK_URL);
        map.put("order_sn", order.getOrderId());
        map.put("ptype", "1");
        map.put("time", strTime);

        String sign = YitongUtil.generateSignature(map,userBusiness.getApiKey());

        log.info("==>易通支付支付宝，请求签名为：{}",sign);
        param.setSign(sign);
        String paramString = JSON.toJSONString(param);
        Map jsonObject = JSON.parseObject(paramString);
        Map<String, String> data = (Map<String,String>)jsonObject;
        String body = HttpUtils.doGet(url, data);
        log.info("==>易通支付支付宝，请求的url为：{} 请求结果为：{}",url, body);
        JSONObject bodyResult = JSON.parseObject(body);
        String strData = bodyResult.getString("data");
        String strCode = bodyResult.getString("code");
        if (strCode.equals("1")){
            log.info("==>易通下单返回失败，返回码：{}",strCode);
            return R.ok().put("url", "");
        }
        JSONObject bodyData = JSON.parseObject(strData);
        String strOrderSn = bodyData.getString("order_sn");
        String payurl = url + "&a=info&osn=" + strOrderSn;
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
        PayServiceFactory.register(BaseConstant.REQUEST_YITONG_ALIPAY, this);
        PayServiceFactory.registerUrl(BaseConstant.REQUEST_YITONG_ALIPAY, utils.getRequestUrl(BaseConstant.REQUEST_YITONG_ALIPAY));
    }

    public String getUTCTimeStr() throws Exception {
        Calendar cal = Calendar.getInstance();
        TimeZone tz = TimeZone.getTimeZone("GMT");
        cal.setTimeZone(tz);
        long lMillTime = cal.getTimeInMillis();
        long lSecondTime = lMillTime/1000;
        return String.valueOf(lSecondTime);// 返回的UTC时间戳
    }

}
