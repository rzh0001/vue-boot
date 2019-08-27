package org.jeecg.modules.pay.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
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
@Service
public class UserAmountDetailServiceImpl extends ServiceImpl<UserAmountDetailMapper, UserAmountDetail> implements IUserAmountDetailService {
    
    @Override
    public boolean addAmountDetail(BigDecimal amount, String type, SysUser opUser) {
        UserAmountDetail detail = new UserAmountDetail();
        detail.setUserId(opUser.getId());
        detail.setUserName(opUser.getUsername());
        detail.setType(type);
        detail.setAmount(amount);
        detail.setAgentId(opUser.getAgentId());
        detail.setAgentUsername(opUser.getAgentUsername());
        detail.setAgentRealname(opUser.getAgentRealname());
        detail.setSalesmanId(opUser.getSalesmanId());
        detail.setSalesmanUsername(opUser.getSalesmanUsername());
        detail.setSalesmanRealname(opUser.getSalesmanRealname());
        return save(detail);
    }
}
