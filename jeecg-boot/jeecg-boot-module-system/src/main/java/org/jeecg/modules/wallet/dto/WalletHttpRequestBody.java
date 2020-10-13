package org.jeecg.modules.wallet.dto;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author: wangjianbin
 * @Date: 2020/10/13 17:00
 */
@Data
@Builder
public class WalletHttpRequestBody implements Serializable {
    private String merchantId;
    private Integer coinType;
    private String callUrl;
}
