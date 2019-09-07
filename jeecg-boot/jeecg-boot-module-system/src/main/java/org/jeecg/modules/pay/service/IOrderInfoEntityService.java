package org.jeecg.modules.pay.service;

import com.alibaba.fastjson.JSONObject;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.pay.entity.OrderInfoEntity;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.util.R;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @Description: 订单信息
 * @Author: jeecg-boot
 * @Date: 2019-07-26
 * @Version: V1.0
 */
public interface IOrderInfoEntityService extends IService<OrderInfoEntity> {
    R createOrder(JSONObject reqobj) throws Exception;

    /**
     * 查询订单信息
     *
     * @param reqobj
     */
    R queryOrderInfo(JSONObject reqobj);

    /**
     * 回调
     *
     * @param reqobj
     * @return
     */
    R callback(JSONObject reqobj, HttpServletRequest req) throws Exception;

    OrderInfoEntity queryOrderInfoByOrderId(String orderId);

    /**
     * 更新订单状态为支付已返回
     *
     * @param orderId
     */
    void updateOrderStatusSuccessByOrderId(@Param("orderId") String orderId);

    /**
     * 更新订单状态为支付未返回
     *
     * @param orderId
     */
    void updateOrderStatusNoBackByOrderId(@Param("orderId") String orderId);

    JSONObject encryptAESData(OrderInfoEntity order, String aseKey) throws Exception;

    void countAmount(String orderId, String userName, String submitAmount, String payType) throws Exception;

    int updateOrderStatusBatch(List<String> orderIds);
}
