package org.jeecg.pay;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.jeecg.common.util.MD5Util;
import org.jeecg.modules.pay.dto.RequestParamDTO;
import org.jeecg.modules.pay.externalUtils.antUtil.YitongUtil;
import org.jeecg.modules.util.HttpResult;
import org.jeecg.modules.util.HttpUtils;
import org.junit.Test;

import java.util.*;
@Slf4j
public class checkoutCounterTest {

    private String pay_url = "http://154.208.101.55/paya/";

    @Test
    public void pay() throws Exception {
        RequestParamDTO.RequestParamDTOBuilder request =
                RequestParamDTO.builder().Amount("500000").mch_id("1234567356").MsgUrl("http://www.baidu.com").orderNo("Order123456789").return_url("http://www.baidu.com").userid("123");
                        ;
        StringBuilder sign = new StringBuilder();
        sign.append("userid=").append("123")
                .append("&orderNo=").append("Order1234567489331")
                .append("&MsgUrl=").append("http://www.baidu.com")
                .append("&return_url=").append("http://www.baidu.com")
                .append("&mch_id=").append("1234567356")
                .append("&key=").append("abcfhosyFQRTV568");

        String s = DigestUtils.md5Hex(sign.toString()).toUpperCase();
        request.sign(s);


        String paramString = JSON.toJSONString(request.build());
        Map jsonObject = JSON.parseObject(paramString);
        Map<String, Object> data = (Map<String,Object>)jsonObject;
        Map<String, Object> data2 = new HashMap<>();
        data2.put("Amount","500000");
        data2.put("userid","123");
        data2.put("orderNo","Order1234567489331");
        data2.put("MsgUrl","http://www.baidu.com");
        data2.put("return_url","http://www.baidu.com");
        data2.put("mch_id","1234567356");
        data2.put("sign",s);
        data2.put("channel","907");

        HttpResult body =  HttpUtils.doPost(pay_url, data2);
        log.info("请求返还结果：{}",body);
        String result = body.getBody();
        JSONObject json = JSON.parseObject(result);
        JSONObject resultJson = (JSONObject) json.get("result");
        String payUrl = (String)resultJson.get("payurl");
    }


}
