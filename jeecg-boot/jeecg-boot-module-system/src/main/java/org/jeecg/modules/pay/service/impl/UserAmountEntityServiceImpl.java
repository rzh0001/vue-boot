package org.jeecg.modules.pay.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.common.constant.PayConstant;
import org.jeecg.modules.pay.entity.UserAmountEntity;
import org.jeecg.modules.pay.mapper.UserAmountEntityMapper;
import org.jeecg.modules.pay.service.IUserAmountEntityService;
import org.jeecg.modules.system.entity.SysUser;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * @Description: 商户收入额度
 * @Author: jeecg-boot
 * @Date:   2019-07-31
 * @Version: V1.0
 */
@Service
public class UserAmountEntityServiceImpl extends ServiceImpl<UserAmountEntityMapper, UserAmountEntity> implements IUserAmountEntityService {
    
    
    @Override
    public UserAmountEntity getUserAmountByUserName(String userName) {
        return baseMapper.getUserAmountByUserName(userName);
    }
    
    @Override
    public void initialUserAmount(SysUser user) {
        
        UserAmountEntity amount = new UserAmountEntity();
        amount.setAmount(BigDecimal.ZERO);
        amount.setUserId(user.getId());
        amount.setUserName(user.getUsername());
        amount.setAgentId(user.getMemberType().equals(PayConstant.MEMBER_TYPE_AGENT) ? null : user.getAgentId());
        this.save(amount);
    }
}
