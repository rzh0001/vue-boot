package org.jeecg.modules.pay.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * @title:
 * @Description: 农信易扫请求挂马平台入参
 * @author: wangjb
 * @create: 2019-08-08 13:45
 */
@Data
public class NxysCallBackParam implements Serializable {
    /**
     * 商户号
     */
    private String merchantid;
    /**
     * 系统订单号
     */
    private String orderid;
    /**
     * 订单金额
     */
    private String amount;
    /**
     * 支付通道
     */
    private String paytype;
    /**
     * 客户端ip
     */
    private String client_ip;
    /**
     * 回调地址
     */
    private String notify_url;
    private String return_url;
    /**
     * 外部订单号（商家）
     */
    private String ext;
    /**
     * 签名
     */
    private String sign;
}
