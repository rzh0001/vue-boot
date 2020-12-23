package org.jeecg.modules.pay.service.requestPayUrl.impl;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
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
import java.util.Map;
import java.util.TreeMap;

@Service
@Slf4j
public class CheckoutCounterImpl  implements RequestPayUrl<OrderInfoEntity, String, String, String, String, PayBusiness,
        Object, HttpServletResponse>, InitializingBean {
    @Override
    public R requestPayUrl(OrderInfoEntity order, String userName, String url, String key, String callbackUrl, PayBusiness userBusiness,HttpServletResponse response) throws Exception {
        RequestParamDTO.RequestParamDTOBuilder request =
                RequestParamDTO.builder().client_ip("127.0.0.1")
                        .format("page").goods_desc("下单").mch_id(userBusiness.getBusinessCode())
                        .money(order.getSubmitAmount().toString()).notify_url("http://www.baidu.com")
                        .order_sn(order.getOrderId()).time(String.valueOf(System.currentTimeMillis()));
        TreeMap<String, Object> map =  new TreeMap<String,Object>();
        map.put("mch_id",userBusiness.getBusinessCode());
        map.put("order_sn",order.getOrderId());
        map.put("money",order.getSubmitAmount().toString());
        map.put("goods_desc","下单");
        map.put("client_ip","127.0.0.1");
        map.put("format","page");
        map.put("notify_url","http://www.baidu.com");
        map.put("time",String.valueOf(System.currentTimeMillis()));
        String sign = YitongUtil.generateSignature(map,key);
        request.sign(sign);

        String paramString = JSON.toJSONString(request.build());
        Map jsonObject = JSON.parseObject(paramString);
        Map<String, Object> data = (Map<String,Object>)jsonObject;

        HttpResult body =  HttpUtils.doPost(url, data);

        response.setContentType("text/html;charset=utf-8");
        PrintWriter writer;
        try {
            writer = response.getWriter();
        } catch (IOException e) {
            writer = new PrintWriter(System.out);
            log.error("IO异常:", e);
        }
        Document document = Jsoup.parse(body.getBody());
        writer.println(document.outerHtml());
        writer.close();
        return null;
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
