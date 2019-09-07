package org.jeecg.modules.quartz.job;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.util.DateUtils;
import org.jeecg.modules.pay.entity.OrderInfoEntity;
import org.jeecg.modules.pay.service.IOrderInfoEntityService;
import org.jeecg.modules.util.BaseConstant;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

/**
 * @title:
 * @Description: 更新订单状态
 * @author: wangjb
 * @create: 2019-09-07 10:58
 */
@Slf4j
public class UpdateOrderStatusJob implements Job {

    @Autowired
    private IOrderInfoEntityService orderService;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        log.info("======》 定时任务，更新订单状态为无效 《=======");
        //获取当前时间+10分钟 的未支付订单，设置为无效订单
        Date starttime = DateUtils.addDateMinutes(new Date(), -15);
        Date endtime = DateUtils.addDateMinutes(new Date(), -10);
        OrderInfoEntity orderInfoEntity = new OrderInfoEntity();
        Map<String, String[]> param = new HashMap<>();
        QueryWrapper<OrderInfoEntity> queryWrapper = new  QueryWrapper<OrderInfoEntity>();
        queryWrapper.eq("status",0).between("create_time",starttime,endtime);
        List<OrderInfoEntity> orders = orderService.list(queryWrapper);
        if(orders == null || orders.size()==0){
            log.info("===>当前无订单需要更新状态");
            return;
        }
        log.info("===》需要更新的订单数据为：{}",orders.size());
        List<String> ids = new ArrayList<>();
        for(OrderInfoEntity order:orders){
            ids.add(order.getOrderId());
            order.setStatus(BaseConstant.ORDER_STATUS_INVALID);
        }
        log.info("===>更新订单状态的订单id为：{}",ids.toArray());
        orderService.updateOrderStatusBatch(ids);
    }
}
