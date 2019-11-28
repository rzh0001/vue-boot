package org.jeecg.modules.system.service.impl;

import org.jeecg.common.api.vo.Result;
import org.jeecg.modules.system.service.IBaseService;
import org.jeecg.modules.system.service.IDashboardService;
import org.jeecg.modules.system.service.IUserAmountDetailService;
import org.jeecg.modules.system.service.IUserAmountEntityService;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.Map;

/**
 * @author ruanzh
 * @since 2019-09-01
 */
public class IDashboardServiceImpl extends IBaseService implements IDashboardService {

  @Autowired private IUserAmountEntityService amountService;

  @Autowired private IUserAmountDetailService amountDetailService;

  @Override
  public Result<Map<String, Object>> homepageSummary() {

    // 获取 今日订单数、成功率、成功金额

    // 获取会员余额、总收入、今日收入

    // 今日提现金额
    return null;
  }

  public Map<String, Object> agentSummary(String userId) {
    // 剩余额度

    // 已代付总额
    // 手续费收入
    // 今日充值
    // 今日代付
    // 今日手续费收入
    return null;
  }

  public Map<String, Object> userSummary(String userId) {
    // 剩余额度
    BigDecimal userAmount = amountService.getUserAmount(userId);
    // 已代付总额
    // 手续费
    // 今日充值
    // 今日代付
    // 今日手续费收入
    return null;
  }
}
