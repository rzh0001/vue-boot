package org.jeecg.modules.wallet.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author: wangjianbin
 * @Date: 2020/10/14 14:49
 */
@Data
public class AsyncNotifyParam implements Serializable {
    private Long timestamp;
    private String remark;
    /**
     * timestamp=timestamp&body=body&apikey=apikey
     */
    private String sign;
    /**
     * 加密数据
     */
    private String body;
}
