package org.jeecg.demo;

import com.alibaba.fastjson.JSON;
import org.apache.commons.codec.digest.DigestUtils;
import org.jeecg.modules.pay.entity.XinPayParam;
import org.jeecg.modules.util.HttpResult;
import org.jeecg.modules.util.HttpUtils;
import org.junit.Test;

import java.util.Map;

public class XinPayTest {

    @Test
    public void test() throws Exception {
        XinPayParam payParam = new XinPayParam();
        payParam.setMerchantNum("a7999");
        payParam.setOrderNo("123456789");
        payParam.setAmount("100");
        payParam.setNotifyUrl("http://www.baidu.com");
        payParam.setReturnUrl("");
        payParam.setPayType("alipay");
        payParam.setAttch("test");
        StringBuilder sign = new StringBuilder();
        sign.append(payParam.getMerchantNum()).append(payParam.getOrderNo()).append(payParam.getAmount()).append(payParam.getNotifyUrl()).append("cc9d205602f87165866458eb79f1a254");
        System.out.println("===>请求信付支付平台，获取支付链接，签名串为：{}"+sign.toString());
        String signStr = DigestUtils.md5Hex(sign.toString());
        System.out.println("===>请求信付支付平台，获取支付链接，签名sign为：{}"+signStr);
        payParam.setSign(signStr);
        String json = JSON.toJSONString(payParam);
        Map<String,Object> mapTypes = JSON.parseObject(json);
        System.out.println("===>请求信付支付平台，获取支付链接，入参为：{}"+mapTypes);

        HttpResult r = HttpUtils.doPost("http://47.52.203.28/api/startOrder", mapTypes);
        System.out.println("===>请求信付支付平台，获取支付链接，返回结果为：{}"+r.getBody());
    }
}
