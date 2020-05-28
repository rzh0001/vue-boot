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
import org.jeecg.modules.v2.entity.PayProductChannel;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;

import org.jeecg.modules.v2.service.impl.PayProductChannelServiceImpl;
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
 * @Description: 产品通道
 * @Author: jeecg-boot
 * @Date: 2020-05-28
 * @Version: V1.0
 */
@Slf4j
@Api(tags = "产品通道")
@RestController
@RequestMapping("/v2/payProductChannel")
public class PayProductChannelController {
    @Autowired
    private PayProductChannelServiceImpl payProductChannelService;

    /**
     * 分页列表查询
     * 
     * @param payProductChannel
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @AutoLog(value = "产品通道-分页列表查询")
    @ApiOperation(value = "产品通道-分页列表查询", notes = "产品通道-分页列表查询")
    @GetMapping(value = "/list")
    public Result<IPage<PayProductChannel>> queryPageList(PayProductChannel payProductChannel,
        @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
        @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize, HttpServletRequest req) {
        Result<IPage<PayProductChannel>> result = new Result<IPage<PayProductChannel>>();
        QueryWrapper<PayProductChannel> queryWrapper =
            QueryGenerator.initQueryWrapper(payProductChannel, req.getParameterMap());
        Page<PayProductChannel> page = new Page<PayProductChannel>(pageNo, pageSize);
        IPage<PayProductChannel> pageList = payProductChannelService.page(page, queryWrapper);
        result.setSuccess(true);
        result.setResult(pageList);
        return result;
    }

    /**
     * 添加
     * 
     * @param payProductChannel
     * @return
     */
    @AutoLog(value = "产品通道-添加")
    @ApiOperation(value = "产品通道-添加", notes = "产品通道-添加")
    @PostMapping(value = "/add")
    public Result<PayProductChannel> add(@RequestBody PayProductChannel payProductChannel) {
        Result<PayProductChannel> result = new Result<PayProductChannel>();
        try {
            if (payProductChannelService.exist(payProductChannel.getProductCode(),
                payProductChannel.getChannelCode())) {
                result.error500("该产品已经关联过通道");
            } else {
                payProductChannelService.save(payProductChannel);
                result.success("添加成功！");
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result.error500("操作失败");
        }
        return result;
    }

    @Autowired
    private PayUserChannelServiceImpl userChannelService;
    /**
     * 
     * @param productCode
     * @param channelCode
     * @return
     */
    @DeleteMapping(value = "/delete")
    public Result<?> delete(@RequestParam(name = "productCode", required = true) String productCode,
        @RequestParam(name = "channelCode", required = true) String channelCode) {
        try {
			/**
			 * 1、删除产品关联的通道信息
			 * 2、取消商户关联该产品下的通道
			 */
			payProductChannelService.delete(productCode,channelCode);
			userChannelService.delete(productCode,channelCode);
        } catch (Exception e) {
            log.error("删除失败", e.getMessage());
            return Result.error("删除失败!");
        }
        return Result.ok("删除成功!");
    }

}
