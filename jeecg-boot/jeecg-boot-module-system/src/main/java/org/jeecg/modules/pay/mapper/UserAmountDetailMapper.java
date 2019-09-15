package org.jeecg.modules.pay.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.jeecg.modules.pay.entity.UserAmountDetail;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @Description: 用户收入流水详情
 * @Author: jeecg-boot
 * @Date:   2019-08-26
 * @Version: V1.0
 */
public interface UserAmountDetailMapper extends BaseMapper<UserAmountDetail> {
    
    /**
     * 获取用户总收入
     *
     * @param userId
     * @return
     */
    BigDecimal getTotalIncome(@Param("userId") String userId);
    
    /**
     * 获取用户今日收入
     *
     * @param userId
     * @return
     */
    BigDecimal getTodayIncome(@Param("userId") String userId);
    
    @Select("select * from sys_user_amount_detail where user_id = #{userId} " +
            "and to_days(create_time) = to_days(#{date}) order by create_time limit 1")
    UserAmountDetail getUserOriginalAmount(@Param("userId") String userId, @Param("date") Date date);
    
}
