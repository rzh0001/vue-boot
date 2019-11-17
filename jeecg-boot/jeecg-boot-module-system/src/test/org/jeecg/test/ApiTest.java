package org.jeecg.test;

import org.jeecg.modules.df.entity.CommonRequestBody;
import org.jeecg.modules.df.entity.PayOrderData;

import java.math.BigDecimal;

/**
 * @author ruanzh
 * @since 2019/11/17
 */
public class ApiTest {
    
    public static void main(String[] args) {
        //
        PayOrderData data = new PayOrderData();
        data.setAccountType("1");
        data.setAccountName("王二");
        data.setAccountNo("1231241241");
        data.setProductCode("alipay");
        data.setBizOrderNo("3333333333333333");
        data.setAmount(BigDecimal.TEN);
        data.setCallbackUrl("q3");
        data.setIp("1.1.1.1");
        data.setRemark("sssssss");
        data.setBankCode("31211212211");
        
        String apiKey = "53e6f5cc060a4adf";
        System.out.println(data.toJsonString());
        System.out.println(data.encodeData(apiKey));
        
        CommonRequestBody req = new CommonRequestBody();
        req.setUsername("shanghu");
        req.setTimestamp(System.currentTimeMillis());
        req.setData(data.encodeData(apiKey));
        req.setSign(req.sign(apiKey));
        System.out.println(req.toJsonString());
    }
}
