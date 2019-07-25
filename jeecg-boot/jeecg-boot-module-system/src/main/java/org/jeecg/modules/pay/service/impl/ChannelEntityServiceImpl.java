package org.jeecg.modules.pay.service.impl;

import org.jeecg.modules.pay.entity.ChannelEntity;
import org.jeecg.modules.pay.mapper.ChannelEntityMapper;
import org.jeecg.modules.pay.service.IChannelEntityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

/**
 * @Description: 通道信息
 * @Author: jeecg-boot
 * @Date:   2019-07-25
 * @Version: V1.0
 */
@Service
public class ChannelEntityServiceImpl extends ServiceImpl<ChannelEntityMapper, ChannelEntity> implements IChannelEntityService {

    @Override
    public ChannelEntity queryChannelByCode(String channelCode) {
        return baseMapper.queryChannelByCode(channelCode);
    }
}
