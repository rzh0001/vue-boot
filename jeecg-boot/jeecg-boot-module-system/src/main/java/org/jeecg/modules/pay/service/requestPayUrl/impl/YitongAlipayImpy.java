package org.jeecg.modules.pay.service.requestPayUrl.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.system.vo.DictModel;
import org.jeecg.common.util.RedisUtil;
import org.jeecg.modules.pay.entity.OrderInfoEntity;
import org.jeecg.modules.pay.entity.YitongAlipayParam;
import org.jeecg.modules.pay.externalUtils.antUtil.YitongUtil;
import org.jeecg.modules.pay.service.factory.PayServiceFactory;
import org.jeecg.modules.pay.service.requestPayUrl.RequestPayUrl;
import org.jeecg.modules.system.service.ISysDictService;
import org.jeecg.modules.util.BaseConstant;
import org.jeecg.modules.util.HttpUtils;
import org.jeecg.modules.util.R;
import org.jeecg.modules.v2.entity.PayBusiness;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

/**
 * @Author: heihei
 * @Date: 2020/4/20s 21:13
 */
@Service
@Slf4j
public class YitongAlipayImpy implements
        RequestPayUrl<OrderInfoEntity, String, String, String, String, PayBusiness, Object>, InitializingBean {
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private RequestUrlUtils utils;
    @Autowired
    public ISysDictService dictService;
    private static final String CALLBACK_URL="/callBack/order/yitongAlipay/sh_order";

    @Override
    public R requestPayUrl(OrderInfoEntity order, String userName, String url, String key, String callbackUrl,
        PayBusiness userBusiness) throws Exception {
        YitongAlipayParam param = new YitongAlipayParam();
        param.setMch_id(userBusiness.getBusinessCode());
        param.setPtype("1");
        param.setFormat("json");
        String strTime = getUTCTimeStr();
        BigDecimal strMoney = order.getSubmitAmount().setScale(2, RoundingMode.HALF_UP);
        param.setTime(strTime);
        param.setClient_ip("127.0.0.1");
        param.setGoods_desc("goods");
        param.setMoney(strMoney.toString());
        param.setOrder_sn(order.getOrderId());
        param.setNotify_url(getDomain()+CALLBACK_URL);
        String paramStr = JSON.toJSONString(param);
        log.info("==>易通支付支付宝，请求入参为：{}",paramStr);
        TreeMap<String, Object> map =  new TreeMap<String,Object>();
        map.put("mch_id", param.getMch_id());
        map.put("ptype", param.getPtype());
        map.put("order_sn",param.getOrder_sn());
        map.put("money", param.getMoney());
        map.put("goods_desc",param.getGoods_desc());
        map.put("client_ip", param.getClient_ip());
        map.put("format", param.getFormat());
        map.put("notify_url", param.getNotify_url());
        map.put("time", param.getTime());

        String sign = YitongUtil.generateSignature(map,userBusiness.getBusinessApiKey());
        log.info("==>易通支付支付宝，请求签名为：{}",sign);
        param.setSign(sign);
        String paramString = JSON.toJSONString(param);
        Map jsonObject = JSON.parseObject(paramString);
        Map<String, String> data = (Map<String,String>)jsonObject;
        String body = HttpUtils.doGet(url, data);
        log.info("==>易通支付支付宝，请求的url为：{} 请求结果为：{}",url, body);
        //{"code":"1","msg":"下单成功",
        // "data":
        // {"order_sn":"MS2020043011244261339","ptype":"1","ptype_name":"支付宝扫码","realname":"施芳菊","account":"y1lijianming@163.com","money":"500.00","bank":"","qrcode":"http:\/\/pay.ccloudpay.com\/uploads\/qr\/20200430\/d88ce6e53be66c17.jpg"}}
        JSONObject bodyResult = JSON.parseObject(body);
        String strData = bodyResult.getString("data");
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
    public boolean orderInfoOk(OrderInfoEntity order, String url, PayBusiness userBusiness)
        throws Exception {
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
