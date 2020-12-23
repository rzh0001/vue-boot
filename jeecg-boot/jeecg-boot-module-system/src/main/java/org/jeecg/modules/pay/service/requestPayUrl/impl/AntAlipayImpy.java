package org.jeecg.modules.pay.service.requestPayUrl.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.jeecg.common.system.vo.DictModel;
import org.jeecg.common.util.IPUtils;
import org.jeecg.common.util.RedisUtil;
import org.jeecg.common.util.UUIDGenerator;
import org.jeecg.modules.pay.entity.AntAlipayParam;
import org.jeecg.modules.pay.entity.OrderInfoEntity;
import org.jeecg.modules.pay.entity.UserBusinessEntity;
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
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @Author: wangjianbin
 * @Date: 2020/4/17 21:13
 */
@Service
@Slf4j
public class AntAlipayImpy implements
    RequestPayUrl<OrderInfoEntity, String, String, String, String, PayBusiness, Object, HttpServletResponse>, InitializingBean {
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private RequestUrlUtils utils;
    @Autowired
    public ISysDictService dictService;
    private static final String CALLBACK_URL="/callBack/order/antAlipay/out_trade_no";
    @Override
    public R requestPayUrl(OrderInfoEntity order, String userName, String url, String key, String callbackUrl,
                           PayBusiness userBusiness,HttpServletResponse response) throws Exception {
        AntAlipayParam param = new AntAlipayParam();
        param.setApp_id(userBusiness.getBusinessCode());
        param.setNonce_str(UUIDGenerator.generate());
        param.setTrade_type("ali_qr");
        param.setTotal_amount(order.getSubmitAmount().multiply(new BigDecimal("100")).stripTrailingZeros().toPlainString());
        param.setOut_trade_no(order.getOrderId());
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        param.setTrade_time(df.format(new Date()));
        param.setNotify_url(getDomain()+CALLBACK_URL);
        param.setUser_ip("127.0.0.1");
        String paramStr = JSON.toJSONString(param);
        log.info("==>蚁支付支付宝，请求入参为：{}",paramStr);
        Map jsonObject = JSON.parseObject(paramStr);
        String sign = this.generateSignature(jsonObject,userBusiness.getBusinessApiKey());
        log.info("==>蚁支付支付宝，请求签名为：{}",sign);
        param.setSign(sign);
        HttpResult result = HttpUtils.doPostJson(url, JSON.toJSONString(param));
        String body = result.getBody();
        log.info("==>蚁支付支付宝，请求结果为：{}",body);
        JSONObject bodyResult = JSON.parseObject(body);
        String payUrl = bodyResult.getString("code_url");
        //String payUrl = bodyResult.getString("code_wap");
        redisUtil.del(order.getOuterOrderId());
        return R.ok().put("url", payUrl);
    }

//    public static void main(String[] args) throws Exception {
//        AntAlipayParam param = new AntAlipayParam();
//        param.setApp_id("8000000237841949");
//        param.setNonce_str(UUIDGenerator.generate());
//        param.setTrade_type("ali_qr");
//        param.setTotal_amount("300000");
//        param.setOut_trade_no("123456789");
//        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        param.setTrade_time(df.format(new Date()));
//        param.setNotify_url("http://www.baidu.com");
//        param.setUser_ip("127.0.0.1");
//        String paramStr = JSON.toJSONString(param);
//        log.info("==>蚁支付支付宝，请求入参为：{}",paramStr);
//        Map jsonObject = JSON.parseObject(paramStr);
//        String sign = generateSignature(jsonObject,"1d4b7a7478dc45c8ab3babbbd34d51ba");
//        log.info("==>蚁支付支付宝，请求签名为：{}",sign);
//        param.setSign(sign);
//        HttpResult result = HttpUtils.doPostJson("https://api.letme8.cn/pay/order", JSON.toJSONString(param));
//        String body = result.getBody();
//    }
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
    public  String generateSignature(final Map<String, String> params, final String key) throws Exception {
        String paramUrl = formatUrlMap(params, true, false, false) + "&key=" + key;
        log.info("==》签名串为：{}",paramUrl);
        return MD5(paramUrl).toUpperCase();
    }
    public  String MD5(String data) throws Exception {
        java.security.MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] array = md.digest(data.getBytes("UTF-8"));
        StringBuilder sb = new StringBuilder();
        for (byte item : array) {
            sb.append(Integer.toHexString((item & 0xFF) | 0x100).substring(1, 3));
        }
        return sb.toString().toUpperCase();
    }
    public  String formatUrlMap(Map<String, String> paraMap, boolean removeEmptyValue, boolean urlEncode, boolean keyToLower) {
        String buff = "";
        Map<String, String> tmpMap = paraMap;
        //开启空值筛选，则移除数据
        try {
            List<Map.Entry<String, String>> infoIds = new ArrayList<Map.Entry<String, String>>(tmpMap.entrySet());
            // 对所有传入参数按照字段名的 ASCII 码从小到大排序（字典序）
            Collections.sort(infoIds, new Comparator<Map.Entry<String, String>>() {
                @Override
                public int compare(Map.Entry<String, String> o1, Map.Entry<String, String> o2) {
                    return (o1.getKey()).toString().compareTo(o2.getKey());
                }
            });
            // 构造URL 键值对的格式
            StringBuilder buf = new StringBuilder();
            for (Map.Entry<String, String> item : infoIds) {
                if (StringUtils.isNotBlank(item.getKey())) {
                    String key = item.getKey();
                    String val = item.getValue();
                    if (removeEmptyValue && StringUtils.isBlank(val)) {
                        continue;
                    }
                    if (urlEncode) {
                        val = URLEncoder.encode(val, "utf-8");
                    }
                    if (keyToLower) {
                        key = key.toLowerCase();
                    }
                    buf.append(key + "=" + val).append("&");
                }
            }
            buff = buf.toString();
            if (buff.isEmpty() == false) {
                buff = buff.substring(0, buff.length() - 1);
            }
        } catch (Exception e) {
            log.info("异常信息为：{}",e);
            return "";
        }
        return buff;
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
        PayServiceFactory.register(BaseConstant.REQUEST_ANT_ALIPAY, this);
        PayServiceFactory.registerUrl(BaseConstant.REQUEST_ANT_ALIPAY, utils.getRequestUrl(BaseConstant.REQUEST_ANT_ALIPAY));
    }
}
