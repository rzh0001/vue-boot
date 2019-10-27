package org.jeecg.modules.system.controller;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Param;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.system.entity.UserAmountEntity;
import org.jeecg.modules.system.service.IUserAmountEntityService;
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
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @Description: 用户余额
 * @Author: jeecg-boot 
 * @Date: 2019-07-31 
 * @Version: V1.0
 */
@Slf4j
@Api(tags = "用户余额")
@RestController
@RequestMapping("/sys/userAmountEntity")
public class UserAmountEntityController {
	@Autowired
	private IUserAmountEntityService userAmountEntityService;
	
	/**
	 * 分页列表查询
	 *
	 * @param userAmountEntity
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "用户余额-分页列表查询")
	@ApiOperation(value = "用户余额-分页列表查询", notes = "用户余额-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<UserAmountEntity>> queryPageList(
			UserAmountEntity userAmountEntity,
			@RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
			@RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
			HttpServletRequest req) {
		Result<IPage<UserAmountEntity>> result = new Result<IPage<UserAmountEntity>>();
		QueryWrapper<UserAmountEntity> queryWrapper =
				QueryGenerator.initQueryWrapper(userAmountEntity, req.getParameterMap());
		Page<UserAmountEntity> page = new Page<UserAmountEntity>(pageNo, pageSize);
		IPage<UserAmountEntity> pageList = userAmountEntityService.page(page, queryWrapper);
		result.setSuccess(true);
		result.setResult(pageList);
		return result;
	}
	
	@AutoLog(value = "用户余额-获取会员可提现余额")
	@ApiOperation(value = "用户余额-获取会员可提现余额", notes = "用户余额-获取会员可提现余额")
	@GetMapping(value = "/getMemberAvailableAmount")
	public Result<BigDecimal> getMemberAvailableAmount(@Param("username") String username) {
		Result<BigDecimal> result = new Result<>();
		String name;
		if (StringUtils.isNotBlank(username)) {
			name = username;
		} else {
			LoginUser opUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
			name = opUser.getUsername();
		}
		LambdaQueryWrapper<UserAmountEntity> wrapper = new QueryWrapper<UserAmountEntity>().lambda().eq(UserAmountEntity::getUserName, name);
		UserAmountEntity amount = userAmountEntityService.getOne(wrapper);
		if (amount == null) {
			result.error500("获取余额失败，请联系管理员");
		} else {
			result.setSuccess(true);
			result.setResult(amount.getAmount());
		}
		return result;
	}
	
	
	/**
	 * 添加
	 *
	 * @param userAmountEntity
	 * @return
	 */
	@AutoLog(value = "用户余额-添加")
	@ApiOperation(value = "用户余额-添加", notes = "用户余额-添加")
	@PostMapping(value = "/add")
	public Result<UserAmountEntity> add(@RequestBody UserAmountEntity userAmountEntity) {
		Result<UserAmountEntity> result = new Result<UserAmountEntity>();
		try {
			userAmountEntityService.save(userAmountEntity);
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
	 * @param userAmountEntity
	 * @return
	 */
	@AutoLog(value = "用户余额-编辑")
	@ApiOperation(value = "用户余额-编辑", notes = "用户余额-编辑")
	@PutMapping(value = "/edit")
	public Result<UserAmountEntity> edit(@RequestBody UserAmountEntity userAmountEntity) {
		Result<UserAmountEntity> result = new Result<UserAmountEntity>();
		UserAmountEntity userAmountEntityEntity =
				userAmountEntityService.getById(userAmountEntity.getId());
		if (userAmountEntityEntity == null) {
			result.error500("未找到对应实体");
		} else {
			boolean ok = userAmountEntityService.updateById(userAmountEntity);
			// TODO 返回false说明什么？
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
	@AutoLog(value = "用户余额-通过id删除")
	@ApiOperation(value = "用户余额-通过id删除", notes = "用户余额-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name = "id", required = true) String id) {
		try {
			userAmountEntityService.removeById(id);
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
	@AutoLog(value = "用户余额-批量删除")
	@ApiOperation(value = "用户余额-批量删除", notes = "用户余额-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<UserAmountEntity> deleteBatch(
			@RequestParam(name = "ids", required = true) String ids) {
		Result<UserAmountEntity> result = new Result<UserAmountEntity>();
		if (ids == null || "".equals(ids.trim())) {
			result.error500("参数不识别！");
		} else {
			this.userAmountEntityService.removeByIds(Arrays.asList(ids.split(",")));
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
	@AutoLog(value = "用户余额-通过id查询")
	@ApiOperation(value = "用户余额-通过id查询", notes = "用户余额-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<UserAmountEntity> queryById(@RequestParam(name = "id", required = true) String id) {
		Result<UserAmountEntity> result = new Result<UserAmountEntity>();
		UserAmountEntity userAmountEntity = userAmountEntityService.getById(id);
		if (userAmountEntity == null) {
			result.error500("未找到对应实体");
		} else {
			result.setResult(userAmountEntity);
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
		QueryWrapper<UserAmountEntity> queryWrapper = null;
		try {
			String paramsStr = request.getParameter("paramsStr");
			if (oConvertUtils.isNotEmpty(paramsStr)) {
				String deString = URLDecoder.decode(paramsStr, "UTF-8");
				UserAmountEntity userAmountEntity = JSON.parseObject(deString, UserAmountEntity.class);
				queryWrapper = QueryGenerator.initQueryWrapper(userAmountEntity, request.getParameterMap());
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		// Step.2 AutoPoi 导出Excel
		ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
		List<UserAmountEntity> pageList = userAmountEntityService.list(queryWrapper);
		// 导出文件名称
		mv.addObject(NormalExcelConstants.FILE_NAME, "用户余额列表");
		mv.addObject(NormalExcelConstants.CLASS, UserAmountEntity.class);
		mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("用户余额列表数据", "导出人:Jeecg", "导出信息"));
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
				List<UserAmountEntity> listUserAmountEntitys =
						ExcelImportUtil.importExcel(file.getInputStream(), UserAmountEntity.class, params);
				userAmountEntityService.saveBatch(listUserAmountEntitys);
				return Result.ok("文件导入成功！数据行数:" + listUserAmountEntitys.size());
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
