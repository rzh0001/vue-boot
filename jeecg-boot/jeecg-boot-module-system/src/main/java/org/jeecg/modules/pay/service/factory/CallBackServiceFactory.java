package org.jeecg.modules.pay.service.factory;

import org.jeecg.modules.pay.service.AbstractCallBack;
import org.jeecg.modules.pay.service.requestPayUrl.RequestPayUrl;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author: wangjianbin
 * @Date: 2020/4/24 22:25
 */
public class CallBackServiceFactory {
    private static Map<String,AbstractCallBack> services = new ConcurrentHashMap<String,AbstractCallBack>();
    public static AbstractCallBack getCallBackRequest(String paytype){
        return services.get(paytype);
    }
    public static void register(String payType, AbstractCallBack callBack){
        services.put(payType,callBack);
    }
}
