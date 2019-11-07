package org.jeecg.modules.pay.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * 信付支付入参
 */
@Data
public class XinPayParam implements Serializable {
    /**
     * 商户号
     */
    private String merchantNum;

    /**
     * 商户订单号
     */
    private String orderNo;

    /**
     * 支付金额
     */
    private String amount;

    /**
     * 异步通知地址
     */
    private String notifyUrl;

    /**
     * 同步通知地址
     */
    private String returnUrl;

    /**
     * 请求支付类型
     * 支持微信，支付宝，云闪付，银行卡
     * alipay/wechat/unionpay/bankcard
     */
    private String payType;

    /**
     * 附加信息
     */
    private String attch;

    /**
     * 签名【md5(商户号+商户订单号+支付金额+异步通知地址+商户秘钥)】
     * 注意：支付金额的值需要转为字符串再进行md5加密
     */
    private String sign;
}
