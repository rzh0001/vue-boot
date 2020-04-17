package org.jeecg.modules.pay.entity;

import io.swagger.models.auth.In;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @Author: wangjianbin
 * @Date: 2020/4/17 21:14
 */
@Data
public class AntAlipayParam implements Serializable {

    /**
     * 商户注册签约后，支付平台分配的唯
     * 一标识号
     */
    private String app_id;
    /**
     * 随机字符串，不长于32位。推荐使用U
     * UID
     */
    private String nonce_str;
    /**
     * 支付宝：ali_qr，微信：wx_qr
     */
    private String trade_type;
    /**
     * 订单总金额，单位为分的整数
     */
    private String total_amount;
    private String out_trade_no;
    private String trade_time;
    private String notify_url;
    private String user_ip;
    private String sign;
}
