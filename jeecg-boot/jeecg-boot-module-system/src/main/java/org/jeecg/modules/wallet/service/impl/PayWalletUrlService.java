package org.jeecg.modules.wallet.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
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
            walletUrl.setStatus(WalletStatus.BUSY.getCode());
           updateById(walletUrl);
            return walletUrl.getWalletUrl();
        }
        return null;
    }

    public void save(String coinType,String url){
        PayWalletUrl walletUrl1 = new PayWalletUrl();
        walletUrl1.setCoinType(coinType);
        walletUrl1.setWalletUrl(url);
        //创建只有在没有钱包地址的情况下才会创建，所以，首次必然使用了该地址
        walletUrl1.setStatus(WalletStatus.BUSY.getCode());
        getBaseMapper().insert(walletUrl1);
    }

    public void freeWallterUrl(String url){
        QueryWrapper<PayWalletUrl> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("wallet_url",url);
        PayWalletUrl update =PayWalletUrl.builder().status(WalletStatus.FREE.getCode()).build();
        update(update,queryWrapper);
    }


    public PayWalletUrl findByUrl(String url){
       return getOne(new LambdaQueryWrapper<PayWalletUrl>().eq(PayWalletUrl::getWalletUrl,url));
    }

    public boolean checkCallbackWalletUrlIsOk(String url){
        PayWalletUrl walletUrl = this.findByUrl(url);
        if(walletUrl == null){
            return false;
        }
        WalletStatus status =  WalletStatus.codeBy(walletUrl.getStatus());
        if(WalletStatus.FREE.equals(status)){
            return false;
        }
        return true;
    }
}
