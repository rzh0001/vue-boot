package org.jeecg.modules.pay.service;

import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.pay.entity.UserRateEntity;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @Description: 用户在指定通道下的费率
 * @Author: jeecg-boot
 * @Date:   2019-07-26
 * @Version: V1.0
 */
public interface IUserRateEntityService extends IService<UserRateEntity> {
    String getUserRateByUserName(String userName);
}
