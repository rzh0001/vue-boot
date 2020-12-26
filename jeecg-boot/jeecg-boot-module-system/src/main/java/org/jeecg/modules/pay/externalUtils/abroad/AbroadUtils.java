package org.jeecg.modules.pay.externalUtils.abroad;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.jeecg.modules.pay.entity.OrderInfoEntity;
import org.jeecg.modules.v2.entity.PayBusiness;

import java.util.HashMap;
import java.util.Map;

/**
 * @author wangjianbin
 * @Description
 * @since 2020/12/25 17:18
 */
@Slf4j
public class AbroadUtils {
    /**
     *
     * @param domain
     * @param channelCode 本系统渠道编码
     * @param userName
     * @param order
     * @param userBusiness
     * @param channel 请求支付的渠道
     * @return
     */
    public static Map<String, Object> buildParamData(String domain, String channelCode, String userName, OrderInfoEntity order, PayBusiness userBusiness,String channel){
        String CALLBACK_URL="/callBack/order/"+channelCode+"/orderNo";
        String callBackUrl = domain+CALLBACK_URL;
        StringBuilder sign = new StringBuilder();
        sign.append("userid=").append(userName)
                .append("&orderNo=").append(order.getOrderId())
                .append("&MsgUrl=").append(callBackUrl)
                .append("&return_url=").append(callBackUrl)
                .append("&mch_id=").append(userBusiness.getBusinessCode())
                .append("&key=").append(userBusiness.getBusinessApiKey());
        log.info("签名字符串为：{}",sign.toString());
        String s = DigestUtils.md5Hex(sign.toString()).toUpperCase();
        Map<String, Object> data = new HashMap<>();
        data.put("Amount",order.getSubmitAmount().toString());
        data.put("userid",userName);
        data.put("orderNo",order.getOrderId());
        data.put("MsgUrl",callBackUrl);
        data.put("return_url",callBackUrl);
        data.put("mch_id",userBusiness.getBusinessCode());
        data.put("sign",s);
        data.put("channel",channel);
        log.info("请求参数：{}",data);
        return data;
    }
}
