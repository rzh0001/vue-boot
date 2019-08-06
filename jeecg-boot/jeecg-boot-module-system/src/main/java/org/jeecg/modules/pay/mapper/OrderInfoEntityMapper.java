package org.jeecg.modules.pay.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.pay.entity.OrderInfoEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * @Description: 订单信息
 * @Author: jeecg-boot
 * @Date:   2019-07-26
 * @Version: V1.0
 */
public interface OrderInfoEntityMapper extends BaseMapper<OrderInfoEntity> {

    String queryOrderByOuterOrderId(@Param("outerOrderId") String outerOrderId);
    OrderInfoEntity queryOrderByOrderId(@Param("orderId") String orderId);
    void updateOrderStatusSuccessByOrderId(@Param("orderId") String orderId);
    void updateOrderStatusNoBackByOrderId(@Param("orderId") String orderId);
}
