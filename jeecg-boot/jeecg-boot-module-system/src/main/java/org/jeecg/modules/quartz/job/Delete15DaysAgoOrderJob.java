package org.jeecg.modules.quartz.job;

import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.util.DateUtils;
import org.jeecg.modules.pay.entity.OrderInfoEntity;
import org.jeecg.modules.pay.service.IOrderInfoEntityService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.List;

/**
 * @title:
 * @Description: 每天凌晨执行，删除20天前的无效订单
 * @author: wangjb
 * @create: 2019-09-16 10:44
 */
@Slf4j
public class Delete15DaysAgoOrderJob implements Job {

    @Autowired
    private IOrderInfoEntityService order;
    /**
     * 20天前
     */
    public static int TWENTY_DAYS_AGO = -20;
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        Date deleteTime = DateUtils.addDateDays(new Date(),TWENTY_DAYS_AGO);
        String time = DateUtils.date2Str(deleteTime,DateUtils.datetimeFormat);
        log.info("==>删除20天前的无效和未支付订单,删除时间小于{}的数据",time);
        List<String> orders =order.getOrderByTime(time);
        if(CollectionUtils.isEmpty(orders)){
            log.info("==>当前无需要删除的订单");
        }else {
            log.info("==> 删除的订单为：{}",orders.toArray());

        }

    }
}
