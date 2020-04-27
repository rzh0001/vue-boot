package org.jeecg.modules.pay.service.callBackServiceImpl;

import org.jeecg.modules.api.constant.PayTypeEnum;
import org.jeecg.modules.pay.service.AbstractCallBack;
import org.jeecg.modules.pay.service.factory.CallBackServiceFactory;
import org.jeecg.modules.util.SignatureUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @Author: wangjianbin
 * @Date: 2020/4/27 21:51
 */
@Service
public class CallbackLetianAlipayImpl extends AbstractCallBack implements InitializingBean {
    @Override
    public Object reply(Map<String, Object> map, String apiKey) throws Exception {
        log.info("==>乐天支付回调，入参为：{}", map);
        String sign = (String)map.get("Signature");
        map.remove("Signature");
        String localSign = SignatureUtils.signature(apiKey, map);
        if (!localSign.equals(sign)) {
            log.info("=>乐天支付，签名校验失败,入参签名：{}，本地签名：{}", sign, localSign);
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
        CallBackServiceFactory.register(PayTypeEnum.LETIAN_ALIPAY.getValue(), this);
    }
}
