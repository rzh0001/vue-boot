package org.jeecg.modules.wallet.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @Author: wangjianbin
 * @Date: 2020/10/13 17:32
 */
@Data
public class WalletHttpResponse implements Serializable {
    private WalletHttpResponseBody data;
    private String message;
    private Integer code;
}
