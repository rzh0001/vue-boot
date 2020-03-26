package org.jeecg.modules.pay.controller;

import java.util.*;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.system.vo.DictModel;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.pay.entity.BusinessLabelValue;
import org.jeecg.modules.pay.entity.ChannelEntity;
import org.jeecg.modules.pay.service.IChannelEntityService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;

import org.jeecg.modules.productChannel.service.IProductChannelService;
import org.jeecg.modules.system.entity.SysUser;
import org.jeecg.modules.system.service.ISysDictService;
import org.jeecg.modules.system.service.ISysUserService;
import org.jeecg.modules.util.BaseConstant;
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
 * @Description: 通道设置
 * @Author: jeecg-boot
 * @Date:   2019-07-26
 * @Version: V1.0
 */
@Slf4j
@Api(tags="通道设置")
@RestController
@RequestMapping("/pay/channelEntity")
public class ChannelEntityController {
	@Autowired
	private IChannelEntityService channelEntityService;
	 @Autowired
	 private ISysUserService sysUserService;
	/**
	  * 分页列表查询
	 * @param channelEntity
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "通道设置-分页列表查询")
	@ApiOperation(value="通道设置-分页列表查询", notes="通道设置-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<ChannelEntity>> queryPageList(ChannelEntity channelEntity,
									  @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
									  @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
									  HttpServletRequest req) {
		Result<IPage<ChannelEntity>> result = new Result<IPage<ChannelEntity>>();
		QueryWrapper<ChannelEntity> queryWrapper = QueryGenerator.initQueryWrapper(channelEntity, req.getParameterMap());
		Page<ChannelEntity> page = new Page<ChannelEntity>(pageNo, pageSize);
		IPage<ChannelEntity> pageList = channelEntityService.page(page, queryWrapper);
		result.setSuccess(true);
		result.setResult(pageList);
		return result;
	}

	 /**
	  * 获取通道
	  * @return
	  */
	 @GetMapping(value = "/channel")
	public Result<List<ChannelEntity>> queryAllChannelCode(){
	 	 //获取系统用户
		 LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
		 SysUser sysUser = sysUserService.getUserByName(user.getUsername());
		 List<ChannelEntity> list = new ArrayList<>();
		 //管理员登录 展示全部的通道
		 if(StringUtils.isEmpty(sysUser.getMemberType())){
		 	list = channelEntityService.queryAllChannelCode();
		 }else if(BaseConstant.USER_AGENT.equals(sysUser.getMemberType())){
			 //代理登录，则展示代理的通道
			 list = channelEntityService.queryAgentChannelCodeByAgentName(sysUser.getUsername());
		 }else{
		 	 //商户或介绍人登录 ,展示其代理的通道
			 list = channelEntityService.queryAgentChannelCodeByAgentName(sysUser.getAgentUsername());
		 }

		 Result<List<ChannelEntity>> result = new Result<>();
		 result.setResult(list);
		return result;
	}
	 @Autowired
	 public ISysDictService dictService;
	 @GetMapping(value = "/getCallbackUrl")
	public Result<String> getCallbackUrl(){
	 	String innerCallBackUrl = null;
		 List<DictModel> innerUrl = dictService.queryDictItemsByCode(BaseConstant.INNER_CALL_BACK_URL);
		 for (DictModel k : innerUrl) {
			 if (BaseConstant.TEST_CALL_BACK_URL.equals(k.getText())) {
				 innerCallBackUrl = k.getValue();
				 break;
			 }
		 }
		 Result<String> result = new Result<>();
		 result.setResult(innerCallBackUrl);
		 return result;
	}
	@Autowired
	private IProductChannelService productChannelService;
	 @GetMapping(value = "/showChannel")
	 public Result<Map<String, Object>> getChannel(@RequestParam(name = "productCode") String productCode){
		 //获取系统用户
		 LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
		 SysUser sysUser = sysUserService.getUserByName(user.getUsername());
		 List<ChannelEntity> list = new ArrayList<>();
		 List<BusinessLabelValue> all = new ArrayList<>();
		 //产品已关联的通道
		 List<String> associated  = productChannelService.getChannelByProductCode(productCode);
		 //管理员登录 展示全部的通道
		 if(StringUtils.isEmpty(sysUser.getMemberType())){
			 list = channelEntityService.queryAllChannelCode();
		 }else if(BaseConstant.USER_AGENT.equals(sysUser.getMemberType())){
			 //代理登录，则展示代理的通道
			 list = channelEntityService.queryAgentChannelCodeByAgentName(sysUser.getUsername());
		 }else{
			 //商户或介绍人登录 ,展示其代理的通道
			 list = channelEntityService.queryAgentChannelCodeByAgentName(sysUser.getAgentUsername());
		 }
		 for(ChannelEntity channel:list){
			 BusinessLabelValue labelValue = new BusinessLabelValue();
			 labelValue.setLabel(channel.getChannelName());
			 labelValue.setValue(channel.getChannelCode());
			 all.add(labelValue);
		 }
		 Result<Map<String, Object>> result = new Result<Map<String, Object>>();
		 Map<String, Object> re = new HashMap<>();
		 re.put("all", all);
		 re.put("associated", org.apache.commons.lang.StringUtils.join(associated.toArray(), ","));
		 result.setResult(re);
		 return result;
	 }
	/**
	  *   添加
	 * @param channelEntity
	 * @return
	 */
	@AutoLog(value = "通道设置-添加")
	@ApiOperation(value="通道设置-添加", notes="通道设置-添加")
	@PostMapping(value = "/add")
	@RequiresPermissions("channel::add")
	public Result<ChannelEntity> add(@RequestBody ChannelEntity channelEntity) {
		Result<ChannelEntity> result = new Result<ChannelEntity>();
		try {
			channelEntityService.save(channelEntity);
			result.success("添加成功！");
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			result.error500("操作失败");
		}
		return result;
	}
	
