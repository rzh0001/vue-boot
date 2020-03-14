package org.jeecg.modules.system.controller;

import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.modules.system.entity.SysUser;
import org.jeecg.modules.system.entity.UserAmountEntity;
import org.jeecg.modules.system.service.IDashboardService;
import org.jeecg.modules.system.service.ISysUserService;
import org.jeecg.modules.system.service.IUserAmountDetailService;
import org.jeecg.modules.system.service.IUserAmountEntityService;
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
		return dashboardService.homepageSummary();
	}
}
