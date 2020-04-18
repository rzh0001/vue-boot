package org.jeecg.modules.pay.externalUtils.antUtil;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.security.MessageDigest;
import java.util.*;

/**
 * @Author: wangjianbin
 * @Date: 2020/4/18 12:03
 */
@Slf4j
@Service
public class AntUtil {
    public static String generateSignature(final Map<String, String> params, final String key) throws Exception {
        String paramUrl = formatUrlMap(params, true, false, false) + "&key=" + key;
        return MD5(paramUrl).toUpperCase();
    }
    public static String MD5(String data) throws Exception {
        java.security.MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] array = md.digest(data.getBytes("UTF-8"));
        StringBuilder sb = new StringBuilder();
        for (byte item : array) {
            sb.append(Integer.toHexString((item & 0xFF) | 0x100).substring(1, 3));
        }
        return sb.toString().toUpperCase();
    }
    public static String formatUrlMap(Map<String, String> paraMap, boolean removeEmptyValue, boolean urlEncode, boolean keyToLower) {
        String buff = "";
        Map<String, String> tmpMap = paraMap;
        //开启空值筛选，则移除数据
        try {
            List<Map.Entry<String, String>> infoIds = new ArrayList<Map.Entry<String, String>>(tmpMap.entrySet());
            // 对所有传入参数按照字段名的 ASCII 码从小到大排序（字典序）
            Collections.sort(infoIds, new Comparator<Map.Entry<String, String>>() {
                @Override
                public int compare(Map.Entry<String, String> o1, Map.Entry<String, String> o2) {
                    return (o1.getKey()).toString().compareTo(o2.getKey());
                }
            });
            // 构造URL 键值对的格式
            StringBuilder buf = new StringBuilder();
            for (Map.Entry<String, String> item : infoIds) {
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
