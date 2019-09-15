package org.jeecg.modules.pay.service;

/**
 * @author ruanzh
 * @since 2019-09-14
 */
public interface IReportService {
    
    /**
     * 生成财务报表
     *
     * @return
     */
    boolean generateFinancialStatement();
    
    /**
     * 生成用户每日期初金额
     *
     * @return
     */
    boolean generateUserOriginalAmount(String dateStr);
}
