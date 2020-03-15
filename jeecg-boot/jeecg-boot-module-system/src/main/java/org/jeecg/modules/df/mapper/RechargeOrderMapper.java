package org.jeecg.modules.df.mapper;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.df.entity.PayOrder;
import org.jeecg.modules.df.entity.RechargeOrder;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * @Description: 代付充值订单
 * @Author: jeecg-boot
 * @Date: 2019-10-26
 * @Version: V1.0
 */
public interface RechargeOrderMapper extends BaseMapper<RechargeOrder> {
	Map<String, Object> summary(@Param(Constants.WRAPPER) QueryWrapper<RechargeOrder> queryWrapper);

}
