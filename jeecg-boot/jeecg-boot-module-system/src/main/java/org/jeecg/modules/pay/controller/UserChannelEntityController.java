package org.jeecg.modules.pay.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.pay.entity.UserChannelEntity;
import org.jeecg.modules.pay.service.IUserChannelEntityService;
import java.util.Date;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;

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
import com.alibaba.fastjson.JSON;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

 /**
 * @Description: 用户关联通道
 * @Author: jeecg-boot
 * @Date:   2019-07-26
 * @Version: V1.0
 */
@Slf4j
@Api(tags="用户关联通道")
@RestController
@RequestMapping("/pay/userChannelEntity")
public class UserChannelEntityController {
	@Autowired
	private IUserChannelEntityService userChannelEntityService;
	
	/**
	  * 分页列表查询
	 * @param userChannelEntity
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "用户关联通道-分页列表查询")
	@ApiOperation(value="用户关联通道-分页列表查询", notes="用户关联通道-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<UserChannelEntity>> queryPageList(UserChannelEntity userChannelEntity,
									  @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
									  @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
									  HttpServletRequest req) {
		Result<IPage<UserChannelEntity>> result = new Result<IPage<UserChannelEntity>>();
		QueryWrapper<UserChannelEntity> queryWrapper = QueryGenerator.initQueryWrapper(userChannelEntity, req.getParameterMap());
		Page<UserChannelEntity> page = new Page<UserChannelEntity>(pageNo, pageSize);
		IPage<UserChannelEntity> pageList = userChannelEntityService.page(page, queryWrapper);
		result.setSuccess(true);
		result.setResult(pageList);
		return result;
	}
	
	/**
	  *   添加
	 * @param userChannelEntity
	 * @return
	 */
	@AutoLog(value = "用户关联通道-添加")
	@ApiOperation(value="用户关联通道-添加", notes="用户关联通道-添加")
	@PostMapping(value = "/add")
	public Result<UserChannelEntity> add(@RequestBody UserChannelEntity userChannelEntity) {
		Result<UserChannelEntity> result = new Result<UserChannelEntity>();
		try {
			userChannelEntityService.save(userChannelEntity);
			result.success("添加成功！");
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			result.error500("操作失败");
		}
		return result;
	}
	
	/**
	  *  编辑
	 * @param userChannelEntity
	 * @return
	 */
	@AutoLog(value = "用户关联通道-编辑")
	@ApiOperation(value="用户关联通道-编辑", notes="用户关联通道-编辑")
	@PutMapping(value = "/edit")
	public Result<UserChannelEntity> edit(@RequestBody UserChannelEntity userChannelEntity) {
		Result<UserChannelEntity> result = new Result<UserChannelEntity>();
		UserChannelEntity userChannelEntityEntity = userChannelEntityService.getById(userChannelEntity.getId());
		if(userChannelEntityEntity==null) {
			result.error500("未找到对应实体");
		}else {
			boolean ok = userChannelEntityService.updateById(userChannelEntity);
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
	@AutoLog(value = "用户关联通道-通过id删除")
	@ApiOperation(value="用户关联通道-通过id删除", notes="用户关联通道-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name="id",required=true) String id) {
		try {
			userChannelEntityService.removeById(id);
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
	@AutoLog(value = "用户关联通道-批量删除")
	@ApiOperation(value="用户关联通道-批量删除", notes="用户关联通道-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<UserChannelEntity> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		Result<UserChannelEntity> result = new Result<UserChannelEntity>();
		if(ids==null || "".equals(ids.trim())) {
			result.error500("参数不识别！");
		}else {
			this.userChannelEntityService.removeByIds(Arrays.asList(ids.split(",")));
			result.success("删除成功!");
		}
		return result;
	}
	
	/**
	  * 通过id查询
	 * @param id
	 * @return
	 */
	@AutoLog(value = "用户关联通道-通过id查询")
	@ApiOperation(value="用户关联通道-通过id查询", notes="用户关联通道-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<UserChannelEntity> queryById(@RequestParam(name="id",required=true) String id) {
		Result<UserChannelEntity> result = new Result<UserChannelEntity>();
		UserChannelEntity userChannelEntity = userChannelEntityService.getById(id);
		if(userChannelEntity==null) {
			result.error500("未找到对应实体");
		}else {
			result.setResult(userChannelEntity);
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
      QueryWrapper<UserChannelEntity> queryWrapper = null;
      try {
          String paramsStr = request.getParameter("paramsStr");
          if (oConvertUtils.isNotEmpty(paramsStr)) {
              String deString = URLDecoder.decode(paramsStr, "UTF-8");
              UserChannelEntity userChannelEntity = JSON.parseObject(deString, UserChannelEntity.class);
              queryWrapper = QueryGenerator.initQueryWrapper(userChannelEntity, request.getParameterMap());
          }
      } catch (UnsupportedEncodingException e) {
          e.printStackTrace();
      }

      //Step.2 AutoPoi 导出Excel
      ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
      List<UserChannelEntity> pageList = userChannelEntityService.list(queryWrapper);
      //导出文件名称
      mv.addObject(NormalExcelConstants.FILE_NAME, "用户关联通道列表");
      mv.addObject(NormalExcelConstants.CLASS, UserChannelEntity.class);
      mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("用户关联通道列表数据", "导出人:Jeecg", "导出信息"));
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
              List<UserChannelEntity> listUserChannelEntitys = ExcelImportUtil.importExcel(file.getInputStream(), UserChannelEntity.class, params);
              userChannelEntityService.saveBatch(listUserChannelEntitys);
              return Result.ok("文件导入成功！数据行数:" + listUserChannelEntitys.size());
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
