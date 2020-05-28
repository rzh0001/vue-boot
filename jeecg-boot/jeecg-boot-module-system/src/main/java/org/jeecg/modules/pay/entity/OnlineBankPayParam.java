package org.jeecg.modules.pay.entity;

import lombok.Data;

/**
 * @Author: wangjianbin
 * @Date: 2020/5/16 11:09
 */
@Data
public class OnlineBankPayParam {
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
     * 订单金额
     */
    private String pay_amount;
    /**
     * MD5签名
     */
    private String pay_md5sign;
    /**
     * 附加字段
     */
    private String pay_attach;
    /**
     * 商品名称
     */
    private String pay_productname;
    /**
     * 商户品数量
     */
    private String pay_productnum;
    /**
     * 商品描述
     */
    private String pay_productdesc;
    /**
     * 商户链接地址
     */
    private String pay_producturl;
}
