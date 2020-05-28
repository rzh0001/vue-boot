package org.jeecg.modules.v2.controller;

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
import org.jeecg.modules.v2.entity.PayUserChannel;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;

import org.jeecg.modules.v2.service.impl.PayUserChannelServiceImpl;
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
 * @Date:   2020-05-28
 * @Version: V1.0
 */
@Slf4j
@Api(tags="用户关联通道")
@RestController
@RequestMapping("/v2/payUserChannel")
public class PayUserChannelController {
	@Autowired
	private PayUserChannelServiceImpl payUserChannelService;
	
	/**
	 * 分页列表查询
	 * @param payUserChannel
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "用户关联通道-分页列表查询")
	@ApiOperation(value="用户关联通道-分页列表查询", notes="用户关联通道-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<PayUserChannel>> queryPageList(PayUserChannel payUserChannel,
									  @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
									  @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
									  HttpServletRequest req) {
		Result<IPage<PayUserChannel>> result = new Result<IPage<PayUserChannel>>();
		QueryWrapper<PayUserChannel> queryWrapper = QueryGenerator.initQueryWrapper(payUserChannel, req.getParameterMap());
		Page<PayUserChannel> page = new Page<PayUserChannel>(pageNo, pageSize);
		IPage<PayUserChannel> pageList = payUserChannelService.page(page, queryWrapper);
		result.setSuccess(true);
		result.setResult(pageList);
		return result;
	}
	
	/**
	 *   添加
	 * @param payUserChannel
	 * @return
	 */
	@AutoLog(value = "用户关联通道-添加")
	@ApiOperation(value="用户关联通道-添加", notes="用户关联通道-添加")
	@PostMapping(value = "/add")
	public Result<PayUserChannel> add(@RequestBody PayUserChannel payUserChannel) {
		Result<PayUserChannel> result = new Result<PayUserChannel>();
		try {
			payUserChannelService.save(payUserChannel);
			result.success("添加成功！");
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			result.error500("操作失败");
		}
		return result;
	}
	
	/**
	 *  编辑
	 * @param payUserChannel
	 * @return
	 */
	@AutoLog(value = "用户关联通道-编辑")
	@ApiOperation(value="用户关联通道-编辑", notes="用户关联通道-编辑")
	@PutMapping(value = "/edit")
	public Result<PayUserChannel> edit(@RequestBody PayUserChannel payUserChannel) {
		Result<PayUserChannel> result = new Result<PayUserChannel>();
		PayUserChannel payUserChannelEntity = payUserChannelService.getById(payUserChannel.getId());
		if(payUserChannelEntity==null) {
			result.error500("未找到对应实体");
		}else {
			boolean ok = payUserChannelService.updateById(payUserChannel);
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
			payUserChannelService.removeById(id);
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
	public Result<PayUserChannel> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		Result<PayUserChannel> result = new Result<PayUserChannel>();
		if(ids==null || "".equals(ids.trim())) {
			result.error500("参数不识别！");
		}else {
			this.payUserChannelService.removeByIds(Arrays.asList(ids.split(",")));
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
	public Result<PayUserChannel> queryById(@RequestParam(name="id",required=true) String id) {
		Result<PayUserChannel> result = new Result<PayUserChannel>();
		PayUserChannel payUserChannel = payUserChannelService.getById(id);
		if(payUserChannel==null) {
			result.error500("未找到对应实体");
		}else {
			result.setResult(payUserChannel);
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
      QueryWrapper<PayUserChannel> queryWrapper = null;
      try {
          String paramsStr = request.getParameter("paramsStr");
          if (oConvertUtils.isNotEmpty(paramsStr)) {
              String deString = URLDecoder.decode(paramsStr, "UTF-8");
              PayUserChannel payUserChannel = JSON.parseObject(deString, PayUserChannel.class);
              queryWrapper = QueryGenerator.initQueryWrapper(payUserChannel, request.getParameterMap());
          }
      } catch (UnsupportedEncodingException e) {
          e.printStackTrace();
      }

      //Step.2 AutoPoi 导出Excel
      ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
      List<PayUserChannel> pageList = payUserChannelService.list(queryWrapper);
      //导出文件名称
      mv.addObject(NormalExcelConstants.FILE_NAME, "用户关联通道列表");
      mv.addObject(NormalExcelConstants.CLASS, PayUserChannel.class);
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
              List<PayUserChannel> listPayUserChannels = ExcelImportUtil.importExcel(file.getInputStream(), PayUserChannel.class, params);
              payUserChannelService.saveBatch(listPayUserChannels);
              return Result.ok("文件导入成功！数据行数:" + listPayUserChannels.size());
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
