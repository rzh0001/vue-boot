package org.jeecg.modules.df.entity;

import com.alibaba.fastjson.JSON;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author ruanzh
 * @since 2019/11/17
 */
@Data
public class PayOrderResult {
    private String orderNo;
    private String bizOrderNo;
    private BigDecimal amount;
    private BigDecimal bizFee;
    
    public static PayOrderResult fromPayOrder(PayOrder o) {
        PayOrderResult data = new PayOrderResult();
        data.setOrderNo(o.getOrderId());
        data.setBizOrderNo(o.getOuterOrderId());
        data.setAmount(o.getAmount());
        data.setBizFee(o.getOrderFee());
        return data;
    }
    
    public String toJsonString() {
        return JSON.toJSONString(this);
    }
    
}
