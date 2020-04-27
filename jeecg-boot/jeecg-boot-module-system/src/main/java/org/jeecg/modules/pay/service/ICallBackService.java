package org.jeecg.modules.pay.service;

import java.util.Map;

/**
 * @Author: wangjianbin
 * @Date: 2020/3/15 22:47
 */
public interface ICallBackService {
    String getApikey(String orderNo,String type)throws Exception;
    Map<String, Object> getParam()throws Exception;
    Object callBack(String orderNoField,String payType) throws Exception;
}
