package org.jeecg.modules.wallet.service;

import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.util.WalletHttpRequestUtils;
import org.jeecg.modules.wallet.service.impl.PayWalletUrlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class WalletService {
    @Autowired
    private PayWalletUrlService walletUrlService;
    /**
     *获取可用的钱包地址
     * @param coinType 币种类型
     * @return
     */
    public String findFreeWalletUrl(String coinType){
        return walletUrlService.findFreeWalletUrl(coinType);
    }

    /**
     * 创建地址
     * @param coinType 币种类型
     * @param callBackUrl 回调地址
     */
    public String createWalletUrl(String coinType,String callBackUrl){
        try {
            String url =  WalletHttpRequestUtils.getWalletUrl(coinType,callBackUrl);
            walletUrlService.save(coinType,url);
            return url;
        }catch (Exception e){
           log.info("获取钱包地址失败，失败原因：{}",e);
        }
        return null;
    }

    /**
     * 将人民币转换为币种对应的数量
     * @param coinType 币种类型
     * @param amount 人民币金额，单位为分
     * @return
     */
    public Double transformCoinByAmount(String coinType,Long amount){
        return null;
    }

    /**
     * 获取币种对应的利率
     * @param coinType
     * @return
     */
    public Double findCoinRate(String coinType){
        return null;
    }
}
