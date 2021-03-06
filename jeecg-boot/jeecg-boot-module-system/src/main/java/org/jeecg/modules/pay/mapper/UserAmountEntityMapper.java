package org.jeecg.modules.pay.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.jeecg.modules.pay.entity.UserAmountEntity;

import java.math.BigDecimal;

/**
 * @Description: 商户收入额度
 * @Author: jeecg-boot
 * @Date:   2019-07-31
 * @Version: V1.0
 */
public interface UserAmountEntityMapper extends BaseMapper<UserAmountEntity> {
    UserAmountEntity getUserAmountByUserName(@Param("userName")String userName);
    
    @Select("select * from sys_user_amount where user_id = #{userId}")
    UserAmountEntity getByUserId(@Param("userId") String userId);
    
    @Update("update sys_user_amount set amount = amount + #{newAmount} where user_id = #{id} and amount + #{newAmount} >= 0")
    boolean changeAmount(@Param("id") String id, @Param("newAmount") BigDecimal newAmount);
    
    @Update("update sys_user_amount set amount = amount + #{newAmount} where user_name = #{userName} and amount + #{newAmount} >= 0")
    boolean changeAmountByUserName(@Param("userName") String userName, @Param("newAmount") BigDecimal amount);
}
