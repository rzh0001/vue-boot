package org.jeecg;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.jeecg.modules.pay.controller.ApiController;
import org.jeecg.modules.util.AES128Util;
import org.jeecg.modules.util.BaseConstant;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
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

    @Test
    public void create(){
        JSONObject req = new JSONObject();
        JSONObject data = new JSONObject();
        data.put(BaseConstant.OUTER_ORDER_ID,"out_id123456789");
        data.put(BaseConstant.USER_NAME,"www");
        data.put(BaseConstant.SUBMIT_AMOUNT,"100");
        data.put(BaseConstant.PAY_TYPE,"ysf");
        data.put(BaseConstant.CALLBACK_URL,"http://localhost:8080");
        String dataEn = AES128Util.encryptBase64(data.toJSONString(), "abc123#@!");
        StringBuilder sign = new StringBuilder();
        //userId+timestamp+data
        Long time = new Date().getTime();
        sign.append("www").append(time).append(dataEn);

        req.put(BaseConstant.SIGN, DigestUtils.md5Hex(sign.toString()));
        req.put(BaseConstant.TIMESTAMP,time);
        req.put(BaseConstant.DATA,dataEn);
        req.put(BaseConstant.USER_NAME,"www");
        System.out.println(req.toJSONString());
        api.create(req);
    }

    public static void main(String[] args) {
        JSONObject req = new JSONObject();
        JSONObject data = new JSONObject();
        data.put(BaseConstant.OUTER_ORDER_ID,"out_id123456789");
        data.put(BaseConstant.USER_NAME,"www");
        data.put(BaseConstant.SUBMIT_AMOUNT,"100");
        data.put(BaseConstant.PAY_TYPE,"ysf");
        data.put(BaseConstant.CALLBACK_URL,"http://localhost:8080");
        String dataEn = AES128Util.encryptBase64(data.toJSONString(), "abc123#@!");
        StringBuilder sign = new StringBuilder();
        //userId+timestamp+data
        Long time = new Date().getTime();
        sign.append("www").append(time).append(dataEn).append("abc123#@!");

        req.put(BaseConstant.SIGN, DigestUtils.md5Hex(sign.toString()));
        req.put(BaseConstant.TIMESTAMP,time);
        req.put(BaseConstant.DATA,dataEn);
        req.put(BaseConstant.USER_NAME,"www");
        System.out.println(req.toJSONString());
    }
    @Test
    public void a(){
        System.out.println(RandomStringUtils.randomAlphabetic(10));
    }


}
