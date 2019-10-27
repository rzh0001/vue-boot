package org.jeecg.modules.df.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.df.entity.PayOrder;

/**
 * @Description: 1
 * @Author: jeecg-boot
 * @Date: 2019-10-27
 * @Version: V1.0
 */
public interface IPayOrderService extends IService<PayOrder> {
    
    /**
     * 创建订单
     *
     * @return
     */
    boolean create(PayOrder order);
}
