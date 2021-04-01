package org.jeecg.modules.api.service;

import org.jeecg.modules.system.entity.SysUser;
import org.jeecg.modules.v2.entity.PayBusiness;
import org.jeecg.modules.v2.entity.PayUserChannel;

import java.math.BigDecimal;

public interface ICommonApiService {
    String getRate(PayUserChannel userChannel);
    String getGateWayUrl(PayUserChannel userChannel);
    /**
     * 获取通道信息
     * @param userName
     * @param productCode
     * @return
     */
    PayUserChannel findChannel(String userName, String productCode);

    PayBusiness findBusiness(String agentName, String channelCode, String productCode);

    void checkSubmitAmountLegal(BigDecimal submitAmount, PayUserChannel channel) throws Exception;

    /**
     * 校验通道信息
     * @param userName
     * @param productCode
     */
    void checkProduct(String userName, String productCode);

    void checkSalesmanRate(SysUser user,PayUserChannel userChannel);

    /**
     * 挂马账户金额是否满足本次订单申请
     * @param business
     * @param amount
     */
    void businessAmountIsLegal(PayBusiness business,BigDecimal amount);
}
