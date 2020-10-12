package org.jeecg.modules.wallet.service;

import org.springframework.stereotype.Service;

@Service
public class WalletService {
    /**
     *获取可用的钱包地址
     * @param coinType 币种类型
     * @return
     */
    public String findFreeWalletUrl(String coinType){
        return null;
    }

    /**
     * 创建地址
     * @param coinType 币种类型
     * @param callBackUrl 回调地址
     */
    public void createWalletUrl(String coinType,String callBackUrl){

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
