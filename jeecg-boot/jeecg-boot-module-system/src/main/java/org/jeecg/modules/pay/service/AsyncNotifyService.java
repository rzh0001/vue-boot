package org.jeecg.modules.pay.service;

/**
 * @Author: wangjianbin
 * @Date: 2020/4/27 14:27
 */
public interface AsyncNotifyService {
    void asyncNotify(String orderNo, String payType) throws Exception;
}
