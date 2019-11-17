package org.jeecg.modules.df.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.jeecg.modules.df.entity.PayOrder;

/**
 * @Description: 1
 * @Author: jeecg-boot
 * @Date:   2019-10-27
 * @Version: V1.0
 */
public interface PayOrderMapper extends BaseMapper<PayOrder> {
    
    @Select("select count(*) from df_pay_order where user_id = #{userId} and outer_order_id = #{outerOrderId}")
    int count(@Param("userId") String userId, @Param("outerOrderId") String outerOrderId);
    
    @Select("select *from df_pay_order where outer_order_id = #{outerOrderId}")
    PayOrder getByOuterOrderId(@Param("outerOrderId") String outerOrderId);
}
