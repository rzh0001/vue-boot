package org.jeecg.modules.pay.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.pay.entity.UserAmountEntity;
import org.jeecg.modules.system.entity.SysUser;

import java.math.BigDecimal;

/**
 * @Description: 商户收入额度
 * @Author: jeecg-boot
 * @Date:   2019-07-31
 * @Version: V1.0
 */
public interface IUserAmountEntityService extends IService<UserAmountEntity> {
    UserAmountEntity getUserAmountByUserName(String userName);
    
    /**
     * 初始化会员余额表
     *
     * @param user
     */
    void initialUserAmount(SysUser user);
    
    /**
     * 更新余额
     *
     * @param id
     * @param amount
     * @return
     */
    boolean changeAmount(String userId, BigDecimal amount);

    void changeAmountByUserName(String userName,BigDecimal amount);
}
