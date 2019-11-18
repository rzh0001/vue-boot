package org.jeecg.modules.df.entity;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.jeecg.modules.util.AES128Util;

/**
 * @author ruanzh
 * @since 2019/11/19
 */
@Data
@Slf4j
public class CallbackBody {
    private String username;
    private String data;
    private Long timestamp;
    private String sign;
    
    public CallbackBody() {
    }
    
    /**
     * 签名验证算法
     *
     * @param apiKey
     * @return
     */
    public boolean verifySignature(String apiKey) {
        String localSign = sign(apiKey);
        log.info("===>系统加密的sign值为：{}", localSign);
        log.info("===>商户传递的sign值为：{}", sign);
        return StrUtil.equals(localSign, sign);
    }
    
    /**
     * 签名算法
     *
     * @param apiKey
     * @return
     */
    public String sign(String apiKey) {
        StringBuilder sb = new StringBuilder();
        sb.append(username).append(timestamp).append(data).append(apiKey);
        log.info("===>系统拼接的sign串为：{}", sb.toString());
        return DigestUtils.md5Hex(sb.toString());
    }
    
    public String decodeData(String apiKey) {
        return AES128Util.decryptBase64(data, apiKey);
    }
    
    public String toJsonString() {
        return JSON.toJSONString(this);
    }
}
