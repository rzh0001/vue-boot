package org.jeecg;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.jeecg.modules.pay.controller.ApiController;
import org.jeecg.modules.util.AES128Util;
import org.jeecg.modules.util.BaseConstant;
import org.jeecg.modules.util.R;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.Date;
import java.util.Random;

/**
 * @title:
 * @Description:
 * @author: wangjb
 * @create: 2019-07-30 09:53
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class PayTest {
    @Resource
    private ApiController api;

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;

    @Before
    public void setUp(){
        request = new MockHttpServletRequest();
        request.setCharacterEncoding("UTF-8");
        response = new MockHttpServletResponse();
    }

    @Test
    public void create(){
        JSONObject req = new JSONObject();
        JSONObject data = new JSONObject();
        data.put(BaseConstant.OUTER_ORDER_ID,"1565776105000abc");
        data.put(BaseConstant.USER_NAME,"www");
        data.put(BaseConstant.SUBMIT_AMOUNT,"111");
        data.put(BaseConstant.PAY_TYPE,"ysf");
        data.put(BaseConstant.CALLBACK_URL,"http://localhost/api/callback");
        String dataEn = AES128Util.encryptBase64(data.toJSONString(), "1234123412ABCDEF");
        StringBuilder sign = new StringBuilder();
        //userId+timestamp+data
        Long time = new Date().getTime();
        sign.append("www").append(time).append(dataEn).append("abc123#@!");

        req.put(BaseConstant.SIGN, DigestUtils.md5Hex(sign.toString()));
        req.put(BaseConstant.TIMESTAMP,time);
        req.put(BaseConstant.DATA,dataEn);
        req.put(BaseConstant.USER_NAME,"www");
        System.out.println(req.toJSONString());
        api.create(req);
    }

    @Test
    public void callback(){
        JSONObject data = new JSONObject();
        data.put(BaseConstant.ORDER_ID,"20190806031402975210");
        data.put(BaseConstant.PAY_TYPE,"bank");
        String dataEn = AES128Util.encryptBase64(data.toJSONString(), "abc123#@!");
        JSONObject req = new JSONObject();
        Long time = new Date().getTime();
        req.put(BaseConstant.USER_NAME,"www");
        req.put(BaseConstant.DATA,dataEn);
        req.put(BaseConstant.TIMESTAMP,time);
        StringBuilder sign = new StringBuilder();
        sign.append("www").append(time).append(dataEn).append("abc123#@!");
        req.put(BaseConstant.SIGN, DigestUtils.md5Hex(sign.toString()));
        api.callback(req,request);
    }

    @Test
    public void queryOrderInfo(){
        JSONObject data = new JSONObject();
        data.put(BaseConstant.ORDER_ID,"20190803152520RJzOi");
        String dataEn = AES128Util.encryptBase64(data.toJSONString(), "abc123#@!");
        JSONObject req = new JSONObject();
        Long time = new Date().getTime();
        req.put(BaseConstant.USER_NAME,"www");
        req.put(BaseConstant.DATA,dataEn);
        req.put(BaseConstant.TIMESTAMP,time);
        StringBuilder sign = new StringBuilder();
        sign.append("www").append(time).append(dataEn).append("abc123#@!");
        req.put(BaseConstant.SIGN, DigestUtils.md5Hex(sign.toString()));
        R r = api.queryOrder(req);
        System.out.println(r.get("orderInfo").toString());
    }


    public static void main(String[] args) {
        JSONObject req = new JSONObject();
        JSONObject data = new JSONObject();
        data.put(BaseConstant.OUTER_ORDER_ID,"1565776105000abc");
        data.put(BaseConstant.USER_NAME,"www");
        data.put(BaseConstant.SUBMIT_AMOUNT,"111");
        data.put(BaseConstant.PAY_TYPE,"ysf");
        data.put(BaseConstant.CALLBACK_URL,"http://localhost/api/callback");
        String dataEn = AES128Util.encryptBase64(data.toJSONString(), "1234123412ABCDEF");
        System.out.println(dataEn);
//        StringBuilder sign = new StringBuilder();
//        //userId+timestamp+data
//        Long time = new Date().getTime();
//        sign.append("www").append(time).append(dataEn).append("abc123#@!");
//
//        req.put(BaseConstant.SIGN, DigestUtils.md5Hex(sign.toString()));
//        req.put(BaseConstant.TIMESTAMP,time);
//        req.put(BaseConstant.DATA,dataEn);
//        req.put(BaseConstant.USER_NAME,"www");
//        System.out.println(req.toJSONString());
    }
    @Test
    public void a(){
        JSONObject data = new JSONObject();
        data.put(BaseConstant.OUTER_ORDER_ID,"1565776105000abc");
        data.put(BaseConstant.USER_NAME,"www");
        data.put(BaseConstant.SUBMIT_AMOUNT,"111");
        data.put(BaseConstant.PAY_TYPE,"ysf");
        data.put(BaseConstant.CALLBACK_URL,"http://localhost/api/callback");
        String dataEn = AES128Util.encryptBase64(data.toJSONString(), "1234123412ABCDEF");
        System.out.println(dataEn);
    }


}
