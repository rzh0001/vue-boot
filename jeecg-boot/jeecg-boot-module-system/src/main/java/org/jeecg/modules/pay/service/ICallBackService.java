package org.jeecg.modules.pay.service;

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
}
