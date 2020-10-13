package org.jeecg.modules.wallet.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author: wangjianbin
 * @Date: 2020/10/13 17:33
 */
@Data
public class WalletHttpResponseBody implements Serializable {

    private Integer coinType;
    private String address;
}
