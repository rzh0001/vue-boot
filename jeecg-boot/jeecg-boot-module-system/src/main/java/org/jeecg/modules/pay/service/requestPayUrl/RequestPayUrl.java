package org.jeecg.modules.pay.service.requestPayUrl;

import org.jeecg.modules.util.R;

import java.io.IOException;
import java.net.URISyntaxException;

public interface RequestPayUrl<O, N, U, K, C, B,P> {

    /**
     * @param order        订单信息
     * @param userName     当前请求的商户用户名
     * @param url          请求的支付地址
     * @param key          请求秘钥
     * @param callbackUrl  挂马的回调地址
     * @param userBusiness 商户信息
     * @return
     * @throws Exception
     */
    R requestPayUrl(O order, N userName, U url, K key, C callbackUrl, B userBusiness) throws Exception;

    /**
     * @param order 订单
     * @param url   请求url
     * @return
     * @throws IOException
     * @throws URISyntaxException
     */
    boolean orderInfoOk(O order, U url, B userBusiness) throws Exception;


    /**
     * 通知挂马平台，扣除手续费
     * @param order
     * @param key
     * @param userBusiness
     * @return
     * @throws Exception
     */
    boolean notifyOrderFinish(O order,K key,B userBusiness,U url) throws Exception;

    /**
     * 回调商户
     * 使用条件：要想使用此回调，必须要求第三方在创建订单的时候，有一个参数，在回调的时候，原样返回。如果没有的话，则不能使用此回调
     * @param object ：挂马平台传递过来的参数
     * @return
     * @throws Exception
     */
    P callBack(P object) throws Exception;
}
