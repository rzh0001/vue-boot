package org.jeecg.modules.pay.controller;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.common.constant.PayConstant;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.util.PasswordUtil;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.exception.RRException;
import org.jeecg.modules.pay.entity.BankCard;
import org.jeecg.modules.pay.entity.CashOutApply;
import org.jeecg.modules.pay.entity.UserAmountEntity;
import org.jeecg.modules.pay.service.IBankCardService;
import org.jeecg.modules.pay.service.ICashOutApplyService;
import org.jeecg.modules.pay.service.IUserAmountDetailService;
import org.jeecg.modules.pay.service.IUserAmountEntityService;
import org.jeecg.modules.system.entity.SysUser;
import org.jeecg.modules.system.service.ISysUserService;
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
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @Description: 提现申请
 * @Author: jeecg-boot
 * @Date: 2019-08-15
 * @Version: V1.0
 */
@Slf4j
@Api(tags = "提现申请")
@RestController
@RequestMapping("/pay/cashOutApply")
public class CashOutApplyController {
	@Autowired
	private ICashOutApplyService cashOutApplyService;

	@Autowired
	private ISysUserService sysUserService;

	@Autowired
	private IBankCardService bankCardService;

	@Autowired
	private IUserAmountEntityService userAmountService;

	@Autowired
	private IUserAmountDetailService userAmountDetailService;

	/**
	 * 分页列表查询
	 *
	 * @param cashOutApply
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "提现申请-分页列表查询")
	@ApiOperation(value = "提现申请-分页列表查询", notes = "提现申请-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<CashOutApply>> queryPageList(CashOutApply cashOutApply,
													 @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
													 @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
													 HttpServletRequest req) {
		Result<IPage<CashOutApply>> result = new Result<IPage<CashOutApply>>();
		QueryWrapper<CashOutApply> queryWrapper = QueryGenerator.initQueryWrapper(cashOutApply, req.getParameterMap());
		LoginUser optUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
		if (optUser.getMemberType() != null) {
			switch (optUser.getMemberType()) {
				case PayConstant.MEMBER_TYPE_AGENT:
					queryWrapper.lambda().eq(CashOutApply::getAgentId, optUser.getId());
					break;
				case PayConstant.MEMBER_TYPE_SALESMAN:
				case PayConstant.MEMBER_TYPE_MEMBER:
					queryWrapper.lambda().eq(CashOutApply::getUserId, optUser.getId());
				default:
			}
		}
		Page<CashOutApply> page = new Page<CashOutApply>(pageNo, pageSize);
		IPage<CashOutApply> pageList = cashOutApplyService.page(page, queryWrapper);
		result.setSuccess(true);
		result.setResult(pageList);
		return result;
	}

	/**
	 * 添加
	 *
	 * @param apply
	 * @return
	 */
	@AutoLog(value = "提现申请-添加")
	@ApiOperation(value = "提现申请-添加", notes = "提现申请-添加")
	@GetMapping(value = "/add")
	@Transactional
	public Result<CashOutApply> add(CashOutApply apply, HttpServletRequest req) {
		Result<CashOutApply> result = new Result<CashOutApply>();
		LoginUser optUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
		SysUser user = sysUserService.getById(optUser.getId());

		if (StrUtil.isNotBlank(user.getPaymentPassword())) {
			Map<String, String[]> parameterMap = req.getParameterMap();
			String paymentPassword = req.getParameter("paymentPassword");
			String password = PasswordUtil.encrypt(user.getUsername(), paymentPassword, user.getSalt());
			if (!StrUtil.equals(password, user.getPaymentPassword())) {
				throw new RRException("提现密码错误！");
			}
		}

		// 校验金额，扣除余额
		UserAmountEntity amount = userAmountService.getUserAmountByUserName(optUser.getUsername());
		if (amount == null || amount.getAmount().compareTo(apply.getAmount()) == -1) {
			throw new RRException("余额不足");
		}
		userAmountService.changeAmount(user.getId(), apply.getAmount().negate());

		// 插入流水表
		userAmountDetailService.addAmountDetail(apply.getAmount().negate(), amount.getAmount(), "2", user);

		apply.setUserId(optUser.getId());
		apply.setUsername(optUser.getUsername());
		apply.setDelFlag(CommonConstant.NOT_DELETE_FLAG);
		apply.setStatus("0");
		apply.setApplyTime(new Date());
		apply.setAgentId(user.getAgentId());
		apply.setAgentUsername(user.getAgentUsername());
		apply.setAgentRealname(user.getAgentRealname());

		BankCard bankCard = bankCardService.getById(apply.getBankCardId());
		apply.setBankName(bankCard.getBankName());
		apply.setBranchName(bankCard.getBranchName());
		apply.setAccountName(bankCard.getAccountName());
		apply.setCardNumber(bankCard.getCardNumber());
		try {
			cashOutApplyService.save(apply);


			result.success("添加成功！");
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			result.error500("操作失败");
		}
		return result;
	}

