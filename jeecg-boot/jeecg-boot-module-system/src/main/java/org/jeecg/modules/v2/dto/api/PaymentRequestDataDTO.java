package org.jeecg.modules.v2.dto.api;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
@Data
public class PaymentRequestDataDTO implements Serializable {
    /**
     * 商户名称
     */
    @NotNull(message = "商户名称不能为空")
    private String userName;
    /**
     * 产品类型
     */
    @NotNull(message = "产品类型不能为空")
    private String productName;
    /**
     * 申请金额 *10000
     */
    @NotNull(message = "申请金额不能为空")
    @Min(value = 10000)
    private Long amount;
    /**
     * 订单号
     */
    @NotNull(message = "商户订单号不能为空")
    private String orderId;
    /**
     * 回调地址
     */
    @NotNull(message = "商户回调地址不能为空")
    private String callbackUrl;
    /**
     * IP地址
     */
    private String ip;
}
