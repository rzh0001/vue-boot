package org.jeecg.modules.pay.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.pay.entity.UserAmountDetail;
import org.jeecg.modules.system.entity.SysUser;

import java.math.BigDecimal;

/**
 * @Description: 用户收入流水详情
 * @Author: jeecg-boot
 * @Date:   2019-08-26
 * @Version: V1.0
 */
public interface IUserAmountDetailService extends IService<UserAmountDetail> {
    
    /**
     * 创建收入流水并保存
     *
     * @param amount
     * @param type
     * @param opUser
     * @return
     */
    boolean addAmountDetail(BigDecimal amount, BigDecimal originalAmount, String type, SysUser opUser);
    
    /**
     * 获取用户总收入
     *
     * @param userId
     * @return
     */
    BigDecimal getTotalIncome(String userId);
    
    /**
     * 获取用户今日收入
     *
     * @param userId
     * @return
     */
    BigDecimal getTodayIncome(String userId);
}
