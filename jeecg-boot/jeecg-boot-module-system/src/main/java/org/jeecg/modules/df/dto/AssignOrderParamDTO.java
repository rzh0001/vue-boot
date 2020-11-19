package org.jeecg.modules.df.dto;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;

import javax.validation.constraints.NotEmpty;

/**
 * @author wangjianbin
 * @Description
 * @since 2020/11/19 15:50
 */
@Data
@Slf4j
public class AssignOrderParamDTO {
    /**
     * 设备编码
     */
    @NotEmpty(message = "设备编码不能为空")
    private String deviceCode;
    /**
     * 余额
     */
    private String balance;
    /**
     * 签名
     * deviceCode=*&banlance=*&deviceKey=*
     */
    @NotEmpty(message = "签名不能为空")
    private String sign;


    public boolean checkSign(String key){
        StringBuilder localSign = new StringBuilder();
        localSign.append("deviceCode=").append(deviceCode).append("&balance=").append(balance).append("&deviceKey=").append(key);
        String localSignStr = DigestUtils.md5Hex(sign.toString());
        if(!localSignStr.equals(this.sign)){
            log.error("签名验证不通过，入参签名为：{},本地签名为：{}，本地签名串为：{}",sign,localSignStr,localSign.toString());
            return false;
        }
        return true;
    }
}
