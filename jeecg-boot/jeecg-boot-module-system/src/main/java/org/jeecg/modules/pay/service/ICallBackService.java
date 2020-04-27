package org.jeecg.modules.pay.service;

import java.util.Map;

/**
 * @Author: wangjianbin
 * @Date: 2020/3/15 22:47
 */
public interface ICallBackService {
    String callBack4niuNanAlipay() throws Exception;
    String callBackTengFeiAlipay() throws  Exception;
    String callBackLeTianAlipay() throws Exception;
    String callBackAntAlipay() throws Exception;
    String callBackGtpaiAlipay() throws Exception;
    String getApikey(String orderNo,String type)throws Exception;
    String notify(String orderNo, String payType) throws Exception;
    Map<String, Object> getParam()throws Exception;

    Object callBack(String orderNoField,String payType) throws Exception;
}
