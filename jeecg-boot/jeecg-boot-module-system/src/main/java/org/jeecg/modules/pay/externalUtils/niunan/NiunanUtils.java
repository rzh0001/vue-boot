package org.jeecg.modules.pay.externalUtils.niunan;

import java.security.MessageDigest;

/**
 * @Author: wangjianbin
 * @Date: 2020/4/27 21:44
 */
public class NiunanUtils {
    /**
     * MD5
     *
     * @param dataStr
     * @return
     */
    public static String md5Hash(String dataStr) {
        try {
            MessageDigest m = MessageDigest.getInstance("MD5");
            m.update(dataStr.getBytes("UTF8"));
            byte s[] = m.digest();
            String result = "";
            for (int i = 0; i < s.length; i++) {
                result += Integer.toHexString((0x000000FF & s[i]) | 0xFFFFFF00).substring(6);
            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
}
