package org.jeecg.demo.java;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.codec.digest.DigestUtils;
import org.jeecg.demo.HttpResult;
import org.jeecg.demo.HttpUtils;
import org.jeecg.common.util.encryption.AES128Util;
import org.jeecg.modules.util.BaseConstant;
import org.junit.Test;

import java.util.Date;

/**
 * @title:
 * @Description:
 * @author: wangjb
 * @create: 2019-08-21 16:53
 */
public class JaveDemo {
    /**
     * 创建订单
     */
    @Test
    public void create() throws Exception {
        JSONObject req = new JSONObject();
        JSONObject data = new JSONObject();
        data.put(BaseConstant.OUTER_ORDER_ID,"1565776105000abc");
        data.put(BaseConstant.USER_NAME,"www");
        data.put(BaseConstant.SUBMIT_AMOUNT,"111");
        data.put(BaseConstant.PRODUCT_NAME,"ysf");
        data.put(BaseConstant.CALLBACK_URL,"http://localhost/api/callback");
        //加密数据
        String dataEn = AES128Util.encryptBase64(data.toJSONString(), "1234123412ABCDEF");
        StringBuilder sign = new StringBuilder();
        //userId+timestamp+data
        Long time = new Date().getTime();
        sign.append("www").append(time).append(dataEn).append("1234123412ABCDEF");

        req.put(BaseConstant.SIGN, DigestUtils.md5Hex(sign.toString()));
        req.put(BaseConstant.TIMESTAMP,time);
        req.put(BaseConstant.DATA,dataEn);
        req.put(BaseConstant.USER_NAME,"www");
        System.out.println(req.toJSONString());
        HttpResult result = HttpUtils.doPostJson("http://www.jcokpay.com/jeecg-boot/api/create",req.toJSONString());
        JSONObject r = JSON.parseObject(result.getBody());
        if("0".equals(r.get("code").toString())){
            String payUrl = r.get("url").toString();
        }
    }

    /**
     * 订单查询
     * @throws Exception
     */
    @Test
    public void queryOrderInfo() throws Exception {
        JSONObject data = new JSONObject();
        data.put(BaseConstant.ORDER_ID,"20190817172444SIGKq");
        String dataEn = AES128Util.encryptBase64(data.toJSONString(), "1234123412ABCDEF");
        JSONObject req = new JSONObject();
        Long time = new Date().getTime();
        req.put(BaseConstant.USER_NAME,"www");
        req.put(BaseConstant.DATA,dataEn);
        req.put(BaseConstant.TIMESTAMP,time);
        StringBuilder sign = new StringBuilder();
        sign.append("www").append(time).append(dataEn).append("1234123412ABCDEF");
        req.put(BaseConstant.SIGN, DigestUtils.md5Hex(sign.toString()));
        HttpResult result = HttpUtils.doPostJson("http://www.jcokpay.com/jeecg-boot/api/queryOrder",req.toJSONString());
        JSONObject r = JSON.parseObject(result.getBody());
        if("0".equals(r.get("code").toString())){
            String info = r.get("orderInfo").toString();
            JSONObject orderInfo = JSON.parseObject(info);
            orderInfo.get("status");
            orderInfo.get("submitAmount");
            orderInfo.get("orderId");
            orderInfo.get("outerOrderId");
        }
    }
}
