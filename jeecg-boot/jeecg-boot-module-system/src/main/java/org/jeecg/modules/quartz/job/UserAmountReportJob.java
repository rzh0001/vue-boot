package org.jeecg.modules.quartz.job;

import cn.hutool.core.date.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.pay.entity.UserAmountEntity;
import org.jeecg.modules.pay.entity.UserAmountReport;
import org.jeecg.modules.pay.service.IUserAmountEntityService;
import org.jeecg.modules.pay.service.IUserAmountReportService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * 用户余额报表-期初余额 每天0点更新
 *
 * @author ruanzh
 * @since 2019-09-11
 */
@Slf4j
public class UserAmountReportJob implements Job {
    
    @Autowired
    private IUserAmountReportService reportService;
    
    @Autowired
    private IUserAmountEntityService amountService;
    
    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        
        String today = DateUtil.today();
        log.info("======》 定时任务[生成用户余额报表-期初余额] 开始执行 《=======");
        log.info("======》 定时任务[生成用户余额报表-期初余额] {} 《=======", today);
        List<UserAmountEntity> userAmountList = amountService.list();
        
        if (!userAmountList.isEmpty()) {
            for (UserAmountEntity amount : userAmountList) {
                UserAmountReport report = generateReport(amount);
                report.setReportDate(today);
                reportService.save(report);
            }
        }
        
        log.info("======》 定时任务[生成用户余额报表-期初余额] 执行完毕 《=======");
    }
    
    private UserAmountReport generateReport(UserAmountEntity amount) {
        UserAmountReport report = new UserAmountReport();
        report.setUserId(amount.getUserId());
        report.setUserName(amount.getUserName());
        report.setAmount(amount.getAmount());
        report.setAgentId(amount.getAgentId());
        return report;
    }
}
