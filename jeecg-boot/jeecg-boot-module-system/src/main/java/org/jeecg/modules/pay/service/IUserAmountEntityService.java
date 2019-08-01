package org.jeecg.modules.pay.service;

import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.pay.entity.UserAmountEntity;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @Description: 商户收入额度
 * @Author: jeecg-boot
 * @Date:   2019-07-31
 * @Version: V1.0
 */
public interface IUserAmountEntityService extends IService<UserAmountEntity> {
    UserAmountEntity getUserAmountByUserName(String userName);
}
