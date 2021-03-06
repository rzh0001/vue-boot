package org.jeecg.modules.quartz.job;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.pay.service.IReportService;
import org.jeecg.modules.pay.service.IUserAmountEntityService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 用户余额报表-期初余额 每天0点更新
 *
 * @author ruanzh
 * @since 2019-09-11
 */
@Slf4j
public class UserAmountReportStep2Job implements Job {
    
    @Autowired
    private IReportService reportService;
    
    @Autowired
    private IUserAmountEntityService amountService;
    
    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        
        DateTime yesterday = DateUtil.yesterday();
        log.info("======》 定时任务[生成财务报表-统计昨日财务数据] 开始执行 《=======");
        log.info("======》 定时任务[生成财务报表-统计昨日财务数据] {} 《=======", yesterday);
        
        reportService.generateFinancialStatement(DateUtil.formatDate(yesterday));
        
        log.info("======》 定时任务[生成财务报表-统计昨日财务数据] 执行完毕 《=======");
    }
    
    
}
