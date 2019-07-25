package org.jeecg.modules.pay.mapper;

import java.util.List;

import org.jeecg.modules.pay.entity.ChannelEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.data.repository.query.Param;

/**
 * @Description: 通道信息
 * @Author: jeecg-boot
 * @Date:   2019-07-25
 * @Version: V1.0
 */
public interface ChannelEntityMapper extends BaseMapper<ChannelEntity> {
    ChannelEntity queryChannelByCode(@Param("channelCode") String channelCode);
}
