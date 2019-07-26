package org.jeecg.modules.pay.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.pay.entity.UserRateEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * @Description: 用户在指定通道下的费率
 * @Author: jeecg-boot
 * @Date:   2019-07-26
 * @Version: V1.0
 */
public interface UserRateEntityMapper extends BaseMapper<UserRateEntity> {

    String getUserRate(@Param("userId")String userId,@Param("channelCode")String channelCode);
}
