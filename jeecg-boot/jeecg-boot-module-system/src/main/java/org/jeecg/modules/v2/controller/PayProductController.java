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
import org.jeecg.modules.v2.constant.DeleteFlagEnum;
import org.jeecg.modules.v2.entity.PayProduct;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;

import org.jeecg.modules.v2.service.impl.PayProductServiceImpl;
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
 * @Description: 产品
 * @Author: jeecg-boot
 * @Date:   2020-05-28
 * @Version: V1.0
 */
@Slf4j
@Api(tags="产品")
@RestController
@RequestMapping("/v2/payProduct")
public class PayProductController {
	@Autowired
	private PayProductServiceImpl payProductService;

	@GetMapping("/getAllProducts")
	public Result<List<PayProduct>> getAllProducts(){
		List<PayProduct> products=payProductService.getAllProducts();
		Result result = new Result();
		result.setResult(products);
		return result;
	}
	/**
	 * 分页列表查询
	 * @param payProduct
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "产品-分页列表查询")
	@ApiOperation(value="产品-分页列表查询", notes="产品-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<PayProduct>> queryPageList(PayProduct payProduct,
									  @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
									  @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
									  HttpServletRequest req) {
		Result<IPage<PayProduct>> result = new Result<IPage<PayProduct>>();
		QueryWrapper<PayProduct> queryWrapper = QueryGenerator.initQueryWrapper(payProduct, req.getParameterMap());
		queryWrapper.eq("del_flag", DeleteFlagEnum.NOT_DELETE.getValue());
		Page<PayProduct> page = new Page<PayProduct>(pageNo, pageSize);
		IPage<PayProduct> pageList = payProductService.page(page, queryWrapper);
		result.setSuccess(true);
		result.setResult(pageList);
		return result;
	}
	
	/**
	 *   添加
	 * @param payProduct
	 * @return
	 */
	@AutoLog(value = "产品-添加")
	@ApiOperation(value="产品-添加", notes="产品-添加")
	@PostMapping(value = "/add")
	public Result<PayProduct> add(@RequestBody PayProduct payProduct) {
		Result<PayProduct> result = new Result<PayProduct>();
		try {
			PayProduct product = payProductService.getProductByProductCode(payProduct.getProductCode());
			if(product != null){
				result.success("产品已存在！");
			}else {
				payProductService.save(payProduct);
				result.success("添加成功！");
			}
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			result.error500("操作失败");
		}
		return result;
	}
	
	/**
	 *  编辑
	 * @param payProduct
	 * @return
	 */
	@AutoLog(value = "产品-编辑")
	@ApiOperation(value="产品-编辑", notes="产品-编辑")
	@PutMapping(value = "/edit")
	public Result<PayProduct> edit(@RequestBody PayProduct payProduct) {
		Result<PayProduct> result = new Result<PayProduct>();
		PayProduct payProductEntity = payProductService.getById(payProduct.getId());
		if(payProductEntity==null) {
			result.error500("未找到对应实体");
		}else {
			payProduct.setProductCode(payProductEntity.getProductCode());
			boolean ok = payProductService.updateById(payProduct);
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
	@AutoLog(value = "产品-通过id删除")
	@ApiOperation(value="产品-通过id删除", notes="产品-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name="id",required=true) String id) {
		try {
			payProductService.deleteById(id);
		} catch (Exception e) {
			log.error("删除失败",e.getMessage());
			return Result.error("删除失败!");
		}
		return Result.ok("删除成功!");
	}
}
