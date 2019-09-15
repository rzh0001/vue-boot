package org.jeecg.modules.pay.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.pay.entity.CashOutApply;

import java.util.Date;
import java.util.Map;

/**
 * @Description: 提现申请
 * @Author: jeecg-boot
 * @Date:   2019-08-15
 * @Version: V1.0
 */
public interface ICashOutApplyService extends IService<CashOutApply> {
    
    /**
     * 统计用户今日提现金额
     *
     * @param userId
     * @param date
     * @return
     */
    Map<String, Object> summaryUserTodayCashOutAmount(String userId, Date date);
}
