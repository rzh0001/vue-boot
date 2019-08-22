package org.jeecg.modules.pay.controller;

import java.util.*;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.pay.entity.OrderInfoEntity;
import org.jeecg.modules.pay.service.IOrderInfoEntityService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;

import org.jeecg.modules.system.entity.SysUser;
import org.jeecg.modules.system.service.ISysUserService;
import org.jeecg.modules.util.BaseConstant;
import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;
import com.alibaba.fastjson.JSON;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

 /**
 * @Description: 订单信息
 * @Author: jeecg-boot
 * @Date:   2019-07-26
 * @Version: V1.0
 */
@Slf4j
@Api(tags="订单信息")
@RestController
@RequestMapping("/pay/orderInfoEntity")
public class OrderInfoEntityController {
	@Autowired
	private IOrderInfoEntityService orderInfoEntityService;
	 @Autowired
	 private ISysUserService userService;
	/**
	  * 分页列表查询
	 * @param orderInfoEntity
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "订单信息-分页列表查询")
	@ApiOperation(value="订单信息-分页列表查询", notes="订单信息-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<OrderInfoEntity>> queryPageList(OrderInfoEntity orderInfoEntity,
									  @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
									  @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
									  HttpServletRequest req) {
		Result<IPage<OrderInfoEntity>> result = new Result<IPage<OrderInfoEntity>>();
		//获取系统用户
		LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
		SysUser sysUser = userService.getUserByName(loginUser.getUsername());

		QueryWrapper<OrderInfoEntity> queryWrapper = QueryGenerator.initQueryWrapper(orderInfoEntity, req.getParameterMap());
		Page<OrderInfoEntity> page = new Page<OrderInfoEntity>(pageNo, pageSize);
		IPage<OrderInfoEntity> pageList = orderInfoEntityService.page(page, queryWrapper);
		List<OrderInfoEntity> db = pageList.getRecords();
		List<OrderInfoEntity> newList = new ArrayList<>();
		if (BaseConstant.USER_AGENT.equals(sysUser.getMemberType())){
			//代理能看到该代理下面的商户
			//获取该代理下的商户
			List<SysUser> users = userService.getUserByAgent(sysUser.getUsername());
			List<String> userNames = new ArrayList<>();
			if(!CollectionUtils.isEmpty(users)){
				for(SysUser u :users){
					userNames.add(u.getUsername());
				}
			}
			for(OrderInfoEntity order:db){
				if(userNames.contains(order.getUserName())){
					newList.add(order);
					continue;
				}
			}
			pageList.setRecords(newList);
		}else if(BaseConstant.USER_MERCHANTS.equals(sysUser.getMemberType())){
			//商户只能看到自己的订单;
			for(OrderInfoEntity order:db){
				if(sysUser.getUsername().equals(order.getUserName())){
					newList.add(order);
					continue;
				}
			}
			pageList.setRecords(newList);
		}else if(BaseConstant.USER_REFERENCES.equals(sysUser.getMemberType())){
			//介绍人能看到介绍的商户的订单
			//获取介绍人介绍的商户的名称
			List<String> users = userService.getUserByRefer(sysUser.getUsername());
			for(OrderInfoEntity order:db){
				if(users.contains(order.getUserName())){
					newList.add(order);
					continue;
				}
			}
			pageList.setRecords(newList);
		}
		result.setSuccess(true);
		result.setResult(pageList);
		return result;
	}
	
	/**
	  *   添加
	 * @param orderInfoEntity
	 * @return
	 */
	@AutoLog(value = "订单信息-添加")
	@ApiOperation(value="订单信息-添加", notes="订单信息-添加")
	@PostMapping(value = "/add")
	public Result<OrderInfoEntity> add(@RequestBody OrderInfoEntity orderInfoEntity) {
		Result<OrderInfoEntity> result = new Result<OrderInfoEntity>();
		try {
			orderInfoEntityService.save(orderInfoEntity);
			result.success("添加成功！");
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			result.error500("操作失败");
		}
		return result;
	}
	
	/**
	  *  编辑
	 * @param orderInfoEntity
	 * @return
	 */
	@AutoLog(value = "订单信息-编辑")
	@ApiOperation(value="订单信息-编辑", notes="订单信息-编辑")
	@PutMapping(value = "/edit")
	public Result<OrderInfoEntity> edit(@RequestBody OrderInfoEntity orderInfoEntity) {
		Result<OrderInfoEntity> result = new Result<OrderInfoEntity>();
		OrderInfoEntity orderInfoEntityEntity = orderInfoEntityService.getById(orderInfoEntity.getId());
		if(orderInfoEntityEntity==null) {
			result.error500("未找到对应实体");
		}else {
			boolean ok = orderInfoEntityService.updateById(orderInfoEntity);
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
	@AutoLog(value = "订单信息-通过id删除")
	@ApiOperation(value="订单信息-通过id删除", notes="订单信息-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name="id",required=true) String id) {
		try {
			orderInfoEntityService.removeById(id);
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
	@AutoLog(value = "订单信息-批量删除")
	@ApiOperation(value="订单信息-批量删除", notes="订单信息-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<OrderInfoEntity> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		Result<OrderInfoEntity> result = new Result<OrderInfoEntity>();
		if(ids==null || "".equals(ids.trim())) {
			result.error500("参数不识别！");
		}else {
			this.orderInfoEntityService.removeByIds(Arrays.asList(ids.split(",")));
			result.success("删除成功!");
		}
		return result;
	}
	
	/**
	  * 通过id查询
	 * @param id
	 * @return
	 */
	@AutoLog(value = "订单信息-通过id查询")
	@ApiOperation(value="订单信息-通过id查询", notes="订单信息-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<OrderInfoEntity> queryById(@RequestParam(name="id",required=true) String id) {
		Result<OrderInfoEntity> result = new Result<OrderInfoEntity>();
		OrderInfoEntity orderInfoEntity = orderInfoEntityService.getById(id);
		if(orderInfoEntity==null) {
			result.error500("未找到对应实体");
		}else {
			result.setResult(orderInfoEntity);
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
      QueryWrapper<OrderInfoEntity> queryWrapper = null;
      try {
          String paramsStr = request.getParameter("paramsStr");
          if (oConvertUtils.isNotEmpty(paramsStr)) {
              String deString = URLDecoder.decode(paramsStr, "UTF-8");
              OrderInfoEntity orderInfoEntity = JSON.parseObject(deString, OrderInfoEntity.class);
              queryWrapper = QueryGenerator.initQueryWrapper(orderInfoEntity, request.getParameterMap());
          }
      } catch (UnsupportedEncodingException e) {
          e.printStackTrace();
      }

      //Step.2 AutoPoi 导出Excel
      ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
      List<OrderInfoEntity> pageList = orderInfoEntityService.list(queryWrapper);
      //导出文件名称
      mv.addObject(NormalExcelConstants.FILE_NAME, "订单信息列表");
      mv.addObject(NormalExcelConstants.CLASS, OrderInfoEntity.class);
      mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("订单信息列表数据", "导出人:Jeecg", "导出信息"));
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
              List<OrderInfoEntity> listOrderInfoEntitys = ExcelImportUtil.importExcel(file.getInputStream(), OrderInfoEntity.class, params);
              orderInfoEntityService.saveBatch(listOrderInfoEntitys);
              return Result.ok("文件导入成功！数据行数:" + listOrderInfoEntitys.size());
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
