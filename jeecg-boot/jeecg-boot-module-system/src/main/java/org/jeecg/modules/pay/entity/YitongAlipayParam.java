package org.jeecg.modules.pay.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author: heihei
 * @Date: 2020/4/26 21:14
 */
@Data
public class YitongAlipayParam implements Serializable {


    //交易商户号
    private String mch_id;
    /**
     1	支付宝扫码
     2	微信扫码
     3	银行卡转账
     4	微信转手机号
     5   微信转银行卡
     6   云闪付
     11   吱口令
     12   支付宝转账码
     */
    private String ptype;

    /**
     * 订单总金额，单位为分的整数
     */
    private String money;
    private String order_sn;
    private String goods_desc;
    private String client_ip;
    private String format;//json或page（默认）
    private String notify_url;
    private String time;//系统当前时间戳 UTC 秒
    private String sign;
}
