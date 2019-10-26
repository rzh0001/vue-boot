package org.jeecg.modules.df.controller;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.df.entity.RechargeOrder;
import org.jeecg.modules.df.service.IRechargeOrderService;
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
 * @Description: 代付充值订单
 * @Author: jeecg-boot
  * @Date: 2019-10-26
 * @Version: V1.0
 */
@Slf4j
@Api(tags="代付充值订单")
@RestController
@RequestMapping("/df/rechargeOrder")
public class RechargeOrderController {
	@Autowired
	private IRechargeOrderService rechargeOrderService;
	
	/**
	 * 分页列表查询
	 * @param rechargeOrder
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "代付充值订单-分页列表查询")
	@ApiOperation(value="代付充值订单-分页列表查询", notes="代付充值订单-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<RechargeOrder>> queryPageList(RechargeOrder rechargeOrder,
									  @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
									  @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
									  HttpServletRequest req) {
		Result<IPage<RechargeOrder>> result = new Result<IPage<RechargeOrder>>();
		QueryWrapper<RechargeOrder> queryWrapper = QueryGenerator.initQueryWrapper(rechargeOrder, req.getParameterMap());
		Page<RechargeOrder> page = new Page<RechargeOrder>(pageNo, pageSize);
		IPage<RechargeOrder> pageList = rechargeOrderService.page(page, queryWrapper);
		result.setSuccess(true);
		result.setResult(pageList);
		return result;
	}
	
	/**
	 *   添加
	 * @param rechargeOrder
	 * @return
	 */
	@AutoLog(value = "代付充值订单-添加")
	@ApiOperation(value="代付充值订单-添加", notes="代付充值订单-添加")
	@PostMapping(value = "/add")
	public Result<RechargeOrder> add(@RequestBody RechargeOrder rechargeOrder) {
		Result<RechargeOrder> result = new Result<RechargeOrder>();
		try {
			rechargeOrderService.save(rechargeOrder);
			result.success("添加成功！");
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			result.error500("操作失败");
		}
		return result;
	}
	
	/**
	 *  编辑
	 * @param rechargeOrder
	 * @return
	 */
	@AutoLog(value = "代付充值订单-编辑")
	@ApiOperation(value="代付充值订单-编辑", notes="代付充值订单-编辑")
	@PutMapping(value = "/edit")
	public Result<RechargeOrder> edit(@RequestBody RechargeOrder rechargeOrder) {
		Result<RechargeOrder> result = new Result<RechargeOrder>();
		RechargeOrder rechargeOrderEntity = rechargeOrderService.getById(rechargeOrder.getId());
		if(rechargeOrderEntity==null) {
			result.error500("未找到对应实体");
		}else {
			boolean ok = rechargeOrderService.updateById(rechargeOrder);
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
	@AutoLog(value = "代付充值订单-通过id删除")
	@ApiOperation(value="代付充值订单-通过id删除", notes="代付充值订单-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name="id",required=true) String id) {
		try {
			rechargeOrderService.removeById(id);
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
	@AutoLog(value = "代付充值订单-批量删除")
	@ApiOperation(value="代付充值订单-批量删除", notes="代付充值订单-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<RechargeOrder> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		Result<RechargeOrder> result = new Result<RechargeOrder>();
		if(ids==null || "".equals(ids.trim())) {
			result.error500("参数不识别！");
		}else {
			this.rechargeOrderService.removeByIds(Arrays.asList(ids.split(",")));
			result.success("删除成功!");
		}
		return result;
	}
	
	/**
	 * 通过id查询
	 * @param id
	 * @return
	 */
	@AutoLog(value = "代付充值订单-通过id查询")
	@ApiOperation(value="代付充值订单-通过id查询", notes="代付充值订单-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<RechargeOrder> queryById(@RequestParam(name="id",required=true) String id) {
		Result<RechargeOrder> result = new Result<RechargeOrder>();
		RechargeOrder rechargeOrder = rechargeOrderService.getById(id);
		if(rechargeOrder==null) {
			result.error500("未找到对应实体");
		}else {
			result.setResult(rechargeOrder);
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
      QueryWrapper<RechargeOrder> queryWrapper = null;
      try {
          String paramsStr = request.getParameter("paramsStr");
          if (oConvertUtils.isNotEmpty(paramsStr)) {
              String deString = URLDecoder.decode(paramsStr, "UTF-8");
              RechargeOrder rechargeOrder = JSON.parseObject(deString, RechargeOrder.class);
              queryWrapper = QueryGenerator.initQueryWrapper(rechargeOrder, request.getParameterMap());
          }
      } catch (UnsupportedEncodingException e) {
          e.printStackTrace();
      }

      //Step.2 AutoPoi 导出Excel
      ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
      List<RechargeOrder> pageList = rechargeOrderService.list(queryWrapper);
      //导出文件名称
      mv.addObject(NormalExcelConstants.FILE_NAME, "代付充值订单列表");
      mv.addObject(NormalExcelConstants.CLASS, RechargeOrder.class);
      mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("代付充值订单列表数据", "导出人:Jeecg", "导出信息"));
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
              List<RechargeOrder> listRechargeOrders = ExcelImportUtil.importExcel(file.getInputStream(), RechargeOrder.class, params);
              rechargeOrderService.saveBatch(listRechargeOrders);
              return Result.ok("文件导入成功！数据行数:" + listRechargeOrders.size());
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
