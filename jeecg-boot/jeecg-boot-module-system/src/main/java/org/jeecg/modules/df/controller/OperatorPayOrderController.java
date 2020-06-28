package org.jeecg.modules.df.controller;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.constant.PayConstant;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.modules.api.service.IDfApiService;
import org.jeecg.modules.df.entity.PayOrder;
import org.jeecg.modules.df.service.IPayOrderService;
import org.jeecg.modules.system.entity.SysUser;
import org.jeecg.modules.system.service.ISysUserService;
import org.jeecg.modules.system.service.IUserAmountEntityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * 操作员代付订单
 *
 * @author ruanzh
 */
@Slf4j
@Api(tags = "代付订单")
@RestController
@RequestMapping("/df/operatorPayOrder")
public class OperatorPayOrderController {
	@Autowired
	private IPayOrderService payOrderService;

	@Autowired
	private IUserAmountEntityService userAmountService;

	@Autowired
	private ISysUserService userService;

	@Autowired
	private IDfApiService apiService;

	// 查询条件独立成方法，查询、统计、导出 三个接口使用
	private QueryWrapper<PayOrder> initQueryCondition(PayOrder order, HttpServletRequest req) {
		LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
		SysUser opUser = userService.getUserByName(loginUser.getUsername());

		QueryWrapper<PayOrder> queryWrapper =
				QueryGenerator.initQueryWrapper(order, req.getParameterMap());
		if (StringUtils.isNotBlank(opUser.getMemberType())) {
			switch (opUser.getMemberType()) {
				case PayConstant.MEMBER_TYPE_AGENT:
					queryWrapper.lambda().eq(PayOrder::getAgentId, opUser.getId());
					break;
				case PayConstant.MEMBER_TYPE_SALESMAN:
					queryWrapper.lambda().eq(PayOrder::getSalesmanId, opUser.getId());
					break;
				case PayConstant.MEMBER_TYPE_MEMBER:
					queryWrapper.lambda().eq(PayOrder::getUserId, opUser.getId());
					break;
				case PayConstant.MEMBER_TYPE_OPERATOR:
					queryWrapper.lambda().eq(PayOrder::getOperatorId, opUser.getId());
					break;
				default:
			}
		}
		queryWrapper.lambda().orderByDesc(PayOrder::getCreateTime);
		return queryWrapper;
	}

	/**
	 * 分页列表查询
	 *
	 * @param order
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "1-分页列表查询")
	@ApiOperation(value = "1-分页列表查询", notes = "1-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<PayOrder>> queryPageList(
			PayOrder order,
			@RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
			@RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
			HttpServletRequest req) {
		Result<IPage<PayOrder>> result = new Result<IPage<PayOrder>>();
		QueryWrapper<PayOrder> queryWrapper = initQueryCondition(order, req);
		Page<PayOrder> page = new Page<PayOrder>(pageNo, pageSize);
		IPage<PayOrder> pageList = payOrderService.page(page, queryWrapper);
		result.setSuccess(true);
		result.setResult(pageList);
		return result;
	}

	@GetMapping(value = "/summary")
	public Result summary(PayOrder order, HttpServletRequest req) {
		QueryWrapper<PayOrder> queryWrapper = initQueryCondition(order, req);
		Map<String, Object> map = payOrderService.summary(queryWrapper);
		return Result.ok(map);
	}


	@GetMapping(value = "/getBizOnlineStatus")
	public Result getBizOnlineStatus() {
		LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
		SysUser opUser = userService.getUserByName(loginUser.getUsername());

		return Result.ok(opUser.getBizOnline());
	}


	@PostMapping(value = "/changeBizOnlineStatus")
//	@RequestMapping(value = "/changeBizOnlineStatus", method = RequestMethod.PUT)
	public Result changeBizOnlineStatus(@RequestBody JSONObject jsonObject) {
		LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();

		SysUser user = new SysUser();
		user.setId(loginUser.getId());
		user.setBizOnline((Integer) jsonObject.get("status"));
		userService.updateById(user);

		return Result.ok();
	}


}
