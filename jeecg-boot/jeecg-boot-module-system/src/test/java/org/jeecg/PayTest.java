package org.jeecg;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.codec.digest.DigestUtils;
import org.jeecg.modules.pay.controller.ApiController;
import org.jeecg.modules.util.AES128Util;
import org.jeecg.modules.util.BaseConstant;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.Date;

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
        data.put(BaseConstant.OUTER_ORDER_ID,"abc123456789");
        data.put(BaseConstant.USER_ID,"abc123456789");
        data.put(BaseConstant.SUBMIT_AMOUNT,"100");
        data.put(BaseConstant.PAY_TYPE,"ysf");
        data.put(BaseConstant.CALLBACK_URL,"http://localhost:8080");
        String dataEn = AES128Util.encryptBase64(data.toJSONString(), "abc123#@!");
        StringBuilder sign = new StringBuilder();
        //userId+timestamp+data
        Long time = new Date().getTime();
        sign.append("abc123456789").append(time).append(dataEn);

        req.put(BaseConstant.SIGN, DigestUtils.md5Hex(sign.toString()));
        req.put(BaseConstant.TIMESTAMP,time);
        req.put(BaseConstant.DATA,dataEn);
        req.put(BaseConstant.USER_ID,"abc123456789");
        System.out.println(req.toJSONString());
        api.create(req);
    }

}