package org.jeecg.modules.v2.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * @Author: wangjianbin
 * @Date: 2020/6/2 11:25
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserChannelParam implements Serializable {
    private String userName;
    private String memberType;
    private String productCode;
    private String ChannelCode;
    private String userRate;
    private BigDecimal upperLimit;
    private BigDecimal lowerLimit;
    //挂马信息
    private String businessCode;
    private String businessApiKey;
    private String businessActiveStatus;
}
