package org.jeecg.modules.pay.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.pay.entity.UserAmountEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * @Description: 商户收入额度
 * @Author: jeecg-boot
 * @Date:   2019-07-31
 * @Version: V1.0
 */
public interface UserAmountEntityMapper extends BaseMapper<UserAmountEntity> {
    UserAmountEntity getUserAmountByUserId(@Param("userId")String userId);

}
