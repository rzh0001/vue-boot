package org.jeecg.modules.pay.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.pay.entity.UserRateEntity;
import org.jeecg.modules.pay.service.IUserRateEntityService;

import java.util.Date;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;

import org.jeecg.modules.system.entity.SysUser;
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
 * @Description: 用户在指定通道下的费率
 * @Author: jeecg-boot
 * @Date: 2019-07-26
 * @Version: V1.0
 */
@Slf4j
@Api(tags = "用户在指定通道下的费率")
@RestController
@RequestMapping("/pay/userRateEntity")
public class UserRateEntityController {
    @Autowired
    private IUserRateEntityService userRateEntityService;
    @Autowired
    private ISysUserService userService;

    /**
     * 分页列表查询
     *
     * @param userRateEntity
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @AutoLog(value = "用户在指定通道下的费率-分页列表查询")
    @ApiOperation(value = "用户在指定通道下的费率-分页列表查询", notes = "用户在指定通道下的费率-分页列表查询")
    @GetMapping(value = "/list")
    public Result<IPage<UserRateEntity>> queryPageList(UserRateEntity userRateEntity,
                                                       @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                       @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                       HttpServletRequest req) {
        Result<IPage<UserRateEntity>> result = new Result<IPage<UserRateEntity>>();
        QueryWrapper<UserRateEntity> queryWrapper = QueryGenerator.initQueryWrapper(userRateEntity,
				req.getParameterMap());
        Page<UserRateEntity> page = new Page<UserRateEntity>(pageNo, pageSize);
        IPage<UserRateEntity> pageList = userRateEntityService.page(page, queryWrapper);
        result.setSuccess(true);
        result.setResult(pageList);
        return result;
    }

    /**
     * 添加
     *
     * @param userRateEntity
     * @return
     */
    @AutoLog(value = "用户在指定通道下的费率-添加")
    @ApiOperation(value = "用户在指定通道下的费率-添加", notes = "用户在指定通道下的费率-添加")
    @PostMapping(value = "/add")
    public Result<UserRateEntity> add(@RequestBody UserRateEntity userRateEntity) {
        Result<UserRateEntity> result = new Result<UserRateEntity>();
        try {
            String userName = userRateEntity.getUserName();
            //判断用户是否存在
            SysUser user = userService.getUserByName(userName);
            if (user == null) {
                result.error500("用户不存在");
                return result;
            }
            //2、如果是普通商户，则必须填写高级代理
            if (BaseConstant.USER_MERCHANTS.equals(user.getMemberType())) {
                if (StringUtils.isBlank(userRateEntity.getAgentId())) {
                    result.error500("普通商户的高级代理必填");
                    return result;
                }
                SysUser agent = userService.getUserByName(userRateEntity.getAgentId());
                if(agent == null){
                    result.error500("高级代理不存在");
                    return result;
                }
                //判断该高级代理下的该用户，是否已经添加过费率
                String rate = userRateEntityService.getUserRateByUserNameAndAngetCode(userName,userRateEntity.getAgentId(),userRateEntity.getChannelCode());
                if(StringUtils.isNotBlank(rate)){
                    result.error500("该用户已经添加过费率");
                    return result;
                }
            }
            //介绍人
            if(BaseConstant.USER_REFERENCES.equals(user.getMemberType())){
                if (StringUtils.isBlank(userRateEntity.getAgentId())) {
                    result.error500("介绍人的高级代理必填");
                    return result;
                }
                if (StringUtils.isBlank(userRateEntity.getBeIntroducerName())) {
                    result.error500("被介绍人姓名必填");
                    return result;
                }
                SysUser agent = userService.getUserByName(userRateEntity.getAgentId());
                if(agent == null){
                    result.error500("高级代理不存在");
                    return result;
                }
                SysUser beIntroducer = userService.getUserByName(userRateEntity.getBeIntroducerName());
                if(beIntroducer == null){
                    result.error500("被介绍人不存在");
                    return result;
                }
            }
            userRateEntityService.save(userRateEntity);
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
     * @param userRateEntity
     * @return
     */
    @AutoLog(value = "用户在指定通道下的费率-编辑")
    @ApiOperation(value = "用户在指定通道下的费率-编辑", notes = "用户在指定通道下的费率-编辑")
    @PutMapping(value = "/edit")
    public Result<UserRateEntity> edit(@RequestBody UserRateEntity userRateEntity) {
        Result<UserRateEntity> result = new Result<UserRateEntity>();
        UserRateEntity userRateEntityEntity = userRateEntityService.getById(userRateEntity.getId());
        if (userRateEntityEntity == null) {
            result.error500("未找到对应实体");
        } else {
            boolean ok = userRateEntityService.updateById(userRateEntity);
            //TODO 返回false说明什么？
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
    @AutoLog(value = "用户在指定通道下的费率-通过id删除")
    @ApiOperation(value = "用户在指定通道下的费率-通过id删除", notes = "用户在指定通道下的费率-通过id删除")
    @DeleteMapping(value = "/delete")
    public Result<?> delete(@RequestParam(name = "id", required = true) String id) {
        try {
            userRateEntityService.removeById(id);
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
    @AutoLog(value = "用户在指定通道下的费率-批量删除")
    @ApiOperation(value = "用户在指定通道下的费率-批量删除", notes = "用户在指定通道下的费率-批量删除")
    @DeleteMapping(value = "/deleteBatch")
    public Result<UserRateEntity> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        Result<UserRateEntity> result = new Result<UserRateEntity>();
        if (ids == null || "".equals(ids.trim())) {
            result.error500("参数不识别！");
        } else {
            this.userRateEntityService.removeByIds(Arrays.asList(ids.split(",")));
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
    @AutoLog(value = "用户在指定通道下的费率-通过id查询")
    @ApiOperation(value = "用户在指定通道下的费率-通过id查询", notes = "用户在指定通道下的费率-通过id查询")
    @GetMapping(value = "/queryById")
    public Result<UserRateEntity> queryById(@RequestParam(name = "id", required = true) String id) {
        Result<UserRateEntity> result = new Result<UserRateEntity>();
        UserRateEntity userRateEntity = userRateEntityService.getById(id);
        if (userRateEntity == null) {
            result.error500("未找到对应实体");
        } else {
            result.setResult(userRateEntity);
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
        QueryWrapper<UserRateEntity> queryWrapper = null;
        try {
            String paramsStr = request.getParameter("paramsStr");
            if (oConvertUtils.isNotEmpty(paramsStr)) {
                String deString = URLDecoder.decode(paramsStr, "UTF-8");
                UserRateEntity userRateEntity = JSON.parseObject(deString, UserRateEntity.class);
                queryWrapper = QueryGenerator.initQueryWrapper(userRateEntity, request.getParameterMap());
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        //Step.2 AutoPoi 导出Excel
        ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
        List<UserRateEntity> pageList = userRateEntityService.list(queryWrapper);
        //导出文件名称
        mv.addObject(NormalExcelConstants.FILE_NAME, "用户在指定通道下的费率列表");
        mv.addObject(NormalExcelConstants.CLASS, UserRateEntity.class);
        mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("用户在指定通道下的费率列表数据", "导出人:Jeecg", "导出信息"));
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
                List<UserRateEntity> listUserRateEntitys = ExcelImportUtil.importExcel(file.getInputStream(), UserRateEntity.class, params);
                userRateEntityService.saveBatch(listUserRateEntitys);
                return Result.ok("文件导入成功！数据行数:" + listUserRateEntitys.size());
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
