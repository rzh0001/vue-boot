package org.jeecg.modules.pay.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author: heihei
 * @Date: 2020/4/20 21:14
 */
@Data
public class GtpaiAlipayParam implements Serializable {

    /**
     * 机构号
     */
    private String store_id;

    //交易商户号
    private String mch_id;
    /**
     * 31：银联快捷
     * 37：网关支付
     * 35：手QH5
     * 41：支付宝H5
     * 38：快捷支付
     * 51：复制转卡H5
     * 53：支付宝个码H5
     * 54：复制网银转账
     */
    private String pay_type;

    /**
     * 订单总金额，单位为分的整数
     */
    private String trans_amt;
    private String out_trade_no;
    private String body;
    private String notify_url;
    private String sign;
}
