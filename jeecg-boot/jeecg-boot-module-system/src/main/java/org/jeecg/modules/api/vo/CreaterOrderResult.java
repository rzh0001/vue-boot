package org.jeecg.modules.api.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @title:
 * @Description:
 * @author: wangjb
 * @create: 2019-06-14 10:32
 */
@Data
public class CreaterOrderResult implements Serializable {
    /**
     * 商户号
     */
    private String mchId;

    private Integer menberId;
    /**
     * 签名密钥
     */
    private String signKey;

    /**
     * appid
     */
    private String appId;

    /**
     * APPSECRET
     */
    private String appSecter;

    /**
     * 网关
     */
    private String gateWay;

    private String notifyUrl;

    private String callbackUrl;

    /**
     * 防封域名
     */
    private String unlockDomain;

    /**
     * 交易金额
     */
    private Double amount;

    private String bankCode;

    /**
     * 银行英文代码
     */
    private String code;

    /**
     * 系统订单号
     */
    private String orderId;

    /**
     * 外部订单号
     */
    private String outTradeId;

    /**
     * 商品标题
     */
    private String subject;

    /**
     * 订单添加状态
     */
    private String status;
}
