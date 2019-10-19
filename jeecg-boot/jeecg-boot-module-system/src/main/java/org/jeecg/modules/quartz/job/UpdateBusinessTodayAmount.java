package org.jeecg.modules.quartz.job;

import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.pay.service.IUserBusinessEntityService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @title:
 * @Description: 每天凌晨更新
 * @author: wangjb
 * @create: 2019-10-09 17:09
 */
@Slf4j
public class UpdateBusinessTodayAmount implements Job {
    @Autowired
    private IUserBusinessEntityService businessEntityService;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        log.info("====>定时任务更新挂马账号的当日收入，将金额更新为0");
        businessEntityService.updateBusinessTodayAmount();
    }
}
