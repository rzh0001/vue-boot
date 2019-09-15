package org.jeecg.modules.pay.controller;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.constant.PayConstant;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.pay.entity.UserAmountReport;
import org.jeecg.modules.pay.service.IUserAmountReportService;
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
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

 /**
 * @Description: 用户余额报表-期初余额 每天0点更新
 * @Author: jeecg-boot
 * @Date:   2019-09-11
 * @Version: V1.0
 */
@Slf4j
@Api(tags="用户余额报表-期初余额 每天0点更新")
@RestController
@RequestMapping("/pay/userAmountReport")
public class UserAmountReportController {
	@Autowired
	private IUserAmountReportService userAmountReportService;
	
	/**
	 * 分页列表查询
	 * @param userAmountReport
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "用户余额报表-期初余额 每天0点更新-分页列表查询")
	@ApiOperation(value="用户余额报表-期初余额 每天0点更新-分页列表查询", notes="用户余额报表-期初余额 每天0点更新-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<UserAmountReport>> queryPageList(UserAmountReport userAmountReport,
									  @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
									  @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
									  HttpServletRequest req) {
		Result<IPage<UserAmountReport>> result = new Result<IPage<UserAmountReport>>();
		QueryWrapper<UserAmountReport> queryWrapper = QueryGenerator.initQueryWrapper(userAmountReport, req.getParameterMap());
		Page<UserAmountReport> page = new Page<UserAmountReport>(pageNo, pageSize);
		LoginUser optUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
		if (optUser.getMemberType() != null) {
			switch (optUser.getMemberType()) {
				case PayConstant.MEMBER_TYPE_AGENT:
					queryWrapper.lambda().eq(UserAmountReport::getAgentId, optUser.getId());
					break;
				case PayConstant.MEMBER_TYPE_SALESMAN:
					queryWrapper.lambda().eq(UserAmountReport::getSalesmanId, optUser.getId());
					break;
				case PayConstant.MEMBER_TYPE_MEMBER:
					queryWrapper.lambda().eq(UserAmountReport::getUserId, optUser.getId());
					break;
				default:
			}
		}
		IPage<UserAmountReport> pageList = userAmountReportService.page(page, queryWrapper);
		result.setSuccess(true);
		result.setResult(pageList);
		return result;
	}
	
	/**
	 *   添加
	 * @param userAmountReport
	 * @return
	 */
	@AutoLog(value = "用户余额报表-期初余额 每天0点更新-添加")
	@ApiOperation(value="用户余额报表-期初余额 每天0点更新-添加", notes="用户余额报表-期初余额 每天0点更新-添加")
	@PostMapping(value = "/add")
	public Result<UserAmountReport> add(@RequestBody UserAmountReport userAmountReport) {
		Result<UserAmountReport> result = new Result<UserAmountReport>();
		try {
			userAmountReportService.save(userAmountReport);
			result.success("添加成功！");
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			result.error500("操作失败");
		}
		return result;
	}
	
	/**
	 *  编辑
	 * @param userAmountReport
	 * @return
	 */
	@AutoLog(value = "用户余额报表-期初余额 每天0点更新-编辑")
	@ApiOperation(value="用户余额报表-期初余额 每天0点更新-编辑", notes="用户余额报表-期初余额 每天0点更新-编辑")
	@PutMapping(value = "/edit")
	public Result<UserAmountReport> edit(@RequestBody UserAmountReport userAmountReport) {
		Result<UserAmountReport> result = new Result<UserAmountReport>();
		UserAmountReport userAmountReportEntity = userAmountReportService.getById(userAmountReport.getId());
		if(userAmountReportEntity==null) {
			result.error500("未找到对应实体");
		}else {
			boolean ok = userAmountReportService.updateById(userAmountReport);
			//TODO 返回false说明什么？
			if(ok) {
				result.success("修改成功!");
			}
		}
		
		return result;
	}
	
	/**
	 *   通过id删除
	 * @param id
	 * @return
	 */
	@AutoLog(value = "用户余额报表-期初余额 每天0点更新-通过id删除")
	@ApiOperation(value="用户余额报表-期初余额 每天0点更新-通过id删除", notes="用户余额报表-期初余额 每天0点更新-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name="id",required=true) String id) {
		try {
			userAmountReportService.removeById(id);
		} catch (Exception e) {
			log.error("删除失败",e.getMessage());
			return Result.error("删除失败!");
		}
		return Result.ok("删除成功!");
	}
	
	/**
	 *  批量删除
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "用户余额报表-期初余额 每天0点更新-批量删除")
	@ApiOperation(value="用户余额报表-期初余额 每天0点更新-批量删除", notes="用户余额报表-期初余额 每天0点更新-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<UserAmountReport> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		Result<UserAmountReport> result = new Result<UserAmountReport>();
		if(ids==null || "".equals(ids.trim())) {
			result.error500("参数不识别！");
		}else {
			this.userAmountReportService.removeByIds(Arrays.asList(ids.split(",")));
			result.success("删除成功!");
		}
		return result;
	}
	
	/**
	 * 通过id查询
	 * @param id
	 * @return
	 */
	@AutoLog(value = "用户余额报表-期初余额 每天0点更新-通过id查询")
	@ApiOperation(value="用户余额报表-期初余额 每天0点更新-通过id查询", notes="用户余额报表-期初余额 每天0点更新-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<UserAmountReport> queryById(@RequestParam(name="id",required=true) String id) {
		Result<UserAmountReport> result = new Result<UserAmountReport>();
		UserAmountReport userAmountReport = userAmountReportService.getById(id);
		if(userAmountReport==null) {
			result.error500("未找到对应实体");
		}else {
			result.setResult(userAmountReport);
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
      QueryWrapper<UserAmountReport> queryWrapper = null;
      try {
          String paramsStr = request.getParameter("paramsStr");
          if (oConvertUtils.isNotEmpty(paramsStr)) {
              String deString = URLDecoder.decode(paramsStr, "UTF-8");
              UserAmountReport userAmountReport = JSON.parseObject(deString, UserAmountReport.class);
              queryWrapper = QueryGenerator.initQueryWrapper(userAmountReport, request.getParameterMap());
          }
      } catch (UnsupportedEncodingException e) {
          e.printStackTrace();
      }

      //Step.2 AutoPoi 导出Excel
      ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
      List<UserAmountReport> pageList = userAmountReportService.list(queryWrapper);
      //导出文件名称
      mv.addObject(NormalExcelConstants.FILE_NAME, "用户余额报表-期初余额 每天0点更新列表");
      mv.addObject(NormalExcelConstants.CLASS, UserAmountReport.class);
      mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("用户余额报表-期初余额 每天0点更新列表数据", "导出人:Jeecg", "导出信息"));
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
              List<UserAmountReport> listUserAmountReports = ExcelImportUtil.importExcel(file.getInputStream(), UserAmountReport.class, params);
              userAmountReportService.saveBatch(listUserAmountReports);
              return Result.ok("文件导入成功！数据行数:" + listUserAmountReports.size());
          } catch (Exception e) {
              log.error(e.getMessage(),e);
              return Result.error("文件导入失败:"+e.getMessage());
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
