package org.jeecg.modules.pay.service.factory;

import org.jeecg.modules.pay.service.requestPayUrl.RequestPayUrl;
import org.springframework.stereotype.Service;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class PayServiceFactory {
    private static Map<String,RequestPayUrl> services = new ConcurrentHashMap<String,RequestPayUrl>();
    private static Map<String,String> payUrls = new ConcurrentHashMap<String,String>();
    public static String getRequestUrl(String channel){
        return payUrls.get(channel);
    }
    public static void registerUrl(String channel,String url){
        payUrls.put(channel,url);
    }
    public static void register(String channel,RequestPayUrl requestPayUrl){
        services.put(channel,requestPayUrl);
    }
    public static RequestPayUrl getPay(String channel) {
        return services.get(channel);
    }
}
