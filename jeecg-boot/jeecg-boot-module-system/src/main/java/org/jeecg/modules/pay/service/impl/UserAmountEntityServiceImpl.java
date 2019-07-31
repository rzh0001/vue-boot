package org.jeecg.modules.pay.service.impl;

import org.jeecg.modules.pay.entity.UserAmountEntity;
import org.jeecg.modules.pay.mapper.UserAmountEntityMapper;
import org.jeecg.modules.pay.service.IUserAmountEntityService;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

/**
 * @Description: 商户收入额度
 * @Author: jeecg-boot
 * @Date:   2019-07-31
 * @Version: V1.0
 */
@Service
public class UserAmountEntityServiceImpl extends ServiceImpl<UserAmountEntityMapper, UserAmountEntity> implements IUserAmountEntityService {

    @Override
    public UserAmountEntity getUserAmountByUserId(String userId) {
        return baseMapper.getUserAmountByUserId(userId);
    }
}
