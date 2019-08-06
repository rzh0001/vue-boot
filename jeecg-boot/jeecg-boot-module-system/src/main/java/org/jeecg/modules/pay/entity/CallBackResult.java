package org.jeecg.modules.pay.entity;

import io.swagger.models.auth.In;
import lombok.Data;

import java.io.Serializable;

/**
 * @title:
 * @Description: 通知商户订单支付成功之后，商户的返回信息
 * @author: wangjb
 * @create: 2019-08-06 11:34
 */
@Data
public class CallBackResult implements Serializable {
    /**
     * 商户返回200 说明商户处理成功
     */
    private Integer code;
    private String msg;
}
