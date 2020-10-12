package org.jeecg.modules.wallet.service.impl;

import org.jeecg.modules.wallet.entity.payWalletOrderInfo;
import org.jeecg.modules.wallet.mapper.payWalletOrderInfoMapper;
import org.jeecg.modules.wallet.service.IpayWalletOrderInfoService;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

/**
 * @Description: 钱包订单信息
 * @Author: jeecg-boot
 * @Date:   2020-10-12
 * @Version: V1.0
 */
@Service
public class payWalletOrderInfoServiceImpl extends ServiceImpl<payWalletOrderInfoMapper, payWalletOrderInfo> implements IpayWalletOrderInfoService {

}
