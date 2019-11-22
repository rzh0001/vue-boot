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
        payParam.setMerchantNum("7484865");
        payParam.setOrderNo("1234567891");
        payParam.setAmount("300");
        payParam.setNotifyUrl("http://www.baidu.com");
        payParam.setReturnUrl("");
        payParam.setPayType("alipay");
        payParam.setAttch("test");
        StringBuilder sign = new StringBuilder();
        sign.append(payParam.getMerchantNum()).append(payParam.getOrderNo()).append(payParam.getAmount()).append(payParam.getNotifyUrl()).append("93a6c71e361c04b8ca275e32fbd52018");
        System.out.println("===>请求信付支付平台，获取支付链接，签名串为：{}"+sign.toString());
        String signStr = DigestUtils.md5Hex(sign.toString());
        System.out.println("===>请求信付支付平台，获取支付链接，签名sign为：{}"+signStr);
        payParam.setSign(signStr);
        String json = JSON.toJSONString(payParam);
        Map<String,Object> mapTypes = JSON.parseObject(json);
        System.out.println("===>请求信付支付平台，获取支付链接，入参为：{}"+mapTypes);

        HttpResult r = HttpUtils.doPost("http://www.nmjianzhi.com/api/startOrder", mapTypes);
        System.out.println("===>请求信付支付平台，获取支付链接，返回结果为：{}"+r.getBody());
    }
}
