package org.jeecg.modules.pay.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @title:
 * @Description:
 * @author: wangjb
 * @create: 2019-08-19 12:50
 */
@Data
public class YsfData implements Serializable {
    private Integer status;
    private String orderid;
    private String agentorderid;
    private Double applyamount;
    private Integer orderchannel;
    private Date createtime;
    private Date paytime;

}
