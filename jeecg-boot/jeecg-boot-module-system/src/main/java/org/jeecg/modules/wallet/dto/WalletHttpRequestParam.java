package org.jeecg.modules.wallet.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @Author: wangjianbin
 * @Date: 2020/10/13 16:58
 */
@Data
public class WalletHttpRequestParam implements Serializable {
    private Long timestamp;
    private Long nonce;
    /**
     * sign=md5(body + key + nonce + timestamp)
     */
    private String sign;
    private String body;
}
