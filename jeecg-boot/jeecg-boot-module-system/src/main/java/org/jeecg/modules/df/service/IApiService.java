package org.jeecg.modules.df.service;

import org.jeecg.modules.df.entity.CommonRequestBody;
import org.jeecg.modules.df.entity.CommonResponseBody;

/**
 * @author ruanzh
 * @since 2019/11/17
 */
public interface IApiService {
    /**
     * 创建订单
     *
     * @param req
     * @return bool
     */
    CommonResponseBody createOrder(CommonRequestBody req);
}
