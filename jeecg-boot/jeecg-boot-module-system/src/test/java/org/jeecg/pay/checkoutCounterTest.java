package org.jeecg.pay;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.pay.dto.RequestParamDTO;
import org.jeecg.modules.pay.externalUtils.antUtil.YitongUtil;
import org.jeecg.modules.util.HttpResult;
import org.jeecg.modules.util.HttpUtils;
import org.junit.Test;

import java.util.*;
@Slf4j
public class checkoutCounterTest {

    private String pay_url = "http://payqqsh001.payto89.com/order/placeForIndex";

    private String key = "428f88b71f834ba289202a036d93afea";

    @Test
    public void pay() throws Exception {
        RequestParamDTO.RequestParamDTOBuilder request =
                RequestParamDTO.builder().client_ip("127.0.0.1")
                        .format("page").goods_desc("测试下单").mch_id("213504930")
                        .money("100").notify_url("http://www.baidu.com")
                        .order_sn("order123456789").time(String.valueOf(System.currentTimeMillis()));
        TreeMap<String, Object> map =  new TreeMap<String,Object>();
        map.put("mch_id","213504930");
        map.put("order_sn","order123456789");
        map.put("money","100");
        map.put("goods_desc","测试下单");
        map.put("client_ip","测试下单");
        map.put("format","page");
        map.put("notify_url","http://www.baidu.com");
        map.put("time",String.valueOf(System.currentTimeMillis()));
        String sign = YitongUtil.generateSignature(map,key);
        request.sign(sign);


        String paramString = JSON.toJSONString(request.build());
        Map jsonObject = JSON.parseObject(paramString);
        Map<String, Object> data = (Map<String,Object>)jsonObject;

        HttpResult body =  HttpUtils.doPost(pay_url, data);
        log.info("请求返还结果：{}",body);
    }


}
