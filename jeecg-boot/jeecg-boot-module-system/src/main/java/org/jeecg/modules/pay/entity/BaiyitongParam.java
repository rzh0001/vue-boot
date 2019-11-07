package org.jeecg.modules.pay.entity;

import lombok.Data;
import java.io.Serializable;

/**
 * @title: 百易通实体
 * @Description:
 * @author: wangjb
 * @create: 2019-11-05 14:21
 */
@Data
public class BaiyitongParam implements Serializable {
    /**
     * 请求支付标识,app、PC、mobile
     */
    private String return_type;

    /**
     * 必填。您的商户唯一标识
     */
    private String appid;

    /**
     * 必填。 wechat:微信、alipay:支付宝、unionpay:云闪付、农信易扫云闪付、bank:银行卡
     */
    private String pay_type;

    /**
     * 交易金额,必填。单位：元。精确小数点后2位
     */
    private String amount;

    /**
     * 回调地址
     */
    private String callback_url;

    /**
     * 支付成功后网页自动跳转地址
     */
    private String success_url;

    /**
     * 支付失败时，或支付超时后网页自动跳转地址
     */
    private String error_url;

    /**
     * 用户网站的请求支付用户信息，可以是帐号也可以是数据库的ID
     *
     * 本系统传递的值为本系统对应的通道
     */
    private String out_uid;

    /**
     * 商户订单号
     */
    private String out_trade_no;

    /**
     * 接口版本号
     */
    private String version = "v1.1";

    private String sign;
}
