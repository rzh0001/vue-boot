package org.jeecg.modules.v2.controller;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.hutool.core.util.StrUtil;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.v2.constant.BusinessActivStatusEnum;
import org.jeecg.modules.v2.dto.ChargeBusinessParam;
import org.jeecg.modules.v2.entity.PayBusiness;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;

import org.jeecg.modules.v2.entity.PayChannel;
import org.jeecg.modules.v2.entity.PayProduct;
import org.jeecg.modules.v2.service.impl.PayBusinessServiceImpl;
import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;
import com.alibaba.fastjson.JSON;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * @Description: 子账号信息
 * @Author: jeecg-boot
 * @Date: 2020-06-01
 * @Version: V1.0
 */
@Slf4j
@Api(tags = "子账号信息")
@RestController
@RequestMapping("/v2/payBusiness")
public class PayBusinessController {
    @Autowired
    private PayBusinessServiceImpl payBusinessService;

    @PostMapping("/deleteBusiness")
    public Result deleteBusiness(@RequestBody PayBusiness business) {
        payBusinessService.deleteBusiness(business);
        Result result = new Result();
        result.setResult("删除成功");
        return result;
    }

    @PostMapping("/activeBusinessStatus")
    public Result activeBusinessStatus(@RequestBody PayBusiness business) {
        business.setBusinessActiveStatus(BusinessActivStatusEnum.ACTIVE.getValue());
        payBusinessService.updateById(business);
        Result result = new Result();
        result.setResult("激活成功");
        return result;
    }
    @PostMapping("/unActiveBusinessStatus")
    public Result unActiveBusinessStatus(@RequestBody PayBusiness business) {
        business.setBusinessActiveStatus(BusinessActivStatusEnum.NOT_ACTIVE.getValue());
        payBusinessService.updateById(business);
        Result result = new Result();
        result.setResult("激活成功");
        return result;
    }

    @PostMapping("/updateBusiness")
    public Result updateBusiness(@RequestBody PayBusiness business) {
        payBusinessService.updateById(business);
        Result result = new Result();
        result.setResult("修改成功");
        return result;
    }

    @GetMapping("/getBusinessByAgentName")
    public Result getBusinessByAgentName(String userName) {
        List<PayBusiness> businesses = payBusinessService.getBusinessByName(userName);
        if (!CollectionUtils.isEmpty(businesses)) {
            businesses = businesses.stream().map(business -> business.setBalance(business.getBusinessRechargeAmount().subtract(business.getBusinessIncomeAmount()))).collect(
                Collectors.toList());
            for (int i = 0; i < businesses.size(); i++) {
                businesses.get(i).setKey(i + 1);
            }
        }
        businesses.stream().forEach(b->b.setBusinessApiKey(StrUtil.subWithLength(b.getBusinessApiKey(),0,10)));
        Result result = new Result();
        result.setResult(businesses);
        return result;
    }

    @GetMapping("/getBusiness")
    public Result getBusiness(String userName, String productCode, String channelCode) {
        List<PayBusiness> businesses = payBusinessService.getBusinessNotDelete(userName, channelCode, productCode);
        Result result = new Result();
        result.setResult(businesses);
        return result;
    }

    @GetMapping("/getBusinessRelatedProduct")
    public Result getBusinessRelatedProduct(String userName) {
        List<PayProduct> products = payBusinessService.getBusinessRelatedProduct(userName);
        Result result = new Result();
        result.setResult(products);
        return result;
    }

	@GetMapping("/getBusinessRelatedChannel")
    public Result getBusinessRelatedChannel(String userName,String productCode){
		List<PayChannel> channels = payBusinessService.getBusinessRelatedChannel(userName,productCode);
		Result result = new Result();
		result.setResult(channels);
		return result;
	}

