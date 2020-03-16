package org.jeecg.modules.productChannel.controller;

import java.util.*;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.pay.entity.BusinessLabelValue;
import org.jeecg.modules.pay.entity.ChannelEntity;
import org.jeecg.modules.pay.service.IChannelEntityService;
import org.jeecg.modules.pay.service.IUserChannelEntityService;
import org.jeecg.modules.productChannel.entity.ProductChannel;
import org.jeecg.modules.productChannel.service.IProductChannelService;
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
 * @Description: 产品关联通道
 * @Author: jeecg-boot
 * @Date:   2020-03-05
 * @Version: V1.0
 */
@Slf4j
@Api(tags="产品关联通道")
@RestController
@RequestMapping("/productChannel/productChannel")
public class ProductChannelController {
	@Autowired
	private IProductChannelService productChannelService;
	@Autowired
	private IUserChannelEntityService userChannelEntityService;
	/**
	 * 分页列表查询
	 * @param productChannel
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "产品关联通道-分页列表查询")
	@ApiOperation(value="产品关联通道-分页列表查询", notes="产品关联通道-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<ProductChannel>> queryPageList(ProductChannel productChannel,
									  @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
									  @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
									  HttpServletRequest req) {
		Result<IPage<ProductChannel>> result = new Result<IPage<ProductChannel>>();
		QueryWrapper<ProductChannel> queryWrapper = QueryGenerator.initQueryWrapper(productChannel, req.getParameterMap());
		Page<ProductChannel> page = new Page<ProductChannel>(pageNo, pageSize);
		IPage<ProductChannel> pageList = productChannelService.page(page, queryWrapper);
		result.setSuccess(true);
		result.setResult(pageList);
		return result;
	}
	 @GetMapping(value = "/getUserProductChannel")
	 public Result<Map<String, Object>> getChannel(@RequestParam(name = "productCode") String productCode,
		 @RequestParam(name="userName")String userName){
		 //产品已关联的通道
		 List<ProductChannel> productChannels  = productChannelService.getChannelProduct(productCode);
		 List<BusinessLabelValue> all = new ArrayList<>();
		 for(ProductChannel channel:productChannels){
			 BusinessLabelValue labelValue = new BusinessLabelValue();
			 labelValue.setLabel(channel.getChannelName());
			 labelValue.setValue(channel.getChannelCode());
			 all.add(labelValue);
		 }
		 //获取用户已关联的通道code
		 List<String> userChannel = userChannelEntityService.getChannelCodeByUserName(userName);
		 Result<Map<String, Object>> result = new Result<Map<String, Object>>();
		 Map<String, Object> re = new HashMap<>();
		 re.put("all", all);
		 re.put("associated", org.apache.commons.lang.StringUtils.join(userChannel.toArray(), ","));
		 result.setResult(re);
		 return result;
	 }
	/**
	 *   添加
	 * @param productChannel
	 * @return
	 */
	@AutoLog(value = "产品关联通道-添加")
	@ApiOperation(value="产品关联通道-添加", notes="产品关联通道-添加")
	@PostMapping(value = "/add")
	public Result<ProductChannel> add(@RequestBody ProductChannel productChannel) {
		Result<ProductChannel> result = new Result<ProductChannel>();
		try {
			productChannelService.save(productChannel);
			result.success("添加成功！");
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			result.error500("操作失败");
		}
		return result;
	}
	@Autowired
	private IChannelEntityService channelEntityService;
	 @GetMapping(value = "/saveProductAndChannels")
	public Result<String> addProductAndChannel(@RequestParam(name = "productCode")String productCode,@RequestParam(name = "channelCodes")String channels){
		 List<String> channelCodes = new ArrayList<>();
		 Result<String> result = new Result<String>();
		if(StringUtils.isNotBlank(channels)){
			channelCodes = Arrays.asList(channels.split(","));
		}
		//根据通道code查询通道
		 List<ChannelEntity> channelEntities = channelEntityService.queryChannelByCodes(channelCodes);
		productChannelService.remove(productCode);
		productChannelService.batchSave(channelEntities,productCode);
		return result.success("success");

	}
	/**
	 *  编辑
	 * @param productChannel
	 * @return
	 */
	@AutoLog(value = "产品关联通道-编辑")
	@ApiOperation(value="产品关联通道-编辑", notes="产品关联通道-编辑")
	@PutMapping(value = "/edit")
	public Result<ProductChannel> edit(@RequestBody ProductChannel productChannel) {
		Result<ProductChannel> result = new Result<ProductChannel>();
		ProductChannel productChannelEntity = productChannelService.getById(productChannel.getId());
		if(productChannelEntity==null) {
			result.error500("未找到对应实体");
		}else {
			boolean ok = productChannelService.updateById(productChannel);
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
	@AutoLog(value = "产品关联通道-通过id删除")
	@ApiOperation(value="产品关联通道-通过id删除", notes="产品关联通道-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name="id",required=true) String id) {
		try {
			productChannelService.removeById(id);
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
	@AutoLog(value = "产品关联通道-批量删除")
	@ApiOperation(value="产品关联通道-批量删除", notes="产品关联通道-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<ProductChannel> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		Result<ProductChannel> result = new Result<ProductChannel>();
		if(ids==null || "".equals(ids.trim())) {
			result.error500("参数不识别！");
		}else {
			this.productChannelService.removeByIds(Arrays.asList(ids.split(",")));
			result.success("删除成功!");
		}
		return result;
	}
	
	/**
	 * 通过id查询
	 * @param id
	 * @return
	 */
	@AutoLog(value = "产品关联通道-通过id查询")
	@ApiOperation(value="产品关联通道-通过id查询", notes="产品关联通道-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<ProductChannel> queryById(@RequestParam(name="id",required=true) String id) {
		Result<ProductChannel> result = new Result<ProductChannel>();
		ProductChannel productChannel = productChannelService.getById(id);
		if(productChannel==null) {
			result.error500("未找到对应实体");
		}else {
			result.setResult(productChannel);
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
      QueryWrapper<ProductChannel> queryWrapper = null;
      try {
          String paramsStr = request.getParameter("paramsStr");
          if (oConvertUtils.isNotEmpty(paramsStr)) {
              String deString = URLDecoder.decode(paramsStr, "UTF-8");
              ProductChannel productChannel = JSON.parseObject(deString, ProductChannel.class);
              queryWrapper = QueryGenerator.initQueryWrapper(productChannel, request.getParameterMap());
          }
      } catch (UnsupportedEncodingException e) {
          e.printStackTrace();
      }

      //Step.2 AutoPoi 导出Excel
      ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
      List<ProductChannel> pageList = productChannelService.list(queryWrapper);
      //导出文件名称
      mv.addObject(NormalExcelConstants.FILE_NAME, "产品关联通道列表");
      mv.addObject(NormalExcelConstants.CLASS, ProductChannel.class);
      mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("产品关联通道列表数据", "导出人:Jeecg", "导出信息"));
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
              List<ProductChannel> listProductChannels = ExcelImportUtil.importExcel(file.getInputStream(), ProductChannel.class, params);
              productChannelService.saveBatch(listProductChannels);
              return Result.ok("文件导入成功！数据行数:" + listProductChannels.size());
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
