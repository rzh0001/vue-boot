package org.jeecg;

import org.jeecg.modules.pay.service.IReportService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author ruanzh
 * @since 2019-09-15
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class ReportTest {
    
    @Autowired
    private IReportService service;
    
    @Test
    public void testReport1() {
        service.generateUserOriginalAmount("2019-09-15");
    }
    
    @Test
    public void testReport2() {
        service.generateFinancialStatement();
    }
}
