package org.jeecg.modules.pay.entity;

import java.io.Serializable;

/**
 * @title:
 * @Description:
 * @author: wangjb
 * @create: 2019-08-06 10:24
 */
@lombok.Data
public class QueryOrderStatusResult implements Serializable {
    private Integer code;
    private String msg;
    private Data data;
}
