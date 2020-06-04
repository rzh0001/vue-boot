package org.jeecg.modules.v2.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @Author: wangjianbin
 * @Date: 2020/6/4 16:01
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChargeBusinessParam implements Serializable {
    private String userName;
    private String productCode;
    private String channelCode;
    private String businessCode;
    private String amount;
    private BigDecimal chargeAmount;
}
