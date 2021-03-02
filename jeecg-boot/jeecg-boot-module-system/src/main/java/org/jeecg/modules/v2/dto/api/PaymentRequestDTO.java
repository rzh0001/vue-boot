package org.jeecg.modules.v2.dto.api;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import org.apache.commons.codec.digest.DigestUtils;
import org.jeecg.common.util.encryption.AES128Util;
import org.springframework.util.StringUtils;

import java.io.Serializable;

@Data
public class PaymentRequestDTO implements Serializable {
    private String data;
    private String sign;
    private String timestamp;
    private String userName;

    private static final String USER_NAME="userName=";
    private static final String TIMESTAMP="timestamp=";
    private static final String KEY="key=";
    private static final String DATA="data=";

    /**
     * 校验签名
     * @param apiKey
     * @return
     */
    public boolean checkSign(String apiKey){
        StringBuilder local = new StringBuilder();
        local.append(USER_NAME).append(userName).append("&")
                .append(TIMESTAMP).append(timestamp).append("&")
                .append(DATA).append(data).append("&")
                .append(KEY).append(apiKey);
        String localSgin = DigestUtils.md5Hex(local.toString());
        if(!localSgin.equals(sign)){
            return false;
        }
        return true;
    }

    /**
     * 解密data
     * @param apiKey
     * @return
     */
    public PaymentRequestDataDTO decrypt(String apiKey){
        String jsonData = AES128Util.decryptBase64(data, apiKey);
        if(StringUtils.isEmpty(jsonData)){
            return null;
        }
        return JSONObject.parseObject(jsonData,PaymentRequestDataDTO.class);
    }
}
