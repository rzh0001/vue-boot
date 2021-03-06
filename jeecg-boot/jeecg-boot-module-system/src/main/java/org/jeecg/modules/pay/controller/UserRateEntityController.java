package org.jeecg.modules.pay.controller;

import java.util.*;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.util.StringUtil;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.pay.entity.UserBusinessEntity;
import org.jeecg.modules.pay.entity.UserRateEntity;
import org.jeecg.modules.pay.service.IUserRateEntityService;

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
        //获取系统用户
        LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        SysUser sysUser = userService.getUserByName(loginUser.getUsername());

        Result<IPage<UserRateEntity>> result = new Result<IPage<UserRateEntity>>();
        QueryWrapper<UserRateEntity> queryWrapper = QueryGenerator.initQueryWrapper(userRateEntity,
                req.getParameterMap());
        Page<UserRateEntity> page = new Page<UserRateEntity>(pageNo, pageSize);
        IPage<UserRateEntity> pageList = userRateEntityService.page(page, queryWrapper);
        List<UserRateEntity> db = pageList.getRecords();
        List<UserRateEntity> newList = new ArrayList<>();
        if (BaseConstant.USER_AGENT.equals(sysUser.getMemberType())){
            //如果是代理登录，则可以看到该代理下的商户和介绍人
            List<SysUser> users = userService.getUserAndReferByAgent(sysUser.getUsername());
            for(UserRateEntity ur:db){
                for(SysUser u:users){
                    if(ur.getUserName().equals(u.getUsername())){
                        newList.add(ur);
                        break;
                    }
                }
            }
            pageList.setRecords(newList);
        }else if(BaseConstant.USER_MERCHANTS.equals(sysUser.getMemberType()) || BaseConstant.USER_REFERENCES.equals(sysUser.getMemberType())){
            for(UserRateEntity ur:db){
                if(ur.getUserName().equals(sysUser.getUsername())){
                    newList.add(ur);
                }
            }
            pageList.setRecords(newList);
        }
        result.setSuccess(true);
        result.setResult(pageList);
        return result;
    }
    @GetMapping(value = "/queryUserRate")
    @RequiresPermissions("rate::detail")
    public Result<List<UserRateEntity>> queryUserRate(@RequestParam(name="username") String username){
        Result<List<UserRateEntity>> result = new Result<List<UserRateEntity>>();
        result.setResult(userRateEntityService.queryUserRate(username));
        return result;
    }
    @GetMapping(value = "/getBeIntroducerName")
    public Result<List<String>> getBeIntroducerName(@RequestParam(name="username") String username){
        Result<List<String>> result = new Result<List<String>>();
        result.setResult(userRateEntityService.getBeIntroducerName(username));
        return result;
    }
    @PostMapping(value = "/deleteUserRate")
    @RequiresPermissions("rate::delete")
    public Result<Boolean> deleteUserRate(@RequestBody UserRateEntity dto){
        Result<Boolean> result = new Result<Boolean>();
        userRateEntityService.deleteUserRate(dto);
        result.setResult(true);
        result.setMessage("删除成功");
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
    @RequiresPermissions("rate::add")
    public Result<UserRateEntity> add(@RequestBody UserRateEntity userRateEntity) {
        Result<UserRateEntity> result = new Result<UserRateEntity>();
        try {
            //当前登录系统的用户
            LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
            SysUser sysUser = userService.getUserByName(loginUser.getUsername());

            //添加的用户
            String userName = userRateEntity.getUserName();
            SysUser user = userService.getUserByName(userName);
            if (user == null) {
                result.error500("添加的商户或介绍人不存在");
                return result;
            }
            userRateEntity.setAgentId(user.getAgentUsername());
            if (BaseConstant.USER_AGENT.equals(sysUser.getMemberType())){
                //当前登录的用户如果是代理，则只能添加该代理下的介绍人或商户
                List<SysUser> dbusers = userService.getUserAndReferByAgent(sysUser.getUsername());
                boolean flag = false;
                for(SysUser u:dbusers){
                    if(userName.equals(u.getUsername())){
                        flag = true;
                        break;
                    }
                }
                if(!flag){
                    result.error500("该商户或介绍人不属于此代理");
                    return result;
                }
                //高级代理登录添加的高级代理必须是当前登录人
                if(!userRateEntity.getAgentId().equals(sysUser.getUsername())){
                    result.error500("高级代理名称不匹配");
                    return result;
                }
            }
            //2、添加的是普通商户，校验该商户是否已经添加过
            if (BaseConstant.USER_MERCHANTS.equals(user.getMemberType())) {
                //判断该高级代理下的该用户，是否已经添加过费率
                String rate = userRateEntityService.getUserRateByUserNameAndAngetCode(userName,
                        userRateEntity.getAgentId(), userRateEntity.getChannelCode());
                if (StringUtils.isNotBlank(rate)) {
                    result.error500("该用户已经添加过费率");
                    return result;
                }
            }
            //添加商户存在介绍人，则校验该介绍人下的商户是否已经添加过
            if (!org.springframework.util.StringUtils.isEmpty(user.getSalesmanUsername())) {
                String rate = userRateEntityService.getBeIntroducerRate(userName, userRateEntity.getAgentId(),
                        userRateEntity.getIntroducerName(), userRateEntity.getChannelCode());
                if (StringUtils.isNotBlank(rate)) {
                    result.error500("该介绍人已经添加过费率，不能重复添加");
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
    @RequiresPermissions("rate::edit")
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
    @RequiresPermissions("rate::delete")
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
                List<UserRateEntity> listUserRateEntitys = ExcelImportUtil.importExcel(file.getInputStream(),
                        UserRateEntity.class, params);
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