	/**
	 * 编辑
	 *
	 * @param cashOutApply
	 * @return
	 */
	@AutoLog(value = "提现申请-编辑")
	@ApiOperation(value = "提现申请-编辑", notes = "提现申请-编辑")
	@PutMapping(value = "/edit")
	public Result<CashOutApply> edit(@RequestBody CashOutApply cashOutApply) {
		Result<CashOutApply> result = new Result<CashOutApply>();
		CashOutApply cashOutApplyEntity = cashOutApplyService.getById(cashOutApply.getId());
		if (cashOutApplyEntity == null) {
			result.error500("未找到对应实体");
		} else {
			boolean ok = cashOutApplyService.updateById(cashOutApply);
			//TODO 返回false说明什么？
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
	@Transactional
	public Result<CashOutApply> approval(@RequestBody JSONObject jsonObject) {

		Result<CashOutApply> result = new Result<CashOutApply>();
		CashOutApply apply = cashOutApplyService.getById(jsonObject.getString("id"));
		if (apply == null) {
			return result.error500("未找到对应实体");
		}
		if ("2".equals(apply.getStatus()) || "3".equals(apply.getStatus())) {
			return result.error500("已审核，请刷新列表！");
		}

		apply.setStatus(jsonObject.getString("status"));
		apply.setApprovalTime(new Date());
		cashOutApplyService.updateById(apply);

		// 审核拒绝须冲正余额
		if ("3".equals(jsonObject.getString("status"))) {
			UserAmountEntity amount = userAmountService.getUserAmountByUserName(apply.getUsername());
			userAmountService.changeAmount(apply.getUserId(), apply.getAmount());

			// 插入流水表
			userAmountDetailService.addAmountDetail(apply.getAmount(), amount.getAmount(), "3", sysUserService.getById(apply.getUserId()));
		}

		return result.success("修改成功");
	}

	/**
	 * 通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "提现申请-通过id删除")
	@ApiOperation(value = "提现申请-通过id删除", notes = "提现申请-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name = "id", required = true) String id) {
		try {
			cashOutApplyService.removeById(id);
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
	@AutoLog(value = "提现申请-批量删除")
	@ApiOperation(value = "提现申请-批量删除", notes = "提现申请-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<CashOutApply> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
		Result<CashOutApply> result = new Result<CashOutApply>();
		if (ids == null || "".equals(ids.trim())) {
			result.error500("参数不识别！");
		} else {
			this.cashOutApplyService.removeByIds(Arrays.asList(ids.split(",")));
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
	@AutoLog(value = "提现申请-通过id查询")
	@ApiOperation(value = "提现申请-通过id查询", notes = "提现申请-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<CashOutApply> queryById(@RequestParam(name = "id", required = true) String id) {
		Result<CashOutApply> result = new Result<CashOutApply>();
		CashOutApply cashOutApply = cashOutApplyService.getById(id);
		if (cashOutApply == null) {
			result.error500("未找到对应实体");
		} else {
			result.setResult(cashOutApply);
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
	public ModelAndView exportXls(HttpServletRequest request, HttpServletResponse response) {
		// Step.1 组装查询条件
		QueryWrapper<CashOutApply> queryWrapper = null;
		try {
			String paramsStr = request.getParameter("paramsStr");
			if (oConvertUtils.isNotEmpty(paramsStr)) {
				String deString = URLDecoder.decode(paramsStr, "UTF-8");
				CashOutApply cashOutApply = JSON.parseObject(deString, CashOutApply.class);
				queryWrapper = QueryGenerator.initQueryWrapper(cashOutApply, request.getParameterMap());
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		//Step.2 AutoPoi 导出Excel
		ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
		List<CashOutApply> pageList = cashOutApplyService.list(queryWrapper);
		//导出文件名称
		mv.addObject(NormalExcelConstants.FILE_NAME, "提现申请列表");
		mv.addObject(NormalExcelConstants.CLASS, CashOutApply.class);
		mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("提现申请列表数据", "导出人:Jeecg", "导出信息"));
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
			MultipartFile file = entity.getValue();// 获取上传文件对象
			ImportParams params = new ImportParams();
			params.setTitleRows(2);
			params.setHeadRows(1);
			params.setNeedSave(true);
			try {
				List<CashOutApply> listCashOutApplys = ExcelImportUtil.importExcel(file.getInputStream(), CashOutApply.class, params);
				cashOutApplyService.saveBatch(listCashOutApplys);
				return Result.ok("文件导入成功！数据行数:" + listCashOutApplys.size());
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
