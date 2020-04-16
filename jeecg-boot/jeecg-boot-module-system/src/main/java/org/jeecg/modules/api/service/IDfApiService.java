package org.jeecg.modules.api.service;

import org.jeecg.modules.api.entity.ApiRequestBody;
import org.jeecg.modules.api.entity.ApiResponseBody;

/**
 * @author ruanzh
 * @since 2019/11/17
 */
public interface IDfApiService {
    /**
     * 创建订单
     *
     * @param req
     * @return bool
     */
    ApiResponseBody createOrder(ApiRequestBody req);
    
    ApiResponseBody queryOrder(ApiRequestBody req);
    
    boolean callback(String orderId);
    
    
}
