package org.jeecg.modules.df.vo;

import io.swagger.models.auth.In;
import lombok.Data;

/**
 * @author wangjianbin
 * @Description
 * @since 2020/11/19 15:07
 */
@Data
public class DeviceUserInfoVO {
    private String userName;
    private Integer status;
    private String deviceCode;
    private String deviceName;
}
