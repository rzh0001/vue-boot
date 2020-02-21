package org.jeecg.modules.pay.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author: wangjianbin
 * @Date: 2020/2/8 19:46
 */
@Data
public class DianJinPayParam implements Serializable {
    private String totalAmount;
    private String merchantTransNo;
    private String paymentType;
    private String uid;
    private String remark;
    private String sign;
}
