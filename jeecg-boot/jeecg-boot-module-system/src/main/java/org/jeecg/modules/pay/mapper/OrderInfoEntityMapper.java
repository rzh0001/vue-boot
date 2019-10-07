package org.jeecg.modules.pay.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.ResultType;
import org.apache.ibatis.annotations.Select;
import org.jeecg.modules.pay.entity.OrderInfoEntity;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description: 订单信息
 * @Author: jeecg-boot
 * @Date:   2019-07-26
 * @Version: V1.0
 */
public interface OrderInfoEntityMapper extends BaseMapper<OrderInfoEntity> {

    @Select("select order_id from pay_order_info where status in(-1,0) and create_time<#{time}")
    List<String> getOrderByTime(@Param("time")String time);
    String queryOrderByOuterOrderId(@Param("outerOrderId") String outerOrderId);
    OrderInfoEntity queryOrderByOrderId(@Param("orderId") String orderId);
    void updateOrderStatusSuccessByOrderId(@Param("orderId") String orderId);
    void updateOrderStatusNoBackByOrderId(@Param("orderId") String orderId);

    int updateOrderStatusBatch(@Param("orderIds") List<String> orderIds);
    
    Map<String, Object> summary(@Param(Constants.WRAPPER) Wrapper wrapper);
    
    @Select("select count(1) as paidCount,sum(submit_amount) as paidAmount, " +
            "sum(poundage) as payFee " +
            "from pay_order_info " +
            "where status = '2' and user_id = #{userId} " +
            "and to_days(success_time) = to_days(#{date})")
    @ResultType(HashMap.class)
    Map<String, Object> summaryUserTodayOrderAmount(@Param("userId") String userId, @Param("date") Date date);
}
