package org.jeecg.modules.v2.controller;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
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
import org.jeecg.modules.system.entity.SysUser;
import org.jeecg.modules.system.service.ISysUserService;
import org.jeecg.modules.v2.entity.PayProduct;
import org.jeecg.modules.v2.entity.PayUserProduct;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;

import org.jeecg.modules.v2.service.impl.PayProductServiceImpl;
import org.jeecg.modules.v2.service.impl.PayUserProductServiceImpl;
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
 * @Description: 用户关联产品
 * @Author: jeecg-boot
 * @Date: 2020-05-28
 * @Version: V1.0
 */
@Slf4j
@Api(tags = "用户关联产品")
@RestController
@RequestMapping("/v2/payUserProduct")
public class PayUserProductController {
    @Autowired
    private PayUserProductServiceImpl payUserProductService;

    @Autowired
    private ISysUserService userService;
	@Autowired
    private PayProductServiceImpl productService;
    /**
     * 获取当前登录用户关联的产品信息
     * 
     * @return
     */
    @GetMapping("/findCurrentLoginAccountRelatedProduct")
    public Result<List<PayProduct>> findCurrentLoginAccountRelatedProduct() {
        LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        SysUser sysUser = userService.getUserByName(loginUser.getUsername());
		Result result = new Result();
        if(sysUser.getUsername().equals("admin")){
        	//管理员可以看到全部产品信息
        	List<PayProduct> products = productService.getAllProducts();
			result.setResult(products);
		}else{
        	List<String> productCodes =payUserProductService.findProductCodesByUserName(sysUser.getUsername());
			if(!CollectionUtils.isEmpty(productCodes)){
				List<PayProduct> products = productService.getProductsByProductCodes(productCodes);
				result.setResult(products);
			}
		}
		return result;
    }

    /**
     * 分页列表查询
     * 
     * @param payUserProduct
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @AutoLog(value = "用户关联产品-分页列表查询")
    @ApiOperation(value = "用户关联产品-分页列表查询", notes = "用户关联产品-分页列表查询")
    @GetMapping(value = "/list")
    public Result<IPage<PayUserProduct>> queryPageList(PayUserProduct payUserProduct,
        @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
        @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize, HttpServletRequest req) {
        Result<IPage<PayUserProduct>> result = new Result<IPage<PayUserProduct>>();
        QueryWrapper<PayUserProduct> queryWrapper =
            QueryGenerator.initQueryWrapper(payUserProduct, req.getParameterMap());
        Page<PayUserProduct> page = new Page<PayUserProduct>(pageNo, pageSize);
        IPage<PayUserProduct> pageList = payUserProductService.page(page, queryWrapper);
        result.setSuccess(true);
        result.setResult(pageList);
        return result;
    }

    /**
     * 添加
     * 
     * @param payUserProduct
     * @return
     */
    @AutoLog(value = "用户关联产品-添加")
    @ApiOperation(value = "用户关联产品-添加", notes = "用户关联产品-添加")
    @PostMapping(value = "/add")
    public Result<PayUserProduct> add(@RequestBody PayUserProduct payUserProduct) {
        Result<PayUserProduct> result = new Result<PayUserProduct>();
        try {
            payUserProductService.save(payUserProduct);
            result.success("添加成功！");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result.error500("操作失败");
        }
        return result;
    }

    /**
     * 编辑
     * 
     * @param payUserProduct
     * @return
     */
    @AutoLog(value = "用户关联产品-编辑")
    @ApiOperation(value = "用户关联产品-编辑", notes = "用户关联产品-编辑")
    @PutMapping(value = "/edit")
    public Result<PayUserProduct> edit(@RequestBody PayUserProduct payUserProduct) {
        Result<PayUserProduct> result = new Result<PayUserProduct>();
        PayUserProduct payUserProductEntity = payUserProductService.getById(payUserProduct.getId());
        if (payUserProductEntity == null) {
            result.error500("未找到对应实体");
        } else {
            boolean ok = payUserProductService.updateById(payUserProduct);
            // TODO 返回false说明什么？
            if (ok) {
                result.success("修改成功!");
            }
        }

        return result;
    }

    /**
     * 通过id删除
     * 
     * @param id
     * @return
     */
    @AutoLog(value = "用户关联产品-通过id删除")
    @ApiOperation(value = "用户关联产品-通过id删除", notes = "用户关联产品-通过id删除")
    @DeleteMapping(value = "/delete")
    public Result<?> delete(@RequestParam(name = "id", required = true) String id) {
        try {
            payUserProductService.removeById(id);
        } catch (Exception e) {
            log.error("删除失败", e.getMessage());
            return Result.error("删除失败!");
        }
        return Result.ok("删除成功!");
    }

    /**
     * 批量删除
     * 
     * @param ids
     * @return
     */
    @AutoLog(value = "用户关联产品-批量删除")
    @ApiOperation(value = "用户关联产品-批量删除", notes = "用户关联产品-批量删除")
    @DeleteMapping(value = "/deleteBatch")
    public Result<PayUserProduct> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        Result<PayUserProduct> result = new Result<PayUserProduct>();
        if (ids == null || "".equals(ids.trim())) {
            result.error500("参数不识别！");
        } else {
            this.payUserProductService.removeByIds(Arrays.asList(ids.split(",")));
            result.success("删除成功!");
        }
        return result;
    }

    /**
     * 通过id查询
     * 
     * @param id
     * @return
     */
    @AutoLog(value = "用户关联产品-通过id查询")
    @ApiOperation(value = "用户关联产品-通过id查询", notes = "用户关联产品-通过id查询")
    @GetMapping(value = "/queryById")
    public Result<PayUserProduct> queryById(@RequestParam(name = "id", required = true) String id) {
        Result<PayUserProduct> result = new Result<PayUserProduct>();
        PayUserProduct payUserProduct = payUserProductService.getById(id);
        if (payUserProduct == null) {
            result.error500("未找到对应实体");
        } else {
            result.setResult(payUserProduct);
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
        QueryWrapper<PayUserProduct> queryWrapper = null;
        try {
            String paramsStr = request.getParameter("paramsStr");
            if (oConvertUtils.isNotEmpty(paramsStr)) {
                String deString = URLDecoder.decode(paramsStr, "UTF-8");
                PayUserProduct payUserProduct = JSON.parseObject(deString, PayUserProduct.class);
                queryWrapper = QueryGenerator.initQueryWrapper(payUserProduct, request.getParameterMap());
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        // Step.2 AutoPoi 导出Excel
        ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
        List<PayUserProduct> pageList = payUserProductService.list(queryWrapper);
        // 导出文件名称
        mv.addObject(NormalExcelConstants.FILE_NAME, "用户关联产品列表");
        mv.addObject(NormalExcelConstants.CLASS, PayUserProduct.class);
        mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("用户关联产品列表数据", "导出人:Jeecg", "导出信息"));
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
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest)request;
        Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
        for (Map.Entry<String, MultipartFile> entity : fileMap.entrySet()) {
            MultipartFile file = entity.getValue();// 获取上传文件对象
            ImportParams params = new ImportParams();
            params.setTitleRows(2);
            params.setHeadRows(1);
            params.setNeedSave(true);
            try {
                List<PayUserProduct> listPayUserProducts =
                    ExcelImportUtil.importExcel(file.getInputStream(), PayUserProduct.class, params);
                payUserProductService.saveBatch(listPayUserProducts);
                return Result.ok("文件导入成功！数据行数:" + listPayUserProducts.size());
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                return Result.error("文件导入失败:" + e.getMessage());
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
