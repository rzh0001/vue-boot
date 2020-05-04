package org.jeecg.modules.pay.service.callBackServiceImpl;

import org.jeecg.modules.pay.externalUtils.antUtil.AntUtil;
import org.jeecg.modules.pay.service.AbstractCallBack;
import org.jeecg.modules.pay.service.factory.CallBackServiceFactory;
import org.jeecg.modules.util.BaseConstant;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @Author: wangjianbin
 * @Date: 2020/4/27 13:35
 */
@Service
public class CallbackAntAlipayImpl extends AbstractCallBack implements InitializingBean {

    @Override
    public Object reply() throws Exception {
        return  "success";
    }

    @Override
    public boolean checkSign(Map<String, Object> map, String apiKey) throws Exception {
        log.info("==>蚁支付，回调参数为：{}", map);
        String sign = (String)map.get("sign");
        map.remove("sign");
        map.remove("code");
        map.remove("msg");
        map.remove("sub_code");
        map.remove("sub_msg");
        String localSign = AntUtil.generateSignature(map, apiKey);
        if (!localSign.equals(sign)) {
            log.info("==>蚁支付验证签名异常，回调签名为：{}，本地签名为：{}", sign, localSign);
            return false;
        }
        return true;
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
        CallBackServiceFactory.register(BaseConstant.REQUEST_ANT_ALIPAY, this);
    }
}
