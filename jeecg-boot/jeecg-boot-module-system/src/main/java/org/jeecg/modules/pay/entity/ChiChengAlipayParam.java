package org.jeecg.modules.pay.entity;

import lombok.Data;

/**
 * @Author: wangjianbin
 * @Date: 2020/3/13 19:56
 */
@Data
public class ChiChengAlipayParam {
    /**
     * 商户号
     */
    private String pay_memberid;

    /**
     * 订单号
     */
    private String pay_orderid;

    /**
     * 提交时间
     */
    private String pay_applydate;

    /**
     * 银行编码
     */
    private String pay_bankcode;

    /**
     * 服务端通知
     */
    private String pay_notifyurl;

    /**
     * 页面跳转通知
     */
    private String pay_callbackurl;

    /**
     *  订单金额
     */
    private String pay_amount;

    /**
     * MD5签名
     */
    private String pay_md5sign;

    /**
     * 此字段在返回时按原样返回
     */
    private String pay_attach;

    /**
     * 商品名称
     */
    private String pay_productname;

    /**
     * 商户品数量
     */
    private String  pay_productnum;

    private String pay_productdesc;
    private String pay_producturl;
    private String pay_type;

}
