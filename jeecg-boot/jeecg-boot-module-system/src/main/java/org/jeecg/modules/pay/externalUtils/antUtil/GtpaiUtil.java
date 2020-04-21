package org.jeecg.modules.pay.externalUtils.antUtil;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.security.MessageDigest;
import java.util.*;

/**
 * @Author: heihei
 * @Date: 2020/4/20 12:03
 */
@Slf4j
@Service
public class GtpaiUtil {
    public static String generateSignature(final Map<String, String> params, final String key) throws Exception {
        String paramSrc =getParamSrc(params);
        String paramSrcTemp = paramSrc+"&key="+ key;
        return MD5(paramSrcTemp).toUpperCase();
    }

    public static String getParamSrc(Map<String, String> paramsMap) {
        StringBuffer paramstr = new StringBuffer();
        for (String pkey : paramsMap.keySet()) {
            String pvalue = paramsMap.get(pkey);
            if (null != pvalue && "" != pvalue) {// 空值不传递，不签名
                paramstr.append(pkey + "=" + pvalue + "&"); // 签名原串，不url编码
            }
        }
        // 去掉最后一个&
        String result = paramstr.substring(0, paramstr.length() - 1);
        //System.out.println("签名原串：" + result);
        //System.out.println("java.nio.charset.Charset.defaultCharset():" + java.nio.charset.Charset.defaultCharset());
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
