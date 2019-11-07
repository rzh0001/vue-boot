package org.jeecg.modules.pay.service.requestPayUrl;

import org.jeecg.modules.util.R;

import java.io.IOException;
import java.net.URISyntaxException;

public interface RequestPayUrl<O, N, U, K, C, B,P> {

    /**
     * @param order        订单
     * @param userName     用户名
     * @param url          支付地址
     * @param key          秘钥
     * @param callbackUrl  回调地址
     * @param userBusiness
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
     * 如果是使用post方式回调的话，要求必须是json格式的入参，且必须要有payType字段
     * 如果是使用get方式回调的话，必要要有payType参数
     * @param object ：挂马平台传递过来的参数
     * @return
     * @throws Exception
     */
    P callBack(P object) throws Exception;
}
