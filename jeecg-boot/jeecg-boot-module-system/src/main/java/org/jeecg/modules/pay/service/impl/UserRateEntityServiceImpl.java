package org.jeecg.modules.pay.service.impl;

import org.jeecg.modules.pay.entity.UserRateEntity;
import org.jeecg.modules.pay.mapper.UserRateEntityMapper;
import org.jeecg.modules.pay.service.IUserRateEntityService;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.util.List;

/**
 * @Description: 用户在指定通道下的费率
 * @Author: jeecg-boot
 * @Date:   2019-07-26
 * @Version: V1.0
 */
@Service
public class UserRateEntityServiceImpl extends ServiceImpl<UserRateEntityMapper, UserRateEntity> implements IUserRateEntityService {

    @Override
    public String getUserRateByUserNameAndAngetCode(String userName,String agentUsername,String payType) {
        return baseMapper.getUserRateByUserNameAndAngetCode(userName, agentUsername,payType);
    }

    @Override
    public String getBeIntroducerRate(String userName, String agentUsername, String beIntroducerName, String payType) {
        return baseMapper.getBeIntroducerRate(userName,agentUsername,beIntroducerName,payType);
    }

    @Override
    public List<UserRateEntity> queryUserRate(String username) {
        return baseMapper.queryUserRate(username);
    }

    @Override
    public void deleteUserRate(UserRateEntity dto) {
        baseMapper.deleteUserRate(dto);
    }

    @Override
    public List<String> getBeIntroducerName(String userName) {
        return baseMapper.getBeIntroducerName(userName);
    }
}
