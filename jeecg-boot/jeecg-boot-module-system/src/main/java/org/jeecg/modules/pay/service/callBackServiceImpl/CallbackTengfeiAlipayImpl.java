package org.jeecg.modules.pay.service.callBackServiceImpl;

import cn.hutool.crypto.SecureUtil;
import org.jeecg.modules.api.constant.PayTypeEnum;
import org.jeecg.modules.pay.service.AbstractCallBack;
import org.jeecg.modules.pay.service.factory.CallBackServiceFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @Author: wangjianbin
 * @Date: 2020/4/27 22:01
 */
@Service
public class CallbackTengfeiAlipayImpl extends AbstractCallBack implements InitializingBean {
    @Override
    public Object reply(Map<String, Object> map, String apiKey) throws Exception {
        log.info("==>腾飞支付 回调，回调参数为：{}", map);
        String openid = (String)map.get("openid");
        String orderNo = (String)map.get("orderNo");
        String tradeNo = (String)map.get("tradeNo");
        String orderPrice = (String)map.get("orderPrice");
        String sign = (String)map.get("sign");
        StringBuilder md5buffer = new StringBuilder();
        md5buffer.append("openid=").append(openid).append("&orderNo=").append(orderNo).append("&orderPrice=")
            .append(orderPrice).append("&tradeNo=").append(tradeNo).append("&key=").append(apiKey);
        log.info("==>腾飞支付 签名：{}", md5buffer.toString());
        String localSign = SecureUtil.md5(md5buffer.toString()).toUpperCase();
        if (!sign.equals(localSign)) {
            log.info("==>签名不匹配，localSign：{}；sign：{}", localSign, sign);
            return "fail";
        }
        return "success";
    }

    @Override
    public Map<String, Object> getCallBackParam(Map<String, Object> map) {
        return map;
    }

    @Override
    public boolean checkOrderStatusIsOK(Map<String, Object> map, String apiKey) throws Exception {
        return true;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        CallBackServiceFactory.register(PayTypeEnum.TENGFEI_ALIPAY.getValue(), this);
    }
}
