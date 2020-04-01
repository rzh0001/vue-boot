package org.jeecg.modules.util;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import java.util.*;

/**
 * 签名辅助类
 * Created by weibin.chen on 2018/02.
 */
public class SignatureUtils {

    /**
     * 将参数从Map<String, String>转换为Map<String, String[]>
     * @param parameters 参数
     * @return 已转换的参数
     */
    public static Map<String, String[]> convertParameters(Map<String, String> parameters) {
        if (parameters == null) {
            return null;
        }
        Map<String, String[]> params = new HashMap<>(parameters.size());
        for (Map.Entry<String, String> item : parameters.entrySet()) {
            params.put(item.getKey(), new String[] {item.getValue()});
        }
        return params;
    }
    
    /**
     * 将参数从Map<String, Object>转换为Map<String, String>
     * @param parameters 参数
     * @return 已转换的参数
     */
	public static Map<String, String> convertParams(Map<String, Object> parameters) {
		if (parameters == null) {
			return null;
		}
		Map<String, String> params = new HashMap<>(parameters.size());
		for (Map.Entry<String, Object> item : parameters.entrySet()) {
			params.put(item.getKey(), item.getValue() == null ? null : item.getValue().toString());
		}
		return params;
	}

    /**
     * 根据指定的参数和密钥生成签名，参数名不允许为null
     * @param secret 密钥
     * @param parameters 参数
     * @return 参数签名
     */
    public static String signature(String secret, Map<String, String> parameters) {
        if (StringUtils.isEmpty(secret)) {
            throw new IllegalArgumentException("secret参数不能为空");
        }
        parameters = parameters == null ? new HashMap<>(0) : parameters;
        TreeMap<String, String> sortedParameters = new TreeMap<>(parameters);
        StringBuilder paramStrBuilder = new StringBuilder();
        for (Map.Entry<String, String> item : sortedParameters.entrySet()) {
            if (item.getValue() == null) {
                paramStrBuilder.append(item.getKey()).append("=").append("&");
                continue;
            }
            List<String> valueList = Arrays.asList(item.getValue());
            Collections.sort(valueList, (a, b) -> {
                if (a == b) {
                    return 0;
                }
                if (a == null) {
                    return -1;
                }
                if (b == null) {
                    return 1;
                }
                if (a.equals(b)) {
                    return 0;
                }
                return a.compareTo(b);
            });
            for (String value : valueList) {
                paramStrBuilder.append(item.getKey())
                        .append("=")
                        .append(value == null ? "" : value)
                        .append("&");
            }
        }
        paramStrBuilder.append("key=").append(secret);
        String paramStr = paramStrBuilder.toString();
        byte[] hash = DigestUtils.sha256(paramStr);
        return Base64.encodeBase64String(hash).toUpperCase();
    }

    /**
     * 验证签名是否匹配，参数名不允许为null
     * @param secret 密钥
     * @param parameters 参数
     * @param signature 签名
     * @return 签名是否匹配
     */
    public static boolean verify(String secret, Map<String, String> parameters, String signature) {
        String correctOne = signature(secret, parameters);
        return correctOne.equals(signature);
    }
}
