package org.jeecg.modules.api.service;

import org.jeecg.modules.api.entity.ApiRequestBody;
import org.jeecg.modules.api.entity.ApiResponseBody;
import org.jeecg.modules.api.entity.PayOrderRequestData;
import org.jeecg.modules.api.entity.PayOrderUrlResponse;
import org.jeecg.modules.pay.entity.OrderInfoEntity;
import org.jeecg.modules.system.entity.SysUser;
import org.jeecg.modules.v2.entity.PayBusiness;
import org.jeecg.modules.v2.entity.PayUserChannel;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.Map;

public interface ISfApiService {

    PayOrderUrlResponse createOrder(OrderInfoEntity orderInfo,SysUser user) throws Exception;

    ApiResponseBody queryOrder(String outerOrderId, String username);

    /**
     * 回调
     * 
     * @param payType
     *            通道代码
     * @param orderId
     *            平台订单号
     * @return
     * @throws Exception
     */
    Object callback(String payType, String orderId) throws Exception;

    /**
     * 校验用户信息
     * 
     * @param reqBody
     * @return
     * @throws Exception
     */
    SysUser verifyAccountInfo(ApiRequestBody reqBody) throws Exception;

    /**
     * 验证签名
     * 
     * @param reqBody
     * @param apiKey
     * @throws Exception
     */
    void verifySignature(ApiRequestBody reqBody, String apiKey) throws Exception;

    /**
     * 数据解密
     * 
     * @param reqBody
     * @param user
     * @return
     * @throws Exception
     */
    PayOrderRequestData decodeData(ApiRequestBody reqBody, SysUser user) throws Exception;





}
