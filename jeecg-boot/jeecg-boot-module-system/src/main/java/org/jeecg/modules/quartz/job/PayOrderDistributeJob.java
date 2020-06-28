package org.jeecg.modules.quartz.job;

import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.util.DateUtils;
import org.jeecg.modules.df.service.OperatorService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;

/**
 * @title:
 * @Description: 更新订单状态
 * @author: wangjb
 * @create: 2019-09-07 10:58
 */
@Slf4j
public class PayOrderDistributeJob implements Job {

	@Autowired
	private OperatorService operatorService;

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		log.info("======》 定时任务，更新订单状态为无效 《=======");
		//获取当前时间+10分钟 的未支付订单，设置为无效订单
		Date starttime = DateUtils.addDateMinutes(new Date(), -15);
	}
}
