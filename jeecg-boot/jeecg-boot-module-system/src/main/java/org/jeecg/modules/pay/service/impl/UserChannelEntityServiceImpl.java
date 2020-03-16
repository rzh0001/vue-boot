package org.jeecg.modules.pay.service.impl;

import org.jeecg.modules.pay.entity.ChannelEntity;
import org.jeecg.modules.pay.entity.UserChannelEntity;
import org.jeecg.modules.pay.mapper.UserChannelEntityMapper;
import org.jeecg.modules.pay.service.IUserChannelEntityService;
import org.jeecg.modules.system.entity.SysUser;
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

    @Override
    public List<String> queryUserChannel(List<String> channelCodes, String userName) {
        return this.baseMapper.queryUserChannel(channelCodes,userName);
    }

    @Override
    public void updateUseTime(String channelCode, String userName) {
        this.baseMapper.updateUseTime(channelCode,userName);
    }

    @Override
    public List<String> getChannelCodeByUserName(String userName) {
        return baseMapper.getChannelCodeByUserName(userName);
    }

    @Override
    public void deleteChannel(String userName,List<String> codes) {
        baseMapper.deleteChannel(userName,codes);
    }

    @Override
    public void batchSave(List<ChannelEntity> channels, SysUser sysUser) {
        baseMapper.batchSave(channels,sysUser);
    }
}
