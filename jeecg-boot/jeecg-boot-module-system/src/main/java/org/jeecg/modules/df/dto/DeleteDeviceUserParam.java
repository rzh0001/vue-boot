package org.jeecg.modules.df.dto;

import lombok.Data;

/**
 * @author wangjianbin
 * @Description
 * @since 2020/11/19 15:31
 */
@Data
public class DeleteDeviceUserParam {
    private String deviceCode;
    private String userName;
}
