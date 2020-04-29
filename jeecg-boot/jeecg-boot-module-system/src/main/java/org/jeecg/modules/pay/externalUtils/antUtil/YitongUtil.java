package org.jeecg.modules.pay.externalUtils.antUtil;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

/**
 * @Author: heihei
 * @Date: 2020/4/20 12:03
 */
@Slf4j
@Service
public class YitongUtil {
    public static String generateSignature(final Map<String, Object> params, final String key) throws Exception {
        String paramUrl = formatUrlMap(params, false, false, true) + "&key=" + key;
        return MD5(paramUrl).toLowerCase();
    }
    public static String MD5(String data) throws Exception {
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("MD5");
            digest.update(data.getBytes());
            return new BigInteger(1, digest.digest()).toString(16);
        } catch (NoSuchAlgorithmException e) {
            log.info("==>请求跑分挂马平台,生成签名异常。异常信息为：{}", e);
        }
        return null;
    }
    public static String formatUrlMap(Map<String, Object> paraMap, boolean removeEmptyValue, boolean urlEncode, boolean keyToLower) {
        String buff = "";
        Map<String, Object> tmpMap = paraMap;
        //开启空值筛选，则移除数据
        try {
            List<Map.Entry<String, Object>> infoIds = new ArrayList<Map.Entry<String, Object>>(tmpMap.entrySet());
            // 对所有传入参数按照字段名的 ASCII 码从小到大排序（字典序）
            Collections.sort(infoIds, new Comparator<Map.Entry<String, Object>>() {
                @Override
                public int compare(Map.Entry<String, Object> o1, Map.Entry<String, Object> o2) {
                    return (o1.getKey()).toString().compareTo(o2.getKey().toString());
                }
            });
            // 构造URL 键值对的格式
            StringBuilder buf = new StringBuilder();
            for (Map.Entry<String, Object> item : infoIds) {
                if (StringUtils.isNotBlank(item.getKey())) {
                    String key = item.getKey();
                    Object val = item.getValue();
                    if (removeEmptyValue && val == null) {
                        continue;
                    }
                    if (urlEncode) {
                        val = URLEncoder.encode(val.toString(), "utf-8");
                    }
                    if (keyToLower) {
                        key = key.toLowerCase();
                    }
                    buf.append(key + "=" + val).append("&");
                }
            }
            buff = buf.toString();
            if (buff.isEmpty() == false) {
                buff = buff.substring(0, buff.length() - 1);
            }
        } catch (Exception e) {
            log.info("==>蚁支付，拼接MD5字符串异常，异常信息为：{}",e);
            return "";
        }
        log.info("==>蚁支付，拼接MD5字符串 buffer=:{}",buff);
        return buff;
    }
}