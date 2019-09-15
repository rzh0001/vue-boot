package org.jeecg.modules.pay.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.constant.PayConstant;
import org.jeecg.modules.exception.RRException;
import org.jeecg.modules.pay.entity.UserAmountEntity;
import org.jeecg.modules.pay.mapper.UserAmountEntityMapper;
import org.jeecg.modules.pay.service.IUserAmountEntityService;
import org.jeecg.modules.system.entity.SysUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * @Description: 商户收入额度
 * @Author: jeecg-boot
 * @Date:   2019-07-31
 * @Version: V1.0
 */
@Slf4j
@Service
public class UserAmountEntityServiceImpl extends ServiceImpl<UserAmountEntityMapper, UserAmountEntity> implements IUserAmountEntityService {
    
    @Autowired
    private UserAmountEntityMapper mapper;
    
    
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
    
    @Override
    public boolean changeAmount(String userId, BigDecimal amount) {
        boolean ok = mapper.changeAmount(userId, amount);
        if (!ok) {
            throw new RRException("更新余额失败");
        }
        return true;
    }
    
    @Override
    public BigDecimal getUserAmount(String userId) {
        UserAmountEntity amount = baseMapper.getByUserId(userId);
        if (amount == null) {
            throw new RRException("获取余额失败");
        }
        return amount.getAmount();
    }
    
    @Override
    public void changeAmountByUserName(String userName, BigDecimal amount) {
        mapper.changeAmountByUserName(userName,amount);
    }
}
