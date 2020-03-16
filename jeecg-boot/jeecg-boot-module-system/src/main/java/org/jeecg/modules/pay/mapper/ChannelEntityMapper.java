package org.jeecg.modules.pay.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.pay.entity.ChannelEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * @Description: 通道设置
 * @Author: jeecg-boot
 * @Date:   2019-07-26
 * @Version: V1.0
 */
public interface ChannelEntityMapper extends BaseMapper<ChannelEntity> {
    ChannelEntity queryChannelByCode(@Param("channelCode") String channelCode);
    List<ChannelEntity> queryAllChannelCode();
    List<ChannelEntity> queryAgentChannelCodeByAgentName(@Param("userName") String userName);
    List<ChannelEntity> queryChannelByCodes(@Param("codes") List<String> codes);
}
