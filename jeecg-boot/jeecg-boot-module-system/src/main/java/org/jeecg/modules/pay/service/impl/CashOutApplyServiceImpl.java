package org.jeecg.modules.pay.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.modules.pay.entity.CashOutApply;
import org.jeecg.modules.pay.mapper.CashOutApplyMapper;
import org.jeecg.modules.pay.service.ICashOutApplyService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @Description: 提现申请
 * @Author: jeecg-boot
 * @Date:   2019-08-15
 * @Version: V1.0
 */
@Service
public class CashOutApplyServiceImpl extends ServiceImpl<CashOutApplyMapper, CashOutApply> implements ICashOutApplyService {
    
    @Override
    public Map<String, Object> summaryUserTodayCashOutAmount(String userId, Date date) {
        BigDecimal cashOutAmount = baseMapper.summaryUserTodayCashOutAmount(userId, date);
        Map<String, Object> map = new HashMap<>();
        map.put("cashOutAmount", cashOutAmount == null ? BigDecimal.ZERO : cashOutAmount);
        return map;
    }
}
