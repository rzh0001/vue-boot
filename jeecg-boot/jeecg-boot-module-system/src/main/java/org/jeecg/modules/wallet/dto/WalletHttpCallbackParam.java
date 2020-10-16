package org.jeecg.modules.wallet.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class WalletHttpCallbackParam implements Serializable {
    private String timestamp;
    private String nonce;
    private String sign;
    private String body;
}
