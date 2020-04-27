package org.jeecg.modules.pay.externalUtils.antUtil;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.util.Map;

/**
 * @Author: heihei
 * @Date: 2020/4/20 12:03
 */
@Slf4j
@Service
public class YitongUtil {
    public static String generateSignature(final Map<String, Object> params, final String key) throws Exception {
        String paramSrc = getParamSrc(params);
        String paramSrcTemp = paramSrc + "&key=" + key;
        log.info("==>签名串为：{}", paramSrcTemp);
        return MD5(paramSrcTemp).toLowerCase();
    }

    public static String getParamSrc(Map<String, Object> paramsMap) {
        StringBuffer paramstr = new StringBuffer();
        for (String pkey : paramsMap.keySet()) {
            String pvalue = (String) paramsMap.get(pkey);
            paramstr.append(pkey + "=" + pvalue + "&"); // 签名原串，不url编码
        }
        // 去掉最后一个&
        String result = paramstr.substring(0, paramstr.length() - 1);
        return result;
    }


    public static String MD5(String data) throws Exception {
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] array = md.digest(data.getBytes("UTF-8"));
        StringBuilder sb = new StringBuilder();
        for (byte item : array) {
            sb.append(Integer.toHexString((item & 0xFF) | 0x100).substring(1, 3));
        }
        return sb.toString().toUpperCase();
    }
}