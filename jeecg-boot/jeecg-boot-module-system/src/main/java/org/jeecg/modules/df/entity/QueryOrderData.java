package org.jeecg.modules.df.entity;

import com.alibaba.fastjson.JSON;
import lombok.Data;
import org.jeecg.modules.util.AES128Util;

import javax.validation.constraints.NotBlank;

/**
 * @author ruanzh
 * @since 2019/11/17
 */
@Data
public class QueryOrderData {
    
    @NotBlank
    private String orderNo;
    private String bizOrderNo;
    
    public String toJsonString() {
        return JSON.toJSONString(this);
    }
    
    public String encodeData(String apiKey) {
        return AES128Util.encryptBase64(toJsonString(), apiKey);
    }
}
