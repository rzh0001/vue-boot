package org.jeecg.modules.pay.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.pay.entity.UserAmountDetail;
import org.jeecg.modules.pay.mapper.UserAmountDetailMapper;
import org.jeecg.modules.pay.service.IUserAmountDetailService;
import org.jeecg.modules.system.entity.SysUser;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * @Description: 用户收入流水详情
 * @Author: jeecg-boot
 * @Date:   2019-08-26
 * @Version: V1.0
 */
@Slf4j
@Service
public class UserAmountDetailServiceImpl extends ServiceImpl<UserAmountDetailMapper, UserAmountDetail> implements IUserAmountDetailService {
    
    @Override
    public boolean addAmountDetail(BigDecimal amount, BigDecimal originalAmount, String type, SysUser opUser) {
        UserAmountDetail detail = new UserAmountDetail();
        detail.setUserId(opUser.getId());
        detail.setUserName(opUser.getUsername());
        detail.setType(type);
        detail.setAmount(amount);
        detail.setInitialAmount(originalAmount);
        detail.setUpdateAmount(amount.add(originalAmount));
        detail.setAgentId(opUser.getAgentId());
        detail.setAgentUsername(opUser.getAgentUsername());
        detail.setAgentRealname(opUser.getAgentRealname());
        detail.setSalesmanId(opUser.getSalesmanId());
        detail.setSalesmanUsername(opUser.getSalesmanUsername());
        detail.setSalesmanRealname(opUser.getSalesmanRealname());
        return save(detail);
    }
    
    @Override
    public BigDecimal getTotalIncome(String userId) {
        return baseMapper.getTotalIncome(userId);
    }
    
    @Override
    public BigDecimal getTodayIncome(String userId) {
        return baseMapper.getTodayIncome(userId);
    }
}
