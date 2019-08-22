package org.jeecg.modules.pay.service.impl;

import org.jeecg.modules.pay.entity.UserBusinessEntity;
import org.jeecg.modules.pay.mapper.UserBusinessEntityMapper;
import org.jeecg.modules.pay.service.IUserBusinessEntityService;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.util.List;

/**
 * @Description: 用户关联商户
 * @Author: jeecg-boot
 * @Date:   2019-07-26
 * @Version: V1.0
 */
@Service
public class UserBusinessEntityServiceImpl extends ServiceImpl<UserBusinessEntityMapper, UserBusinessEntity> implements IUserBusinessEntityService {

    @Override
    public List<UserBusinessEntity> queryBusinessCodeByUserName(String userName, String channelCode) {
        return baseMapper.queryBusinessCodeByUserName(userName,channelCode);
    }
}
