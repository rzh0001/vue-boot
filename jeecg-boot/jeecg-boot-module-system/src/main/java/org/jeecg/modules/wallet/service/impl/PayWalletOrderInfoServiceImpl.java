package org.jeecg.modules.wallet.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.jeecg.common.wallet.OrderStatusEnum;
import org.jeecg.modules.wallet.entity.PayWalletOrderInfo;
import org.jeecg.modules.wallet.mapper.payWalletOrderInfoMapper;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

/**
 * @Description: 钱包订单信息
 * @Author: jeecg-boot
 * @Date:   2020-10-12
 * @Version: V1.0
 */
@Service
public class PayWalletOrderInfoServiceImpl extends ServiceImpl<payWalletOrderInfoMapper, PayWalletOrderInfo> {
    /**
     * 获取当前回调的钱包订单
     * @param url
     * @return
     */
    public PayWalletOrderInfo findCurrentCallbackWalletOrder(String url){
        return getOne(new LambdaQueryWrapper<PayWalletOrderInfo>().eq(PayWalletOrderInfo::getWalletUrl,url).eq(PayWalletOrderInfo::getStatus,
            OrderStatusEnum.NO_PAY.getCode()));
    }


}
