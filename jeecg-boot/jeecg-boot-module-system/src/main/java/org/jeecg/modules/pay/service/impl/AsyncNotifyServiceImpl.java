package org.jeecg.modules.pay.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.pay.entity.OrderInfoEntity;
import org.jeecg.modules.pay.service.IOrderInfoEntityService;
import org.jeecg.modules.system.entity.SysUser;
import org.jeecg.modules.system.service.ISysUserService;
import org.jeecg.modules.util.BaseConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * @Author: wangjianbin
 * @Date: 2020/4/27 14:27
 */
@Service
@Slf4j
public class AsyncNotifyServiceImpl {
    @Autowired
    private IOrderInfoEntityService orderInfoEntityService;
    @Autowired
    private ISysUserService userService;
    @Async("taskExecutor")
    public void asyncNotify(String orderNo, String payType) throws Exception {
        log.info("==>异步通知商户信息，订单号：{}，通道类型：{}",orderNo,payType);
        OrderInfoEntity order = orderInfoEntityService.queryOrderInfoByOrderId(orderNo);
        if (order == null || order.getStatus() == 2) {
            log.info("==>异步通知商户信息,无订单信息，订单号为：{}",orderNo);
            return;
        }
        order.setStatus(BaseConstant.ORDER_STATUS_SUCCESS_NOT_RETURN);
        SysUser user = userService.getUserByName(order.getUserName());
        orderInfoEntityService.notifyCustomer(order, user, payType);
        log.info("==>异步通知商户信息成功");
    }

    public void walletAsyncNotify(String orderNo){

    }
}
