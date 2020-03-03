package org.jeecg.modules.df.controller;

import cn.hutool.core.util.StrUtil;
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
import org.jeecg.modules.df.constant.DfConstant;
import org.jeecg.modules.df.entity.PayOrder;
import org.jeecg.modules.df.service.IApiService;
import org.jeecg.modules.df.service.IPayOrderService;
import org.jeecg.modules.pay.entity.OrderInfoEntity;
import org.jeecg.modules.system.entity.SysUser;
import org.jeecg.modules.system.service.ISysUserService;
import org.jeecg.modules.system.service.IUserAmountEntityService;
import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

/**
 * @Description: 代付订单 @Author: jeecg-boot @Date: 2019-10-27 @Version: V1.0
 */
@Slf4j
@Api(tags = "代付订单")
@RestController
@RequestMapping("/df/payOrder")
public class PayOrderController {
	@Autowired
	private IPayOrderService payOrderService;

	@Autowired
	private IUserAmountEntityService userAmountService;

	@Autowired
	private ISysUserService userService;

	@Autowired
	private IApiService apiService;

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
	public Result<Map<String, Object>> summary(PayOrder order, HttpServletRequest req) {
		Result<Map<String, Object>> result = new Result<>();
		QueryWrapper<PayOrder> queryWrapper = initQueryCondition(order, req);
		Map<String, Object> map = payOrderService.summary(queryWrapper);
		result.setResult(map);
		result.setSuccess(true);
		return result;
	}

	/**
	 * 添加
	 *
	 * @param order
	 * @return
	 */
	@AutoLog(value = "1-添加")
	@ApiOperation(value = "1-添加", notes = "1-添加")
	@PostMapping(value = "/add")
	public Result<PayOrder> add(@RequestBody PayOrder order) {
		Result<PayOrder> result = new Result<PayOrder>();

		try {
			payOrderService.create(order);
			result.success("添加成功！");
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			result.error500("操作失败," + e.getMessage());
		}
		return result;
	}

	/**
	 * 编辑
	 *
	 * @param payOrder
	 * @return
	 */
	@AutoLog(value = "1-编辑")
	@ApiOperation(value = "1-编辑", notes = "1-编辑")
	@PutMapping(value = "/edit")
	public Result<PayOrder> edit(@RequestBody PayOrder payOrder) {
		Result<PayOrder> result = new Result<PayOrder>();
		PayOrder payOrderEntity = payOrderService.getById(payOrder.getId());
		if (payOrderEntity == null) {
			result.error500("未找到对应实体");
		} else {
			boolean ok = payOrderService.updateById(payOrder);
			// TODO 返回false说明什么？
			if (ok) {
				result.success("修改成功!");
			}
		}

		return result;
	}

	/**
	 * 审核
	 *
	 * @param jsonObject
	 * @return
	 */
	@AutoLog(value = "提现申请-审核")
	@ApiOperation(value = "提现申请-审核", notes = "提现申请-审核")
	@PutMapping(value = "/approval")
	@Transactional(rollbackFor = Exception.class)
	public Result<Object> approval(@RequestBody JSONObject jsonObject) {

		PayOrder order = payOrderService.getById(jsonObject.getString("id"));
		if (order == null) {
			return Result.error("未找到对应实体");
		}
		String status = jsonObject.getString("status");

		if (StrUtil.isEmpty(status)) {
			return Result.error("内部错误，status 字段不能为空");
		}
		order.setStatus(status);
		order.setSuccessTime(new Date());
		boolean ok = payOrderService.updateById(order);

		if (DfConstant.STATUS_CHECKED.equals(status)) {
			payOrderService.checked(order);
		} else if (DfConstant.STATUS_REJECTED.equals(status)) {
			payOrderService.rejected(order);
		}

		return Result.ok("审核成功!");
	}

	@AutoLog(value = "代付订单-手动回调")
	@ApiOperation(value = "代付订单-手动回调", notes = "代付订单-手动回调")
	@PutMapping(value = "/manualCallback")
	@Transactional(rollbackFor = Exception.class)
	public Result<Object> manualCallback(@RequestBody JSONObject jsonObject) {

		PayOrder order = payOrderService.getById(jsonObject.getString("id"));
		if (order == null) {
			return Result.error("未找到对应实体");
		}

		apiService.callback(order.getId());

		return Result.ok("异步通知已发出!");
	}

	/**
	 * 通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "1-通过id删除")
	@ApiOperation(value = "1-通过id删除", notes = "1-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name = "id", required = true) String id) {
		try {
			payOrderService.removeById(id);
		} catch (Exception e) {
			log.error("删除失败", e.getMessage());
			return Result.error("删除失败!");
		}
		return Result.ok("删除成功!");
	}

	/**
	 * 批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "1-批量删除")
	@ApiOperation(value = "1-批量删除", notes = "1-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<PayOrder> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
		Result<PayOrder> result = new Result<PayOrder>();
		if (ids == null || "".equals(ids.trim())) {
			result.error500("参数不识别！");
		} else {
			this.payOrderService.removeByIds(Arrays.asList(ids.split(",")));
			result.success("删除成功!");
		}
		return result;
	}

	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "1-通过id查询")
	@ApiOperation(value = "1-通过id查询", notes = "1-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<PayOrder> queryById(@RequestParam(name = "id", required = true) String id) {
		Result<PayOrder> result = new Result<PayOrder>();
		PayOrder payOrder = payOrderService.getById(id);
		if (payOrder == null) {
			result.error500("未找到对应实体");
		} else {
			result.setResult(payOrder);
			result.setSuccess(true);
		}
		return result;
	}

	/**
	 * 导出excel
	 *
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = "/exportXls")
	public ModelAndView exportXls(
			PayOrder order, HttpServletRequest request, HttpServletResponse response) {
		// Step.1 组装查询条件
		QueryWrapper<PayOrder> queryWrapper = initQueryCondition(order, request);

		// Step.2 AutoPoi 导出Excel
		ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
		List<PayOrder> pageList = payOrderService.list(queryWrapper);
		// 导出文件名称
		mv.addObject(NormalExcelConstants.FILE_NAME, "1列表");
		mv.addObject(NormalExcelConstants.CLASS, PayOrder.class);
		mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("1列表数据", "导出人:Jeecg", "导出信息"));
		mv.addObject(NormalExcelConstants.DATA_LIST, pageList);
		return mv;
	}

	/**
	 * 通过excel导入数据
	 *
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/importExcel", method = RequestMethod.POST)
	public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
		MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
		Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
		for (Map.Entry<String, MultipartFile> entity : fileMap.entrySet()) {
			MultipartFile file = entity.getValue(); // 获取上传文件对象
			ImportParams params = new ImportParams();
			params.setTitleRows(2);
			params.setHeadRows(1);
			params.setNeedSave(true);
			try {
				List<PayOrder> listPayOrders =
						ExcelImportUtil.importExcel(file.getInputStream(), PayOrder.class, params);
				payOrderService.saveBatch(listPayOrders);
				return Result.ok("文件导入成功！数据行数:" + listPayOrders.size());
			} catch (Exception e) {
				log.error(e.getMessage(), e);
				return Result.error("文件导入失败:" + e.getMessage());
			} finally {
				try {
					file.getInputStream().close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return Result.ok("文件导入失败！");
	}
}
