package org.jeecg.modules.pay.service.callBackServiceImpl;

import org.apache.commons.codec.digest.DigestUtils;
import org.jeecg.modules.pay.service.AbstractCallBack;
import org.jeecg.modules.pay.service.factory.CallBackServiceFactory;
import org.jeecg.modules.util.BaseConstant;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class CallbackAbroad923Impl extends AbstractCallBack implements InitializingBean {
    @Override
    public Object reply() throws Exception {
        return  "success";
    }

    @Override
    public boolean checkSign(Map<String, Object> map, String apiKey) throws Exception {
        log.info("923回调参数为：{}", map);
        String sign = (String)map.get("sign");
        String orderNo = (String)map.get("orderNo");
        String money = (String)map.get("money");
        String mch_id = (String)map.get("mch_id");
        StringBuilder signLocal = new StringBuilder();
        signLocal.append("orderNo=").append(orderNo)
                .append("&state=success&money=").append(money)
                .append("&mch_id=").append(mch_id)
                .append("&key=").append(apiKey);
        log.info("本地签名串为:{}",signLocal.toString());
        String s = DigestUtils.md5Hex(signLocal.toString()).toUpperCase();
        if(sign.equals(s)){
            return true;
        }
        return false;
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
        CallBackServiceFactory.register(BaseConstant.REQUEST_ABROAD_923, this);
    }
}
