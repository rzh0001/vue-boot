package org.jeecg.modules.pay.service.impl;

import org.jeecg.modules.pay.entity.ChannelUserEntity;
import org.jeecg.modules.pay.mapper.ChannelUserEntityMapper;
import org.jeecg.modules.pay.service.IChannelUserEntityService;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

/**
 * @Description: 用户关联通道信息
 * @Author: jeecg-boot
 * @Date:   2019-07-25
 * @Version: V1.0
 */
@Service
public class ChannelUserEntityServiceImpl extends ServiceImpl<ChannelUserEntityMapper, ChannelUserEntity> implements IChannelUserEntityService {

    @Override
    public ChannelUserEntity queryChannelAndUser(String channelCode, String userId) {
        return baseMapper.queryChannelAndUser(channelCode,userId);
    }
}
