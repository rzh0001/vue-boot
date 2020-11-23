package org.jeecg.modules.df.dto;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.constant.PayOrderStatusEnum;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @author wangjianbin
 * @Description
 * @since 2020/11/23 9:43
 */
@Data
@Slf4j
public class CallbackParamDTO implements Serializable {
    @NotEmpty(message = "订单号不能为空")
    private String orderNo;
    @NotEmpty(message = "设备号不能为空")
    private String deviceCode;
    @NotEmpty(message = "签名不能为空")
    private String sign;
    @NotNull(message = "状态不能为空")
    private PayOrderStatusEnum status;

    public static final String ORDER_NO_COL = "orderNo";
    public static final String DEVICE_CODE = "deviceCode";
    public static final String DEVICE_KEY = "device_key";
}
