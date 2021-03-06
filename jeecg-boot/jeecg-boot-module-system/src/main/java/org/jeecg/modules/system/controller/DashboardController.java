package org.jeecg.modules.system.controller;

import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.constant.PayConstant;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.modules.pay.entity.UserAmountEntity;
import org.jeecg.modules.pay.service.IUserAmountDetailService;
import org.jeecg.modules.pay.service.IUserAmountEntityService;
import org.jeecg.modules.system.entity.SysUser;
import org.jeecg.modules.system.service.IDashboardService;
import org.jeecg.modules.system.service.ISysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * @author ruanzh
 * @since 2019-08-29
 */
@Slf4j
@RestController
@RequestMapping("/sys/dashboard")
public class DashboardController {

	@Autowired
	private ISysUserService userService;

	@Autowired
	private IUserAmountEntityService amountService;

	@Autowired
	private IUserAmountDetailService amountDetailService;

	@Autowired
	private IDashboardService dashboardService;

	@GetMapping("/summary")
	public Result summary() {
		LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
		SysUser opUser = userService.getUserByName(loginUser.getUsername());

		Result result = new Result();
		Map<String, Object> map = new HashMap<>();
		if (loginUser.getMemberType() != null) {
			switch (loginUser.getMemberType()) {
				case PayConstant.MEMBER_TYPE_AGENT:
					map.put("agentId", loginUser.getId());
					break;
				case PayConstant.MEMBER_TYPE_SALESMAN:
					map.put("salesmanId", loginUser.getId());
					break;
				case PayConstant.MEMBER_TYPE_MEMBER:
					map.put("userId", loginUser.getId());
					break;
				default:
			}
		}
		// 剩余额度
		// 已代付总额
		// 手续费收入
		// 今日交易

		// 今日代付
		// 今日手续费收入

		// 获取会员余额、总收入、今日收入
		//    UserAmountEntity userAmount = amountService.getUserAmountByUserName(opUser.getUsername());
		//    map.put("userAmount", userAmount.getAmount());
		//
		//    BigDecimal totalIncome = amountDetailService.getTotalIncome(opUser.getId());
		//    map.put("totalIncome", totalIncome);
		//
		//    BigDecimal todayIncome = amountDetailService.getTodayIncome(opUser.getId());
		//    map.put("todayIncome", todayIncome);
		//
		//    // 今日提现金额
		//
		//    result.setSuccess(true);
		//    result.setResult(map);
		return dashboardService.homepageSummary();
	}
}
