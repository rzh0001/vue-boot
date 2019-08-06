package org.jeecg.modules.pay.entity;

import java.io.Serializable;

/**
 * @title:
 * @Description:
 * @author: wangjb
 * @create: 2019-08-06 10:25
 */
@lombok.Data
public class Data implements Serializable {
    private String status;
    private String creation_time;
    private String trade_no;
    private String out_trade_no;
    private String amount;
}
