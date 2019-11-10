package org.jeecg;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.jeecg.modules.pay.controller.ApiController;
import org.jeecg.modules.pay.service.impl.OrderInfoEntityServiceImpl;
import org.jeecg.modules.util.AES128Util;
import org.jeecg.modules.util.BareBonesBrowserLaunch;
import org.jeecg.modules.util.BaseConstant;
import org.jeecg.modules.util.R;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private OrderInfoEntityServiceImpl order;
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
        data.put(BaseConstant.ORDER_ID,"1565608382003");
        data.put(BaseConstant.PAY_TYPE,"ysf");
        String dataEn = AES128Util.encryptBase64(data.toJSONString(), "abc123#@!");
        JSONObject req = new JSONObject();
        Long time = new Date().getTime();
        req.put(BaseConstant.USER_NAME,"www");
        req.put(BaseConstant.DATA,dataEn);
        req.put(BaseConstant.TIMESTAMP,time);
        StringBuilder sign = new StringBuilder();
        sign.append("www").append(time).append(dataEn).append("abc123#@!");
        req.put(BaseConstant.SIGN, DigestUtils.md5Hex(sign.toString()));
//        api.callback(req,request);
    }

    @Test
    public void queryOrderInfo(){
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
        R r = api.queryOrder(req);
        System.out.println(r.get("orderInfo").toString());
    }
    @Test
    public void testAmount() throws Exception {
        order.countAmount("20190913123844IiNeL","a7_putong","100","ali_bank");
    }

    public static void main(String[] args) {
        BareBonesBrowserLaunch.openURL("http://www.baidu.com");
    }
    @Test
    public void a(){
//        JSONObject data = new JSONObject();
//        data.put(BaseConstant.OUTER_ORDER_ID,"HY19092222008184403");
//        data.put(BaseConstant.USER_NAME,"hc001");
//        data.put(BaseConstant.SUBMIT_AMOUNT,"1000.000");
//        data.put(BaseConstant.PAY_TYPE,"wechat_bank");
//        data.put(BaseConstant.CALLBACK_URL,"http://localhost:60997/Notify/JinChanPayNotify");
//        String dataEn = AES128Util.encryptBase64(data.toJSONString(), "5787731c4d684c29");
       String data = "oOiP7Z8hAXyNifhyg9DX4fiAF4mINZKp6mlb9lpz7k9e04SdPh5rEfZnl5/X+zJJ8tzFfMZHiy7oh//rVyPrJcYIUTPJ4oJCIe2I1zstPI7ck5hZOlaMKjZ4SwRmiM+IIk3H0xTHCQtXIf2ZDiMpNA==";
        String a = AES128Util.decryptBase64(data, "15525d49bbe64a2c");
        System.out.println(a);
    }


}
