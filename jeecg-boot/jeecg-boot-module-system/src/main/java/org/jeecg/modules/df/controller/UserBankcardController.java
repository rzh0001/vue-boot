package org.jeecg.modules.df.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.modules.df.entity.UserBankcard;
import org.jeecg.modules.df.entity.UserBankcardVo;
import org.jeecg.modules.df.service.IUserBankcardService;
import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @Description: 代付平台用户银行卡
 * @Author: jeecg-boot
 * @Date: 2019-10-25
 * @Version: V1.0
 */
@Slf4j
@Api(tags = "代付平台用户银行卡")
@RestController
@RequestMapping("/df/userBankcard")
public class UserBankcardController {
	@Autowired
	private IUserBankcardService userBankcardService;

	// 查询条件独立成方法，查询、统计、导出 三个接口使用
	private QueryWrapper<UserBankcard> initQueryCondition(UserBankcard userBankcard, HttpServletRequest req) {
		LoginUser ou = (LoginUser) SecurityUtils.getSubject().getPrincipal();
		QueryWrapper<UserBankcard> qw = QueryGenerator.initQueryWrapper(userBankcard, req.getParameterMap());
		qw.lambda().eq(UserBankcard::getUserId, ou.getId());
//		if (StrUtil.isNotBlank(ou.getMemberType())) {
//			switch (ou.getMemberType()) {
//				case PayConstant.MEMBER_TYPE_AGENT:
//					queryWrapper.lambda().eq(UserBankcard::getAgentId, ou.getId());
//					break;
//				case PayConstant.MEMBER_TYPE_SALESMAN:
//					queryWrapper.lambda().eq(UserBankcard::getSalesmanId, ou.getId());
//					break;
//				case PayConstant.MEMBER_TYPE_MEMBER:
//					queryWrapper.lambda().eq(UserBankcard::getUserId, ou.getId());
//					break;
//				default:
//			}
//		}
		qw.orderByAsc("del_flag", "is_open", "create_time");
		return qw;
	}

	/**
	 * 分页列表查询
	 *
	 * @param userBankcard
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "代付平台用户银行卡-分页列表查询")
	@ApiOperation(value = "代付平台用户银行卡-分页列表查询", notes = "代付平台用户银行卡-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<UserBankcardVo>> queryPageList(UserBankcard userBankcard,
													   @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
													   @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
													   HttpServletRequest req) {
		Result<IPage<UserBankcardVo>> result = new Result<IPage<UserBankcardVo>>();
		LoginUser ou = (LoginUser) SecurityUtils.getSubject().getPrincipal();
		QueryWrapper<UserBankcard> qw = initQueryCondition(userBankcard, req);
		Page<UserBankcard> page = new Page<UserBankcard>(pageNo, pageSize);

		IPage<UserBankcardVo> pageList = userBankcardService.selectUserBankcardPage(page, qw);
		result.setSuccess(true);
		result.setResult(pageList);
		return result;
	}

	/**
	 * 添加
	 *
	 * @param userBankcard
	 * @return
	 */
	@AutoLog(value = "代付平台用户银行卡-添加")
	@ApiOperation(value = "代付平台用户银行卡-添加", notes = "代付平台用户银行卡-添加")
	@PostMapping(value = "/add")
	public Result<UserBankcard> add(@RequestBody UserBankcard userBankcard) {
		Result<UserBankcard> result = new Result<UserBankcard>();
		try {
			userBankcardService.add(userBankcard);
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
	 * @param userBankcard
	 * @return
	 */
	@AutoLog(value = "代付平台用户银行卡-编辑")
	@ApiOperation(value = "代付平台用户银行卡-编辑", notes = "代付平台用户银行卡-编辑")
	@PutMapping(value = "/edit")
	public Result<UserBankcard> edit(@RequestBody UserBankcard userBankcard) {
		Result<UserBankcard> result = new Result<UserBankcard>();
		UserBankcard userBankcardEntity = userBankcardService.getById(userBankcard.getId());
		if (userBankcardEntity == null) {
			result.error500("未找到对应实体");
		} else {
			boolean ok = userBankcardService.updateById(userBankcard);
			//TODO 返回false说明什么？
			if (ok) {
				result.success("修改成功!");
			}
		}

		return result;
	}

	/**
	 * 通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "代付平台用户银行卡-通过id删除")
	@ApiOperation(value = "代付平台用户银行卡-通过id删除", notes = "代付平台用户银行卡-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name = "id", required = true) String id) {
		try {
			userBankcardService.delete(id);
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
	@AutoLog(value = "代付平台用户银行卡-批量删除")
	@ApiOperation(value = "代付平台用户银行卡-批量删除", notes = "代付平台用户银行卡-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<UserBankcard> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
		Result<UserBankcard> result = new Result<UserBankcard>();
		if (ids == null || "".equals(ids.trim())) {
			result.error500("参数不识别！");
		} else {
			this.userBankcardService.removeByIds(Arrays.asList(ids.split(",")));
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
	@AutoLog(value = "代付平台用户银行卡-通过id查询")
	@ApiOperation(value = "代付平台用户银行卡-通过id查询", notes = "代付平台用户银行卡-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<UserBankcard> queryById(@RequestParam(name = "id", required = true) String id) {
		Result<UserBankcard> result = new Result<UserBankcard>();
		UserBankcard userBankcard = userBankcardService.getById(id);
		if (userBankcard == null) {
			result.error500("未找到对应实体");
		} else {
			result.setResult(userBankcard);
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
	public ModelAndView exportXls(UserBankcard userBankcard, HttpServletRequest request, HttpServletResponse response) {
		// Step.1 组装查询条件
		QueryWrapper<UserBankcard> qw = initQueryCondition(userBankcard, request);

		//Step.2 AutoPoi 导出Excel
		ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
		List<UserBankcard> pageList = userBankcardService.list(qw);
		//导出文件名称
		mv.addObject(NormalExcelConstants.FILE_NAME, "代付平台用户银行卡列表");
		mv.addObject(NormalExcelConstants.CLASS, UserBankcard.class);
		mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("代付平台用户银行卡列表数据", "导出人:Jeecg", "导出信息"));
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
				List<UserBankcard> listUserBankcards = ExcelImportUtil.importExcel(file.getInputStream(), UserBankcard.class, params);
				userBankcardService.saveBatch(listUserBankcards);
				return Result.ok("文件导入成功！数据行数:" + listUserBankcards.size());
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
