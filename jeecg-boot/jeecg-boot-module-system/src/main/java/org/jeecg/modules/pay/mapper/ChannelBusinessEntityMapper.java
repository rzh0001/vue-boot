package org.jeecg.modules.pay.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.pay.entity.ChannelBusinessEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.jeecg.modules.pay.entity.ChannelEntity;

/**
 * @Description: 通道关联商户
 * @Author: jeecg-boot
 * @Date:   2019-07-26
 * @Version: V1.0
 */
public interface ChannelBusinessEntityMapper extends BaseMapper<ChannelBusinessEntity> {
    ChannelBusinessEntity queryChannelBusiness(@Param("businessCode") String businessCode, @Param("channelCode")String channelCode);

    ChannelBusinessEntity queryChannelBusinessByUserName(@Param("userName") String userName);
}
