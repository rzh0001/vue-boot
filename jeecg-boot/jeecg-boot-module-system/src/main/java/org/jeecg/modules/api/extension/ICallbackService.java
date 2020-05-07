package org.jeecg.modules.api.extension;

/**
 * @Author: wangjianbin
 * @Date: 2020/5/7 21:26
 */
public interface ICallbackService {
    /**
     *
     * @param orderId 平台订单号
     * @param payType 支付类型
     * @return
     * @throws Exception
     */
    Object callBack(String orderId,String payType) throws Exception;
}