	@PostMapping("/chargeAmount")
	public Result chargeAmount(@RequestBody ChargeBusinessParam param){
		Result result = new Result();
    	if(StringUtils.isEmpty(param.getAmount())){
			result.error500("金额不能为空");
			return result;
		}
		param.setChargeAmount(new BigDecimal(param.getAmount()));
		payBusinessService.updateBusinessAmount(param);
		result.success("充值成功,充值金额为："+param.getAmount());
		return result;
	}
    /**
     * 分页列表查询
     * 
     * @param payBusiness
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @AutoLog(value = "子账号信息-分页列表查询")
    @ApiOperation(value = "子账号信息-分页列表查询", notes = "子账号信息-分页列表查询")
    @GetMapping(value = "/list")
    public Result<IPage<PayBusiness>> queryPageList(PayBusiness payBusiness,
        @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
        @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize, HttpServletRequest req) {
        Result<IPage<PayBusiness>> result = new Result<IPage<PayBusiness>>();
        QueryWrapper<PayBusiness> queryWrapper = QueryGenerator.initQueryWrapper(payBusiness, req.getParameterMap());
        Page<PayBusiness> page = new Page<PayBusiness>(pageNo, pageSize);
        IPage<PayBusiness> pageList = payBusinessService.page(page, queryWrapper);
        result.setSuccess(true);
        result.setResult(pageList);
        return result;
    }

    /**
     * 添加
     * 
     * @param payBusiness
     * @return
     */
    @AutoLog(value = "子账号信息-添加")
    @ApiOperation(value = "子账号信息-添加", notes = "子账号信息-添加")
    @PostMapping(value = "/add")
    public Result<PayBusiness> add(@RequestBody PayBusiness payBusiness) {
        Result<PayBusiness> result = new Result<PayBusiness>();
        try {
            payBusinessService.save(payBusiness);
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
     * @param payBusiness
     * @return
     */
    @AutoLog(value = "子账号信息-编辑")
    @ApiOperation(value = "子账号信息-编辑", notes = "子账号信息-编辑")
    @PutMapping(value = "/edit")
    public Result<PayBusiness> edit(@RequestBody PayBusiness payBusiness) {
        Result<PayBusiness> result = new Result<PayBusiness>();
        PayBusiness payBusinessEntity = payBusinessService.getById(payBusiness.getId());
        if (payBusinessEntity == null) {
            result.error500("未找到对应实体");
        } else {
            boolean ok = payBusinessService.updateById(payBusiness);
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
    @AutoLog(value = "子账号信息-通过id删除")
    @ApiOperation(value = "子账号信息-通过id删除", notes = "子账号信息-通过id删除")
    @DeleteMapping(value = "/delete")
    public Result<?> delete(@RequestParam(name = "id", required = true) String id) {
        try {
            payBusinessService.removeById(id);
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
    @AutoLog(value = "子账号信息-批量删除")
    @ApiOperation(value = "子账号信息-批量删除", notes = "子账号信息-批量删除")
    @DeleteMapping(value = "/deleteBatch")
    public Result<PayBusiness> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        Result<PayBusiness> result = new Result<PayBusiness>();
        if (ids == null || "".equals(ids.trim())) {
            result.error500("参数不识别！");
        } else {
            this.payBusinessService.removeByIds(Arrays.asList(ids.split(",")));
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
    @AutoLog(value = "子账号信息-通过id查询")
    @ApiOperation(value = "子账号信息-通过id查询", notes = "子账号信息-通过id查询")
    @GetMapping(value = "/queryById")
    public Result<PayBusiness> queryById(@RequestParam(name = "id", required = true) String id) {
        Result<PayBusiness> result = new Result<PayBusiness>();
        PayBusiness payBusiness = payBusinessService.getById(id);
        if (payBusiness == null) {
            result.error500("未找到对应实体");
        } else {
            result.setResult(payBusiness);
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
        QueryWrapper<PayBusiness> queryWrapper = null;
        try {
            String paramsStr = request.getParameter("paramsStr");
            if (oConvertUtils.isNotEmpty(paramsStr)) {
                String deString = URLDecoder.decode(paramsStr, "UTF-8");
                PayBusiness payBusiness = JSON.parseObject(deString, PayBusiness.class);
                queryWrapper = QueryGenerator.initQueryWrapper(payBusiness, request.getParameterMap());
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        // Step.2 AutoPoi 导出Excel
        ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
        List<PayBusiness> pageList = payBusinessService.list(queryWrapper);
        // 导出文件名称
        mv.addObject(NormalExcelConstants.FILE_NAME, "子账号信息列表");
        mv.addObject(NormalExcelConstants.CLASS, PayBusiness.class);
        mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("子账号信息列表数据", "导出人:Jeecg", "导出信息"));
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
                List<PayBusiness> listPayBusinesss =
                    ExcelImportUtil.importExcel(file.getInputStream(), PayBusiness.class, params);
                payBusinessService.saveBatch(listPayBusinesss);
                return Result.ok("文件导入成功！数据行数:" + listPayBusinesss.size());
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
