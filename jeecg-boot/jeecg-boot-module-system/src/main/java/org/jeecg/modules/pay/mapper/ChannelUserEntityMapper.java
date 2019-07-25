package org.jeecg.modules.pay.mapper;

import java.util.List;

import org.jeecg.modules.pay.entity.ChannelUserEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.data.repository.query.Param;

/**
 * @Description: 用户关联通道信息
 * @Author: jeecg-boot
 * @Date:   2019-07-25
 * @Version: V1.0
 */
public interface ChannelUserEntityMapper extends BaseMapper<ChannelUserEntity> {

    ChannelUserEntity queryChannelAndUser(@Param("channelCode") String channelCode,@Param("userId") String userId);
}
