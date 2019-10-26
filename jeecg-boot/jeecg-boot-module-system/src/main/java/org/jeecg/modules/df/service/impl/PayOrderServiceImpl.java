package org.jeecg.modules.df.service.impl;

import org.jeecg.modules.df.entity.PayOrder;
import org.jeecg.modules.df.mapper.PayOrderMapper;
import org.jeecg.modules.df.service.IPayOrderService;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

/**
 * @Description: 代付订单
 * @Author: jeecg-boot
 * @Date:   2019-10-25
 * @Version: V1.0
 */
@Service
public class PayOrderServiceImpl extends ServiceImpl<PayOrderMapper, PayOrder> implements IPayOrderService {

}
