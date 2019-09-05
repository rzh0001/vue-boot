package org.jeecg.modules.pay.service.impl;

import org.jeecg.modules.pay.entity.UserChannelEntity;
import org.jeecg.modules.pay.mapper.UserChannelEntityMapper;
import org.jeecg.modules.pay.service.IUserChannelEntityService;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.util.List;

/**
 * @Description: 用户关联通道
 * @Author: jeecg-boot
 * @Date:   2019-07-26
 * @Version: V1.0
 */
@Service
public class UserChannelEntityServiceImpl extends ServiceImpl<UserChannelEntityMapper, UserChannelEntity> implements IUserChannelEntityService {
    @Override
    public UserChannelEntity queryChannelAndUserName(String channelCode, String userName) {
        return baseMapper.queryChannelAndUser(channelCode,userName);
    }

    @Override
    public List<UserChannelEntity> queryChannelByUserName(String username) {
        return baseMapper.queryChannelByUserName(username);
    }

    @Override
    public void deleteUserChannel(String userName, String channelCode) {
        baseMapper.deleteUserChannel(userName,channelCode);
    }
}
