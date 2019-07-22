package org.jeecg.modules.pay.service;

import com.alibaba.fastjson.JSONObject;
import org.jeecg.modules.pay.entity.OrderInfoEntity;
import org.jeecg.modules.util.R;

/**
 * @title:
 * @Description:
 * @author: wangjb
 * @create: 2019-07-22 10:35
 */
public interface IOrderInfoService {
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
    R callback(JSONObject reqobj);

    OrderInfoEntity queryOrderInfoByOrderId(String orderId);
}
