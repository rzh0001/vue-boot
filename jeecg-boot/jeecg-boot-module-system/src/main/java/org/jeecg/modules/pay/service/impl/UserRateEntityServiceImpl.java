package org.jeecg.modules.pay.service.impl;

import org.jeecg.modules.pay.entity.UserRateEntity;
import org.jeecg.modules.pay.mapper.UserRateEntityMapper;
import org.jeecg.modules.pay.service.IUserRateEntityService;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

/**
 * @Description: 用户在指定通道下的费率
 * @Author: jeecg-boot
 * @Date:   2019-07-26
 * @Version: V1.0
 */
@Service
public class UserRateEntityServiceImpl extends ServiceImpl<UserRateEntityMapper, UserRateEntity> implements IUserRateEntityService {

    @Override
    public String getUserRateByUserName(String userName) {
        return baseMapper.getUserRateByUserName(userName);
    }
}
