package org.jeecg.modules.pay.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * @title: 跑分实体
 * @Description:
 * @author: 嘿嘿
 * @create: 2019-11-05 14:21
 */
@Data
public class PaofenParam implements Serializable {

    /**
     * 必填。您的商户唯一标识
     */
    private String shid;

    /**
     * 必填。 wx:微信、zfb:支付宝、yl:银联
     */
    private String pay;

    /**
     * 交易金额,必填。单位：元。精确小数点后2位
     */
    private String amount;

    /**
     * 回调地址
     */
    private String url;

    /**
     * 商户订单号
     */
    private String orderid;

    /**
     * 商户秘钥
     */
    private String key;
}
