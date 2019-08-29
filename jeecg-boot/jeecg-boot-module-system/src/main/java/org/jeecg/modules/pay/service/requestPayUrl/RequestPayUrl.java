package org.jeecg.modules.pay.service.requestPayUrl;

import org.jeecg.modules.util.R;

import java.io.IOException;
import java.net.URISyntaxException;

public interface RequestPayUrl<O ,N ,U,K,C,B> {

    /**
     *
     * @param order 订单
     * @param userName 用户名
     * @param url 支付地址
     * @param key 秘钥
     * @param callbackUrl 回调地址
     * @return
     * @throws Exception
     */
    R requestPayUrl(O order,N userName ,U url,K key,C callbackUrl,B userBusiness) throws Exception;

    /**
     *
     * @param order 订单
     * @param url 请求url
     * @return
     * @throws IOException
     * @throws URISyntaxException
     */
    boolean orderInfoOk(O order,U url,B userBusiness) throws Exception;
}
