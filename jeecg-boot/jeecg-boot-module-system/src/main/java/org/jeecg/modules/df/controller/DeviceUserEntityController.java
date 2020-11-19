package org.jeecg.modules.df.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.df.dto.DeviceUserParam;
import org.jeecg.modules.df.entity.DeviceUserEntity;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;

import org.jeecg.modules.df.service.impl.DeviceUserEntityServiceImpl;
import org.jeecg.modules.df.vo.DeviceUserInfoVO;
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
 * @Description: 商户关联设备
 * @Author: jeecg-boot
 * @Date:   2020-11-18
 * @Version: V1.0
 */
@Slf4j
@Api(tags="商户关联设备")
@RestController
@RequestMapping("/df/deviceUserEntity")
public class DeviceUserEntityController {
	@Autowired
	private DeviceUserEntityServiceImpl deviceUserEntityService;
	
	/**
	 * 分页列表查询
	 * @param deviceUserEntity
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "商户关联设备-分页列表查询")
	@ApiOperation(value="商户关联设备-分页列表查询", notes="商户关联设备-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<DeviceUserEntity>> queryPageList(DeviceUserEntity deviceUserEntity,
									  @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
									  @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
									  HttpServletRequest req) {
		Result<IPage<DeviceUserEntity>> result = new Result<IPage<DeviceUserEntity>>();
		QueryWrapper<DeviceUserEntity> queryWrapper = QueryGenerator.initQueryWrapper(deviceUserEntity, req.getParameterMap());
		Page<DeviceUserEntity> page = new Page<DeviceUserEntity>(pageNo, pageSize);
		IPage<DeviceUserEntity> pageList = deviceUserEntityService.page(page, queryWrapper);
		result.setSuccess(true);
		result.setResult(pageList);
		return result;
	}
	
	/**
	 *   添加
	 * @param deviceUserEntity
	 * @return
	 */
	@AutoLog(value = "商户关联设备-添加")
	@ApiOperation(value="商户关联设备-添加", notes="商户关联设备-添加")
	@PostMapping(value = "/add")
	public Result<DeviceUserEntity> add(@RequestBody DeviceUserEntity deviceUserEntity) {
		Result<DeviceUserEntity> result = new Result<DeviceUserEntity>();
		try {
			deviceUserEntityService.save(deviceUserEntity);
			result.success("添加成功！");
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			result.error500("操作失败");
		}
		return result;
	}

	 /**
	  * 保存设备和商户的关联关系
	  * @param param
	  * @return
	  */
	 @PostMapping(value = "/addRelation")
	public Result<String> saveUserRelation(@RequestBody DeviceUserParam param){
		 Result<String> result = new Result<String>();
		 result.success("success");
		if(StringUtils.isEmpty(param.getUserName())){
			return result;
		}
		List<String> userNames = Arrays.stream(param.getUserName().split(",")).collect(Collectors.toList());
		 List<DeviceUserEntity> exists = deviceUserEntityService.findByCode(param.getDeviceCode());
		 //去重
		if(!CollectionUtils.isEmpty(exists)){
			List<String> existNames = exists.stream().map(key->key.getUserName()).collect(Collectors.toList());
			userNames.removeAll(existNames);
		}
		//保存
		 if(CollectionUtils.isEmpty(userNames)){
			 return result;
		 }
		 deviceUserEntityService.batchSave(param.getDeviceCode(),userNames);
		 return result;
	}

	 /**
	  * 查询设备管理的账号
	  * @param deviceCode
	  * @return
	  */
	@GetMapping("/findDeviceUserInfo")
	public Result<List<DeviceUserInfoVO>> findDeviceUserInfo(String deviceCode){
		Result<List<DeviceUserInfoVO>> result = new Result<>();
		List<DeviceUserInfoVO> vo = deviceUserEntityService.findDeviceUserInfo(deviceCode);
		result.setResult(vo);
		return result;
	}
	/**
	 *  编辑
	 * @param deviceUserEntity
	 * @return
	 */
	@AutoLog(value = "商户关联设备-编辑")
	@ApiOperation(value="商户关联设备-编辑", notes="商户关联设备-编辑")
	@PutMapping(value = "/edit")
	public Result<DeviceUserEntity> edit(@RequestBody DeviceUserEntity deviceUserEntity) {
		Result<DeviceUserEntity> result = new Result<DeviceUserEntity>();
		DeviceUserEntity deviceUserEntityEntity = deviceUserEntityService.getById(deviceUserEntity.getId());
		if(deviceUserEntityEntity==null) {
			result.error500("未找到对应实体");
		}else {
			boolean ok = deviceUserEntityService.updateById(deviceUserEntity);
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
	@AutoLog(value = "商户关联设备-通过id删除")
	@ApiOperation(value="商户关联设备-通过id删除", notes="商户关联设备-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name="id",required=true) String id) {
		try {
			deviceUserEntityService.removeById(id);
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
	@AutoLog(value = "商户关联设备-批量删除")
	@ApiOperation(value="商户关联设备-批量删除", notes="商户关联设备-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<DeviceUserEntity> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		Result<DeviceUserEntity> result = new Result<DeviceUserEntity>();
		if(ids==null || "".equals(ids.trim())) {
			result.error500("参数不识别！");
		}else {
			this.deviceUserEntityService.removeByIds(Arrays.asList(ids.split(",")));
			result.success("删除成功!");
		}
		return result;
	}
	
	/**
	 * 通过id查询
	 * @param id
	 * @return
	 */
	@AutoLog(value = "商户关联设备-通过id查询")
	@ApiOperation(value="商户关联设备-通过id查询", notes="商户关联设备-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<DeviceUserEntity> queryById(@RequestParam(name="id",required=true) String id) {
		Result<DeviceUserEntity> result = new Result<DeviceUserEntity>();
		DeviceUserEntity deviceUserEntity = deviceUserEntityService.getById(id);
		if(deviceUserEntity==null) {
			result.error500("未找到对应实体");
		}else {
			result.setResult(deviceUserEntity);
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
      QueryWrapper<DeviceUserEntity> queryWrapper = null;
      try {
          String paramsStr = request.getParameter("paramsStr");
          if (oConvertUtils.isNotEmpty(paramsStr)) {
              String deString = URLDecoder.decode(paramsStr, "UTF-8");
              DeviceUserEntity deviceUserEntity = JSON.parseObject(deString, DeviceUserEntity.class);
              queryWrapper = QueryGenerator.initQueryWrapper(deviceUserEntity, request.getParameterMap());
          }
      } catch (UnsupportedEncodingException e) {
          e.printStackTrace();
      }

      //Step.2 AutoPoi 导出Excel
      ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
      List<DeviceUserEntity> pageList = deviceUserEntityService.list(queryWrapper);
      //导出文件名称
      mv.addObject(NormalExcelConstants.FILE_NAME, "商户关联设备列表");
      mv.addObject(NormalExcelConstants.CLASS, DeviceUserEntity.class);
      mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("商户关联设备列表数据", "导出人:Jeecg", "导出信息"));
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
              List<DeviceUserEntity> listDeviceUserEntitys = ExcelImportUtil.importExcel(file.getInputStream(), DeviceUserEntity.class, params);
              deviceUserEntityService.saveBatch(listDeviceUserEntitys);
              return Result.ok("文件导入成功！数据行数:" + listDeviceUserEntitys.size());
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
