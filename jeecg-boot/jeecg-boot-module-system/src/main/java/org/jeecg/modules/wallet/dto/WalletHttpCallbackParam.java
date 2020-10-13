package org.jeecg.modules.wallet.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class WalletHttpCallbackParam implements Serializable {
    private Long timestamp;
    private Long nonce;
    private String sign;
    private String body;
}
