package org.jeecg.modules.pay.service;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.pay.entity.OrderInfoEntity;
import org.jeecg.modules.system.entity.SysUser;
import org.jeecg.modules.util.R;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @Description: 订单信息
 * @Author: jeecg-boot
 * @Date: 2019-07-26
 * @Version: V1.0
 */
public interface IOrderInfoEntityService extends IService<OrderInfoEntity> {
    R createOrder(JSONObject reqobj, HttpServletRequest req) throws Exception;

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
    public R notifyCustomer(OrderInfoEntity order, SysUser user, String payType) throws Exception;
    Object innerSysCallBack(String payTpye,Object param) throws Exception;
    Map<String, Object> isInternalSystem(Map<String, Object> param);
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
    
    Map<String, Object> summary(Wrapper wrapper);
    
    Map<String, Object> summaryUserTodayOrderAmount(String userId, Date date);

    /**
     * 通知挂马扣除手续费
     * @param orderId
     * @param payType
     */
    boolean notifyOrderFinish(String orderId,String payType) throws Exception;
    List<String> getOrderByTime(String time);

    /**
     * 手动调整金额商户，根据订单号，更新马商的收入金额
     * @param orderId
     */
    void updateCustomerIncomeAmount(String orderId, BigDecimal amount) throws Exception;


}
