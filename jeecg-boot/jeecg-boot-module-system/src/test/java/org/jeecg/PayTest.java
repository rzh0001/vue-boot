package org.jeecg;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.codec.digest.DigestUtils;
import org.jeecg.common.util.encryption.AES128Util;
import org.jeecg.modules.pay.controller.ApiController;
import org.jeecg.modules.pay.entity.DianJinPayParam;
import org.jeecg.modules.pay.service.impl.OrderInfoEntityServiceImpl;
import org.jeecg.modules.util.*;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import javax.annotation.Resource;
import java.util.Date;
import java.util.Map;

/**
 * @title:
 * @Description:
 * @author: wangjb
 * @create: 2019-07-30 09:53
 */
//@RunWith(SpringRunner.class)
//@SpringBootTest
public class PayTest {
    @Resource
    private ApiController api;
    @Resource
    //private RedisController redis;
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
    public void redisTest(){
        for(int i=0;i<10;i++){
            Thread h = new Thread(
                    new Runnable() {
                        @Override
                        public void run() {
                            //redis.test();
                        }
                    }
            );
            h.run();
        }
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
        data.put(BaseConstant.ORDER_ID,"20191202222707GuEGZ");
        data.put(BaseConstant.PAY_TYPE,"ali_bank");
        String dataEn = AES128Util.encryptBase64(data.toJSONString(), "Pfe9jM2oV8zGWS9a");
        JSONObject req = new JSONObject();
        Long time = new Date().getTime();
        req.put(BaseConstant.USER_NAME,"xiaoyao");
        req.put(BaseConstant.DATA,dataEn);
        req.put(BaseConstant.TIMESTAMP,time);
        StringBuilder sign = new StringBuilder();
        sign.append("xiaoyao").append(time).append(dataEn).append("Pfe9jM2oV8zGWS9a");
        req.put(BaseConstant.SIGN, DigestUtils.md5Hex(sign.toString()));
        System.out.println(req.toJSONString());
        api.callback();
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
        System.out.println(req.toJSONString());
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

    @Test
    public void dianPayTest() throws Exception {
        String key = "44843f1629e8b1142636fd799fb2e373b1feb096eb79bdcbeba8be8b1a65e752a2d08b88e0e0732a3b6a5f5572b1f7464e11f769d140d2675c74f9cdc99cfd2f1d33ecb9d0ffdb45df2e1665678c788c1a6ce5b69e539fdfb6c1daef8703c1e2";
        String url ="http://47.240.10.174/api/order/CreateOrderUrl";
        DianJinPayParam param = new DianJinPayParam();
        param.setUid("6");
        param.setMerchantTransNo("123456789Test");
        param.setTotalAmount("500.00");
        param.setPaymentType("1");
        param.setRemark("qiPayAlipay");
        StringBuilder sign = new StringBuilder();
        sign.append("merchantTransNo=").append(param.getMerchantTransNo())
            .append("&").append("paymentType=").append(param.getPaymentType())
            .append("&").append("totalAmount=").append(param.getTotalAmount())
            .append("&").append("uid=").append(param.getUid())
            .append("&").append("key=").append(key);
        System.out.println("签名值sign="+sign.toString());
        System.out.println("签名值sign的MD5值="+DigestUtils.md5Hex(sign.toString()));
        param.setSign(DigestUtils.md5Hex(sign.toString()));
        String jsonstring = JSON.toJSONString(param);
        Map map = JSON.parseObject(jsonstring);
        System.out.println("请求入参为："+map);
        HttpResult result = HttpUtils.doPost(url, map);
        System.out.println(result.getBody());
        String body = result.getBody();
        if(body.contains("href")){
            String[] htmls = body.split("href=\"");
            String urlpay = htmls[1].split("\"")[0];
            System.out.println(urlpay);
        }
    }

}
