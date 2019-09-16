package org.jeecg.modules.pay.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.ResultType;
import org.apache.ibatis.annotations.Select;
import org.jeecg.modules.pay.entity.CashOutApply;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @Description: 提现申请
 * @Author: jeecg-boot
 * @Date:   2019-08-15
 * @Version: V1.0
 */
public interface CashOutApplyMapper extends BaseMapper<CashOutApply> {
    
    @Select("select sum(amount) as cashOutAmount from pay_cash_out_apply " +
            "where status = 2 and user_id = #{userId} and to_days(update_time) = to_days(#{date})")
    @ResultType(BigDecimal.class)
    BigDecimal summaryUserTodayCashOutAmount(@Param("userId") String userId, @Param("date") Date date);
}
