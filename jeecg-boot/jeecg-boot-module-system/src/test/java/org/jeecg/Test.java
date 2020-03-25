package org.jeecg;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.codec.digest.DigestUtils;
import org.jeecg.modules.exception.RRException;
import org.jeecg.modules.pay.entity.BaiyitongParam;
import org.jeecg.modules.pay.entity.OrderInfoEntity;
import org.jeecg.modules.util.AES128Util;
import org.jeecg.modules.util.BaseConstant;
import org.jeecg.modules.util.HttpResult;
import org.jeecg.modules.util.HttpUtils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

/**
 * @title:
 * @Description:
 * @author: wangjb
 * @create: 2019-11-05 16:09
 */

public class Test {
    @org.junit.Test
    public void entry(){
        JSONObject json = new JSONObject();
        json.put("orderId","20200323192459gAAyC");
        String a = AES128Util.encryptBase64(json.toJSONString(),"3c6a3ce41ec44a6c");
        System.out.println(a);

        System.out.println(DigestUtils.md5Hex("NJ081584965437613NP61O2aa7S4mfookaeGOfIFl6XaAQYY913aF9TEfQcAEP73D58LvB/EeEK2NB+Ra3c6a3ce41ec44a6c"));
    }
    private static final String MD5_KEY="UoVoRPwckOZgYWBDZ15kQnzAZeY2140d";
    @org.junit.Test
    public void test1(){
        BigDecimal amount = new BigDecimal("100").setScale(2, RoundingMode.HALF_UP);
        System.out.println(amount.toString());
    }
    @org.junit.Test
    public void test() throws Exception {
        BaiyitongParam param = valueOf(null,null,null);
        String json = JSON.toJSONString(param);
        Map<String,Object> mapTypes = JSON.parseObject(json);
        System.out.println("===>请求百易通，获取支付链接，请求入参为："+json);
        HttpResult r = HttpUtils.doPost("http://api.autosu.cn/gateway/index/unifiedorder?format=json", mapTypes);
        String body = r.getBody();
        String payUrl = null;
        JSONObject result = JSONObject.parseObject(body);
        if("200".equals(result.get("code").toString())){
            JSONObject data  = (JSONObject) result.get("data");
            payUrl = (String)result.get("url").toString();
            System.out.println("===url=="+payUrl);
        }else{
            throw new RRException("设备产码失败，请联系商户");
        }
        System.out.println("===>请求百易通，获取支付链接,请求返回code为：，返回内容为：{}"+r.getBody());
    }

    private BaiyitongParam valueOf(OrderInfoEntity order, String businessCode, String callBackUrl){
        BaiyitongParam param = new BaiyitongParam();
        param.setReturn_type("app");
        param.setAppid("1015518");
        param.setPay_type("wechat");
        param.setAmount("100.00");
        param.setCallback_url("http://www.baidu.com");
        param.setOut_uid(BaseConstant.REQUEST_BAIYITONG_WECHAT);
        param.setOut_trade_no("2021545489632");
        SortedMap<Object,Object> map = new TreeMap<Object,Object>();
        map.put("out_trade_no",param.getOut_trade_no());
        map.put("amount",param.getAmount());
        map.put("appid",param.getAppid());
        map.put("callback_url",param.getCallback_url());
        map.put("out_uid","baiyitong_pay_wechat");
        map.put("pay_type",param.getPay_type());
        map.put("return_type","app");
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
        System.out.println("==>请求百易通挂马平台，生成的签名串为："+sbkey);
        //MD5加密,结果转换为大写字符
        String sign = MD5(sbkey.toString()).toUpperCase();
        System.out.println("==>请求百易通挂马平台，生成的签名为："+sign);
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
        }
        return null;
    }
}
