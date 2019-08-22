package org.jeecg.modules.pay.service.impl;

import org.jeecg.modules.pay.entity.ChannelEntity;
import org.jeecg.modules.pay.mapper.ChannelEntityMapper;
import org.jeecg.modules.pay.service.IChannelEntityService;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.util.List;

/**
 * @Description: 通道设置
 * @Author: jeecg-boot
 * @Date:   2019-07-26
 * @Version: V1.0
 */
@Service
public class ChannelEntityServiceImpl extends ServiceImpl<ChannelEntityMapper, ChannelEntity> implements IChannelEntityService {
    @Override
    public ChannelEntity queryChannelByCode(String channelCode) {
        return baseMapper.queryChannelByCode(channelCode);
    }

    @Override
    public List<ChannelEntity> queryAllChannelCode() {
        return baseMapper.queryAllChannelCode();
    }

    @Override
    public List<ChannelEntity> queryAgentChannelCodeByAgentName(String userName) {
        return baseMapper.queryAgentChannelCodeByAgentName(userName);
    }
}
