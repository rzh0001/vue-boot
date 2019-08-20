package org.jeecg.modules.pay.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * @title:
 * @Description: 请求挂马后台创建订单实体信息
 * @author: wangjb
 * @create: 2019-07-24 16:15
 */
@Data
public class AliPayCallBackParam implements Serializable {
    private String account_id;
    private String content_type;
    private String thoroughfare;
    private String type;
    private String out_trade_no;
    private String robin;
    private String keyId;
    private String amount;
    private String callback_url;
    private String success_url;
    private String error_url;
    private String sign;
    private String userName;
    private String payType;
}
