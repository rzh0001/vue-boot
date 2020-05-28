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
import org.jeecg.common.aspect.annotation.PermissionData;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.v2.constant.DeleteFlagEnum;
import org.jeecg.modules.v2.entity.PayChannel;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;

import org.jeecg.modules.v2.service.impl.PayChannelServiceImpl;
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
 * @Description: channel
 * @Author: jeecg-boot
 * @Date: 2020-05-28
 * @Version: V1.0
 */
@Slf4j
@Api(tags = "channel")
@RestController
@RequestMapping("/v2/payChannel")
public class PayChannelController {
    @Autowired
    private PayChannelServiceImpl payChannelService;

    @GetMapping("/getAllChannels")
    public Result<List<PayChannel>> getAllChannels() {
        List<PayChannel> list = payChannelService.getAllChannel();
        Result result = new Result();
        result.setResult(list);
        return result;
    }

    /**
     * 分页列表查询
     * 
     * @param payChannel
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @AutoLog(value = "channel-分页列表查询")
    @ApiOperation(value = "channel-分页列表查询", notes = "channel-分页列表查询")
    @GetMapping(value = "/list")
    public Result<IPage<PayChannel>> queryPageList(PayChannel payChannel,
        @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
        @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize, HttpServletRequest req) {
        Result<IPage<PayChannel>> result = new Result<IPage<PayChannel>>();
        QueryWrapper<PayChannel> queryWrapper = QueryGenerator.initQueryWrapper(payChannel, req.getParameterMap());
		queryWrapper.eq("del_flag", DeleteFlagEnum.NOT_DELETE.getValue());
        Page<PayChannel> page = new Page<PayChannel>(pageNo, pageSize);
        IPage<PayChannel> pageList = payChannelService.page(page, queryWrapper);
        result.setSuccess(true);
        result.setResult(pageList);
        return result;
    }

    /**
     * 添加
     * 
     * @param payChannel
     * @return
     */
    @AutoLog(value = "channel-添加")
    @ApiOperation(value = "channel-添加", notes = "channel-添加")
    @PostMapping(value = "/add")
    public Result<PayChannel> add(@RequestBody PayChannel payChannel) {
        Result<PayChannel> result = new Result<PayChannel>();
        try {
            PayChannel channel = payChannelService.getChannelByChannelCode(payChannel.getChannelCode());
            if (channel != null) {
				result.error500("该通道代码已经存在");
            }else {
				payChannelService.save(payChannel);
				result.success("添加成功！");
			}
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result.error500("操作失败");
        }
        return result;
    }

    /**
     * 编辑
     * 
     * @param payChannel
     * @return
     */
    @AutoLog(value = "channel-编辑")
    @ApiOperation(value = "channel-编辑", notes = "channel-编辑")
    @PutMapping(value = "/edit")
    public Result<PayChannel> edit(@RequestBody PayChannel payChannel) {
        Result<PayChannel> result = new Result<PayChannel>();
        PayChannel payChannelEntity = payChannelService.getById(payChannel.getId());
        if (payChannelEntity == null) {
            result.error500("未找到对应实体");
        } else {
            // channelCode不能修改
            payChannel.setChannelCode(payChannelEntity.getChannelCode());
            boolean ok = payChannelService.updateById(payChannel);
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
    @AutoLog(value = "channel-通过id删除")
    @ApiOperation(value = "channel-通过id删除", notes = "channel-通过id删除")
    @DeleteMapping(value = "/delete")
    public Result<?> delete(@RequestParam(name = "id", required = true) String id) {
        try {
            payChannelService.deleteById(id);
        } catch (Exception e) {
            log.error("删除失败", e.getMessage());
            return Result.error("删除失败!");
        }
        return Result.ok("删除成功!");
    }

}
