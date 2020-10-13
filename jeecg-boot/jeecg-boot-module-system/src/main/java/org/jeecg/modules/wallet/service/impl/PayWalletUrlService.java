package org.jeecg.modules.wallet.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.jeecg.common.wallet.WalletStatus;
import org.jeecg.modules.wallet.entity.PayWalletUrl;
import org.jeecg.modules.wallet.mapper.PayWalletUrlMapper;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

/**
 * @Description: 钱包地址
 * @Author: jeecg-boot
 * @Date:   2020-10-12
 * @Version: V1.0
 */
@Service
public class PayWalletUrlService extends ServiceImpl<PayWalletUrlMapper, PayWalletUrl> {

    public String findFreeWalletUrl(String coinType){
        PayWalletUrl walletUrl =  getOne(new LambdaQueryWrapper<PayWalletUrl>().eq(PayWalletUrl::getCoinType,coinType).eq(PayWalletUrl::getStatus,
            WalletStatus.FREE.getCode()));
        if(walletUrl != null){
            return walletUrl.getWalletUrl();
        }
        return null;
    }

    public void save(String coinType,String url){
        PayWalletUrl walletUrl1 = new PayWalletUrl();
        walletUrl1.setCoinType(coinType);
        walletUrl1.setWalletUrl(url);
        walletUrl1.setStatus(WalletStatus.FREE.getCode());
        getBaseMapper().insert(walletUrl1);
    }
}
