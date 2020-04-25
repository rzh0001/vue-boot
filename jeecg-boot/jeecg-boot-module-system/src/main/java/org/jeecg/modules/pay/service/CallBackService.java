package org.jeecg.modules.pay.service;

/**
 * @Author: wangjianbin
 * @Date: 2020/4/24 21:46
 */
public interface CallBackService {
    /**
     *
     * @param orderNoField 三方指定的订单属性名称
     * @param payType 支付类型
     * @return
     * @throws Exception
     */
    Object callBack(String orderNoField,String payType) throws Exception;
}
