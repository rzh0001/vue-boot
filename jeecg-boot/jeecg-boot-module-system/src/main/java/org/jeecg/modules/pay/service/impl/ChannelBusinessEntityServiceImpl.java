package org.jeecg.modules.pay.service.impl;

import org.jeecg.modules.pay.entity.ChannelBusinessEntity;
import org.jeecg.modules.pay.mapper.ChannelBusinessEntityMapper;
import org.jeecg.modules.pay.service.IChannelBusinessEntityService;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

/**
 * @Description: 通道关联商户
 * @Author: jeecg-boot
 * @Date:   2019-07-26
 * @Version: V1.0
 */
@Service
public class ChannelBusinessEntityServiceImpl extends ServiceImpl<ChannelBusinessEntityMapper, ChannelBusinessEntity> implements IChannelBusinessEntityService {

    @Override
    public ChannelBusinessEntity queryChannelBusiness(String businessCode, String channelCode) {
        return baseMapper.queryChannelBusiness(businessCode,channelCode);
    }

    @Override
    public ChannelBusinessEntity queryChannelBusinessByUserId(String userId) {
        return baseMapper.queryChannelBusinessByUserId(userId);
    }
}
