package org.jeecg.modules.pay.service.requestPayUrl.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.jeecg.modules.pay.dto.RequestParamDTO;
import org.jeecg.modules.pay.entity.OrderInfoEntity;
import org.jeecg.modules.pay.externalUtils.antUtil.YitongUtil;
import org.jeecg.modules.pay.service.factory.PayServiceFactory;
import org.jeecg.modules.pay.service.requestPayUrl.RequestPayUrl;
import org.jeecg.modules.system.util.IPUtils;
import org.jeecg.modules.util.BaseConstant;
import org.jeecg.modules.util.HttpResult;
import org.jeecg.modules.util.HttpUtils;
import org.jeecg.modules.util.R;
import org.jeecg.modules.v2.entity.PayBusiness;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

@Service
@Slf4j
public class CheckoutCounterImpl  implements RequestPayUrl<OrderInfoEntity, String, String, String, String, PayBusiness,
        Object, HttpServletResponse>, InitializingBean {
    @Override
    public R requestPayUrl(OrderInfoEntity order, String userName, String url, String key, String callbackUrl, PayBusiness userBusiness,HttpServletResponse response) throws Exception {
        StringBuilder sign = new StringBuilder();
        sign.append("userid=").append("123")
                .append("&orderNo=").append("Order123456789")
                .append("&MsgUrl=").append("http://www.baidu.com")
                .append("&return_url=").append("http://www.baidu.com")
                .append("&mch_id=").append("1234567356")
                .append("&key=").append("abcfhosyFQRTV568");

        String s = DigestUtils.md5Hex(sign.toString()).toUpperCase();
        Map<String, Object> data2 = new HashMap<>();
        data2.put("Amount","500000");
        data2.put("userid","123");
        data2.put("orderNo","Order123456789");
        data2.put("MsgUrl","http://www.baidu.com");
        data2.put("return_url","http://www.baidu.com");
        data2.put("mch_id","1234567356");
        data2.put("sign",s);
        data2.put("channel","907");

        HttpResult body =  HttpUtils.doPost(url, data2);
        log.info("请求返还结果：{}",body);
        log.info("请求返还结果：{}",body);
        String result = body.getBody();
        JSONObject json = JSON.parseObject(result);
        JSONObject resultJson = (JSONObject) json.get("result");
        String payUrl = (String)resultJson.get("payurl");
        return R.ok().put("url", payUrl);

    }

    @Override
    public boolean orderInfoOk(OrderInfoEntity order, String url, PayBusiness userBusiness) throws Exception {
        return false;
    }

    @Override
    public boolean notifyOrderFinish(OrderInfoEntity order, String key, PayBusiness userBusiness, String url) throws Exception {
        return false;
    }

    @Override
    public Object callBack(Object object) throws Exception {
        return null;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        PayServiceFactory.register(BaseConstant.REQUEST_CHECKOUT_COUNTER, this);
    }
}
