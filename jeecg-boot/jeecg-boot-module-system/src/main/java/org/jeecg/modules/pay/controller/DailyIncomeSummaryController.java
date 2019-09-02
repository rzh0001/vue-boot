package org.jeecg.modules.pay.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
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
import org.jeecg.modules.pay.service.IDailyIncomeSummaryService;
import org.jeecg.modules.pay.vo.DailyIncomeSummaryVO;
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
import java.util.Date;
import java.util.List;
import java.util.Map;

 /**
 * @Description: 今日交易统计
 * @Author: jeecg-boot
 * @Date:   2019-09-02
 * @Version: V1.0
 */
@Slf4j
@Api(tags="今日交易统计")
@RestController
@RequestMapping("/pay/dailyIncomeSummary")
public class DailyIncomeSummaryController {
	@Autowired
	private IDailyIncomeSummaryService dailyIncomeSummaryService;
	
	/**
	 * 分页列表查询
	 * @param queryParams
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "今日交易统计-分页列表查询")
	@ApiOperation(value="今日交易统计-分页列表查询", notes="今日交易统计-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<DailyIncomeSummaryVO>> queryPageList(DailyIncomeSummaryVO queryParams,
															 @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
															 @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
															 HttpServletRequest req) {
		Result<IPage<DailyIncomeSummaryVO>> result = new Result<IPage<DailyIncomeSummaryVO>>();
		Map<String, Object> map = BeanUtil.beanToMap(queryParams);
		if (ObjectUtil.isNull(map.get("transTime"))) {
			map.put("date", new Date());
		} else {
			map.put("date", DateUtil.parse(queryParams.getTransTime()));
		}
		
		LoginUser opUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
		if (opUser.getMemberType() != null) {
			switch (opUser.getMemberType()) {
				case PayConstant.MEMBER_TYPE_AGENT:
					map.put("agentId", opUser.getId());
					break;
				case PayConstant.MEMBER_TYPE_SALESMAN:
					map.put("salesmanId", opUser.getId());
					break;
				case PayConstant.MEMBER_TYPE_MEMBER:
					map.put("userId", opUser.getId());
					break;
				default:
			}
		}
		Page<DailyIncomeSummaryVO> page = new Page<DailyIncomeSummaryVO>(pageNo, pageSize);
		IPage<DailyIncomeSummaryVO> pageList = dailyIncomeSummaryService.pageSummary(page, map);
		result.setSuccess(true);
		result.setResult(pageList);
		return result;
	}
	
	
	/**
	 * 通过id查询
	 * @param id
	 * @return
	 */
	@AutoLog(value = "今日交易统计-通过id查询")
	@ApiOperation(value="今日交易统计-通过id查询", notes="今日交易统计-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<DailyIncomeSummaryVO> queryById(@RequestParam(name="id",required=true) String id) {
		Result<DailyIncomeSummaryVO> result = new Result<DailyIncomeSummaryVO>();
		DailyIncomeSummaryVO dailyIncomeSummaryVO = dailyIncomeSummaryService.getById(id);
		if(dailyIncomeSummaryVO ==null) {
			result.error500("未找到对应实体");
		}else {
			result.setResult(dailyIncomeSummaryVO);
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
      QueryWrapper<DailyIncomeSummaryVO> queryWrapper = null;
      try {
          String paramsStr = request.getParameter("paramsStr");
          if (oConvertUtils.isNotEmpty(paramsStr)) {
              String deString = URLDecoder.decode(paramsStr, "UTF-8");
              DailyIncomeSummaryVO dailyIncomeSummaryVO = JSON.parseObject(deString, DailyIncomeSummaryVO.class);
              queryWrapper = QueryGenerator.initQueryWrapper(dailyIncomeSummaryVO, request.getParameterMap());
          }
      } catch (UnsupportedEncodingException e) {
          e.printStackTrace();
      }

      //Step.2 AutoPoi 导出Excel
      ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
      List<DailyIncomeSummaryVO> pageList = dailyIncomeSummaryService.list(queryWrapper);
      //导出文件名称
      mv.addObject(NormalExcelConstants.FILE_NAME, "今日交易统计列表");
      mv.addObject(NormalExcelConstants.CLASS, DailyIncomeSummaryVO.class);
      mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("今日交易统计列表数据", "导出人:Jeecg", "导出信息"));
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
              List<DailyIncomeSummaryVO> listDailyIncomeSummaryVOS = ExcelImportUtil.importExcel(file.getInputStream(), DailyIncomeSummaryVO.class, params);
              dailyIncomeSummaryService.saveBatch(listDailyIncomeSummaryVOS);
              return Result.ok("文件导入成功！数据行数:" + listDailyIncomeSummaryVOS.size());
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