	/**
	  *  编辑
	 * @param channelEntity
	 * @return
	 */
	@AutoLog(value = "通道设置-编辑")
	@ApiOperation(value="通道设置-编辑", notes="通道设置-编辑")
	@PutMapping(value = "/edit")
	@RequiresPermissions("channel::edit")
	public Result<ChannelEntity> edit(@RequestBody ChannelEntity channelEntity) {
		Result<ChannelEntity> result = new Result<ChannelEntity>();
		ChannelEntity channelEntityEntity = channelEntityService.getById(channelEntity.getId());
		if(channelEntityEntity==null) {
			result.error500("未找到对应实体");
		}else {
			boolean ok = channelEntityService.updateById(channelEntity);
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
	@AutoLog(value = "通道设置-通过id删除")
	@ApiOperation(value="通道设置-通过id删除", notes="通道设置-通过id删除")
	@DeleteMapping(value = "/delete")
	@RequiresPermissions("channel:delete")
	public Result<?> delete(@RequestParam(name="id",required=true) String id) {
		try {
			channelEntityService.removeById(id);
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
	@AutoLog(value = "通道设置-批量删除")
	@ApiOperation(value="通道设置-批量删除", notes="通道设置-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<ChannelEntity> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		Result<ChannelEntity> result = new Result<ChannelEntity>();
		if(ids==null || "".equals(ids.trim())) {
			result.error500("参数不识别！");
		}else {
			this.channelEntityService.removeByIds(Arrays.asList(ids.split(",")));
			result.success("删除成功!");
		}
		return result;
	}
	
	/**
	  * 通过id查询
	 * @param id
	 * @return
	 */
	@AutoLog(value = "通道设置-通过id查询")
	@ApiOperation(value="通道设置-通过id查询", notes="通道设置-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<ChannelEntity> queryById(@RequestParam(name="id",required=true) String id) {
		Result<ChannelEntity> result = new Result<ChannelEntity>();
		ChannelEntity channelEntity = channelEntityService.getById(id);
		if(channelEntity==null) {
			result.error500("未找到对应实体");
		}else {
			result.setResult(channelEntity);
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
      QueryWrapper<ChannelEntity> queryWrapper = null;
      try {
          String paramsStr = request.getParameter("paramsStr");
          if (oConvertUtils.isNotEmpty(paramsStr)) {
              String deString = URLDecoder.decode(paramsStr, "UTF-8");
              ChannelEntity channelEntity = JSON.parseObject(deString, ChannelEntity.class);
              queryWrapper = QueryGenerator.initQueryWrapper(channelEntity, request.getParameterMap());
          }
      } catch (UnsupportedEncodingException e) {
          e.printStackTrace();
      }

      //Step.2 AutoPoi 导出Excel
      ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
      List<ChannelEntity> pageList = channelEntityService.list(queryWrapper);
      //导出文件名称
      mv.addObject(NormalExcelConstants.FILE_NAME, "通道设置列表");
      mv.addObject(NormalExcelConstants.CLASS, ChannelEntity.class);
      mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("通道设置列表数据", "导出人:Jeecg", "导出信息"));
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
              List<ChannelEntity> listChannelEntitys = ExcelImportUtil.importExcel(file.getInputStream(), ChannelEntity.class, params);
              channelEntityService.saveBatch(listChannelEntitys);
              return Result.ok("文件导入成功！数据行数:" + listChannelEntitys.size());
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
