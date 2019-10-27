package org.jeecg;

import org.jeecg.modules.system.entity.UserAmountEntity;
import org.jeecg.modules.system.service.IUserAmountDetailService;
import org.jeecg.modules.system.service.IUserAmountEntityService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;

/**
 * @author ruanzh
 * @since 2019-09-14
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class UserAmountTest {
    
    @Autowired
    private IUserAmountEntityService amountEntityService;
    
    @Autowired
    private IUserAmountDetailService amountDetailService;
    
    @Test
    public void testChangeAmount() {
        UserAmountEntity fh001 = amountEntityService.getUserAmountByUserName("fh001");
        
        amountEntityService.changeAmount(fh001.getId(), new BigDecimal(100));
    }
}
