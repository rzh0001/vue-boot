package org.jeecg.modules.pay.service;

import com.alibaba.fastjson.JSONObject;
import org.jeecg.modules.pay.entity.OrderInfoEntity;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.util.R;

import javax.servlet.http.HttpServletRequest;

/**
 * @Description: 订单信息
 * @Author: jeecg-boot
 * @Date:   2019-07-26
 * @Version: V1.0
 */
public interface IOrderInfoEntityService extends IService<OrderInfoEntity> {
    R createOrder(JSONObject reqobj);
    /**
     * 查询订单信息
     * @param reqobj
     */
    R queryOrderInfo(JSONObject reqobj);

    /**
     * 回调
     * @param reqobj
     * @return
     */
    R callback(JSONObject reqobj, HttpServletRequest req);

    OrderInfoEntity queryOrderInfoByOrderId(String orderId);


}
