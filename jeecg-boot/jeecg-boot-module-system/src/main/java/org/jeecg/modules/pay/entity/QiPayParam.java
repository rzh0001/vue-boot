package org.jeecg.modules.pay.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author: wangjianbin
 * @Date: 2020/2/7 22:59
 */
@Data
public class QiPayParam implements Serializable {
    private String pid;
    private String money;
    /**
     * 支付宝通道固定值     alipay
     * 云闪付通道固定值  unionpay
     * 支付宝转银行卡固定值    toCard
     * 淘宝现金红包通道固定值 taobaohongbao
     */
    private String channel;
    private String out_order_id;
    /**
     * 扩展信息，支付成功后，原样返回给异步通知地址
     */
    private String extend;
    /**
     * 终端类型
     * pc
     */
    private String terminal;
    /**
     * md5(pid+money+out_order_id+extend+apikey)
     */
    private String sign;
    private String notifyurl;
}
