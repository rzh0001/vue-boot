package org.jeecg.modules.pay.service.requestPayUrl;

import org.jeecg.modules.util.R;

public interface RequestPayUrl<T,V,K> {
    /**
     *
     * @param t 请求参数
     * @param v 请求地址
     * @param k 加密秘钥
     * @return
     */
    R requestPayUrl(T t,V v,K  k) throws Exception;
}
