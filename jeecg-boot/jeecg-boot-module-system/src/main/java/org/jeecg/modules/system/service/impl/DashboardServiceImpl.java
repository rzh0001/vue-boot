package org.jeecg.modules.system.service.impl;

import cn.hutool.core.map.MapUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.modules.system.entity.SysUser;
import org.jeecg.modules.system.mapper.DashboardMapper;
import org.jeecg.modules.system.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * @author ruanzh
 * @since 2019-09-01
 */
@Slf4j
@Service
public class DashboardServiceImpl extends IBaseService implements IDashboardService {

  @Autowired private ISysUserService userService;

  @Autowired private IUserAmountEntityService amountService;

  @Autowired private IUserAmountDetailService amountDetailService;

  @Autowired private DashboardMapper mapper;

  @Override
  public Result<Map<String, Object>> homepageSummary() {
    Result r = new Result();
    LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
    SysUser user = userService.getUserById(loginUser.getId());

    if (user.getMemberType().equals("1")) {
      Map<String, Object> summary = agentSummary(user.getId());
      r.setSuccess(true);
      r.setResult(summary);
    } else if (user.getMemberType().equals("3")) {
      Map<String, Object> summary = userSummary(user.getId());
      r.setSuccess(true);
      r.setResult(summary);
    }

    return r;
  }

  public Map<String, Object> agentSummary(String userId) {
    Map map = new HashMap();
    // 剩余额度
    BigDecimal userAmount = mapper.summaryUserAmount(userId);
    // 已代付总额
    BigDecimal payAmount = mapper.summaryDfPayAmount(userId);
    // 手续费收入
    BigDecimal orderFee = mapper.summaryDfOrderFee(userId);
    // 今日充值
    BigDecimal todayRechargeAmount = mapper.summaryTodayRechargeAmount(userId);
    // 今日代付
    BigDecimal todayDfPayAmount = mapper.summaryTodayDfPayAmount(userId);
    // 今日手续费收入
    BigDecimal todayDfOrderFee = mapper.summaryTodayDfOrderFee(userId);
    map.put("userAmount", userAmount);
    map.put("payAmount", payAmount);
    map.put("orderFee", orderFee);
    map.put("todayRechargeAmount", todayRechargeAmount);
    map.put("todayDfPayAmount", todayDfPayAmount);
    map.put("todayDfOrderFee", todayDfOrderFee);

    return map;
  }

  public Map<String, Object> userSummary(String userId) {
    Map map = new HashMap();
    // 剩余额度
    BigDecimal userAmount = mapper.getUserAmount(userId);
    // 已代付总额
    BigDecimal payAmount = mapper.summaryUserDfPayAmount(userId);
    // 手续费收入
    BigDecimal orderFee = mapper.summaryUserDfOrderFee(userId);
    // 今日充值
    BigDecimal todayRechargeAmount = mapper.summaryUserTodayRechargeAmount(userId);
    // 今日代付
    BigDecimal todayDfPayAmount = mapper.summaryUserTodayDfPayAmount(userId);
    // 今日手续费收入
    BigDecimal todayDfOrderFee = mapper.summaryUserTodayDfOrderFee(userId);
    map.put("userAmount", userAmount);
    map.put("payAmount", payAmount);
    map.put("orderFee", orderFee);
    map.put("todayRechargeAmount", todayRechargeAmount);
    map.put("todayDfPayAmount", todayDfPayAmount);
    map.put("todayDfOrderFee", todayDfOrderFee);
    return map;
  }
}