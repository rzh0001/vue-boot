package org.jeecg.modules.pay.controller;

import java.util.*;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.pay.entity.ChannelEntity;
import org.jeecg.modules.pay.entity.UserBusinessEntity;
import org.jeecg.modules.pay.entity.UserChannelEntity;
import org.jeecg.modules.pay.service.IChannelEntityService;
import org.jeecg.modules.pay.service.IUserBusinessEntityService;
import org.jeecg.modules.pay.service.IUserChannelEntityService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;

import org.jeecg.modules.productChannel.service.IProductChannelService;
import org.jeecg.modules.system.entity.SysUser;
import org.jeecg.modules.system.service.ISysUserService;
import org.jeecg.modules.util.BaseConstant;
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
 * @Description: 用户关联通道
 * @Author: jeecg-boot
 * @Date: 2019-07-26
 * @Version: V1.0
 */
@Slf4j
@Api(tags = "用户关联通道")
@RestController
@RequestMapping("/pay/userChannelEntity")
public class UserChannelEntityController {
    @Autowired
    private IUserChannelEntityService userChannelEntityService;
    @Autowired
    private ISysUserService userService;
    @Autowired
    private IUserBusinessEntityService userBusinessEntityService;
    @Autowired
    private IChannelEntityService channelEntityService;

    @Autowired
    private IProductChannelService productChannelService;
    @GetMapping(value = "/saveUserChannels")
    public Result<String> getChannel(@RequestParam(name = "channelCodes") String channelCodes,
        @RequestParam(name="userName")String userName,@RequestParam(name = "productCode")String productCode){
        List<String> productChannel = productChannelService.getChannelByProductCode(productCode);
        //删除已关联信息
        if(!CollectionUtils.isEmpty(productChannel)){
            userChannelEntityService.deleteChannel(userName,productChannel);
        }
        Result<String> result = new Result<>();
        if(StringUtils.isNotBlank(channelCodes)){
            List<String> codes = Arrays.asList(channelCodes.split(","));
            codes = codes.stream().filter(code -> productChannel.contains(code)).collect(Collectors.toList());
            SysUser sysUser = userService.getUserByName(userName);
            if(CollectionUtils.isEmpty(codes)){
                result.setMessage("success");
                return result;
            }
            List<ChannelEntity> channels = channelEntityService.queryChannelByCodes(codes);
            for(ChannelEntity channel:channels){
                UserChannelEntity userChannelEntity = new UserChannelEntity();
                userChannelEntity.setMemberType(sysUser.getMemberType());
                userChannelEntity.setUserId(sysUser.getId());
                userChannelEntity.setUserName(sysUser.getUsername());
                userChannelEntity.setChannelCode(channel.getChannelCode());
                userChannelEntity.setChannelId(channel.getId());
                userChannelEntityService.save(userChannelEntity);
            }
        }

        result.setMessage("success");
        return result;
    }
    /**
     * 分页列表查询
     *
     * @param userChannelEntity
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @AutoLog(value = "用户关联通道-分页列表查询")
    @ApiOperation(value = "用户关联通道-分页列表查询", notes = "用户关联通道-分页列表查询")
    @GetMapping(value = "/list")
    public Result<IPage<UserChannelEntity>> queryPageList(UserChannelEntity userChannelEntity,
                                                          @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                          @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                          HttpServletRequest req) {
        //获取系统用户
        LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        SysUser sysUser = userService.getUserByName(loginUser.getUsername());

        Result<IPage<UserChannelEntity>> result = new Result<IPage<UserChannelEntity>>();
        QueryWrapper<UserChannelEntity> queryWrapper = QueryGenerator.initQueryWrapper(userChannelEntity, req.getParameterMap());
        Page<UserChannelEntity> page = new Page<UserChannelEntity>(pageNo, pageSize);
        IPage<UserChannelEntity> pageList = userChannelEntityService.page(page, queryWrapper);
        List<UserChannelEntity> db = pageList.getRecords();
        List<UserChannelEntity> newList = new ArrayList<>();
        //如果是管理员登录，则展示全部的信息；如果是商户、代理登录
        if (BaseConstant.USER_AGENT.equals(sysUser.getMemberType())) {
            //代理登录，则展示代理关联的。和代理下面商户关联的
            List<SysUser> users = userService.getUserByAgent(sysUser.getUsername());
            for (UserChannelEntity uc : db) {
                if (uc.getUserName().equals(sysUser.getUsername())) {
                    newList.add(uc);
                }
                for (SysUser user : users) {
                    if (user.getUsername().equals(uc.getUserName())) {
                        newList.add(uc);
                        break;
                    }
                }
            }
            pageList.setRecords(newList);
        } else if (BaseConstant.USER_MERCHANTS.equals(sysUser.getMemberType())) {
            //商户登录，则只展示属于商户自己的关联信息
            for (UserChannelEntity uc : db) {
                if (uc.getUserName().equals(sysUser.getUsername())) {
                    newList.add(uc);
                }
            }
            pageList.setRecords(newList);
        }
        result.setSuccess(true);
        result.setResult(pageList);
        return result;
    }


    @GetMapping(value = "/queryChannelByUserName")
    @RequiresPermissions("channel::detail")
    public Result<List<UserChannelEntity>> queryChannelByUserName(@RequestParam(name = "username") String username) {
        Result<List<UserChannelEntity>> result = new Result<List<UserChannelEntity>>();
        result.setResult(userChannelEntityService.queryChannelByUserName(username));
        return result;
    }

    @PostMapping(value = "/deleteUserChannel")
    @RequiresPermissions("channel::delete")
    public Result<Boolean> deleteUserChannel(@RequestBody UserChannelEntity dto) {
        Result<Boolean> result = new Result<Boolean>();
        SysUser user = userService.getUserByName(dto.getUserName());
        if (user.getMemberType().equals(BaseConstant.USER_AGENT)) {
            //删除代理关联的通道，则需要先删除代理关联的挂马，再删除代理关联的通道
            UserBusinessEntity userBusinessEntity = new UserBusinessEntity();
            userBusinessEntity.setChannelCode(dto.getChannelCode());
            userBusinessEntity.setUserName(dto.getUserName());
            userBusinessEntity.setBusinessCode(dto.getBusinessCode());
            userBusinessEntityService.deleteUserBusiness(userBusinessEntity);
            List<UserBusinessEntity> ub = userBusinessEntityService.queryBusiness(dto.getUserName(),dto.getChannelCode());
            if(CollectionUtils.isEmpty(ub)){
                userChannelEntityService.deleteUserChannel(dto.getUserName(), dto.getChannelCode());
            }
        }else{
            userChannelEntityService.deleteUserChannel(dto.getUserName(), dto.getChannelCode());
        }
        result.setResult(true);
        result.setMessage("删除成功");
        return result;
    }

    /**
     * 代理和商户才能关联通道
     *
     * @param userChannelEntity
     * @return
     */
    @AutoLog(value = "用户关联通道-添加")
    @ApiOperation(value = "用户关联通道-添加", notes = "用户关联通道-添加")
    @PostMapping(value = "/add")
    @RequiresPermissions("channel::user::add")
    public Result<UserChannelEntity> add(@RequestBody UserChannelEntity userChannelEntity) {
        Result<UserChannelEntity> result = new Result<UserChannelEntity>();
        try {
            String userName = userChannelEntity.getUserName();
            SysUser user = userService.getUserByName(userName);
            if (user == null) {
                result.error500("用户不存在");
                return result;
            }
            if (BaseConstant.USER_REFERENCES.equals(user.getMemberType())) {
                result.error500("介绍人无法关联通道");
                return result;
            }
            UserChannelEntity channel = userChannelEntityService.queryChannelAndUserName(userChannelEntity.getChannelCode(), userChannelEntity.getUserName());
            if (BaseConstant.USER_MERCHANTS.equals(user.getMemberType())) {
                if (channel != null) {
                    result.error500("该用户已经添加过通道");
                    return result;
                }
                userChannelEntity.setMemberType(user.getMemberType());
                userChannelEntityService.save(userChannelEntity);
            } else if (BaseConstant.USER_AGENT.equals(user.getMemberType())) {
                List<UserBusinessEntity> ub = userBusinessEntityService.queryBusiness2(userChannelEntity.getUserName(),userChannelEntity.getChannelCode(),userChannelEntity.getBusinessCode());
                if(!CollectionUtils.isEmpty(ub)){
                    result.error500("该账号已经关联过此通道");
                    return result;
                }
                //如果是代理，则需要设置和挂马账号的关联关系
                if (StringUtils.isEmpty(userChannelEntity.getBusinessCode())) {
                    result.error500("挂马账号不能为空");
                    return result;
                }
                if(channel == null){
                    userChannelEntity.setMemberType(user.getMemberType());
                    userChannelEntityService.save(userChannelEntity);
                }
                UserBusinessEntity business = new UserBusinessEntity();
                business.setUserId(user.getId());
                business.setUserName(userChannelEntity.getUserName());
                business.setChannelCode(userChannelEntity.getChannelCode());
                business.setApiKey(userChannelEntity.getApiKey());
                business.setBusinessCode(userChannelEntity.getBusinessCode());
                userBusinessEntityService.add(business);
            }else {
                return result.error500("操作失败");
            }
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
     * @param userChannelEntity
     * @return
     */
    @AutoLog(value = "用户关联通道-编辑")
    @ApiOperation(value = "用户关联通道-编辑", notes = "用户关联通道-编辑")
    @PutMapping(value = "/edit")
    @RequiresPermissions("channel::user::edit")
    public Result<UserChannelEntity> edit(@RequestBody UserChannelEntity userChannelEntity) {
        Result<UserChannelEntity> result = new Result<UserChannelEntity>();
        UserChannelEntity userChannelEntityEntity = userChannelEntityService.getById(userChannelEntity.getId());
        if (userChannelEntityEntity == null) {
            result.error500("未找到对应实体");
        } else {
            boolean ok = userChannelEntityService.updateById(userChannelEntity);
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
    @AutoLog(value = "用户关联通道-通过id删除")
    @ApiOperation(value = "用户关联通道-通过id删除", notes = "用户关联通道-通过id删除")
    @DeleteMapping(value = "/delete")
    @RequiresPermissions("channel::user::delete")
    public Result<?> delete(@RequestParam(name = "id", required = true) String id) {
        try {
            userChannelEntityService.removeById(id);
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
    @AutoLog(value = "用户关联通道-批量删除")
    @ApiOperation(value = "用户关联通道-批量删除", notes = "用户关联通道-批量删除")
    @DeleteMapping(value = "/deleteBatch")
    public Result<UserChannelEntity> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        Result<UserChannelEntity> result = new Result<UserChannelEntity>();
        if (ids == null || "".equals(ids.trim())) {
            result.error500("参数不识别！");
        } else {
            this.userChannelEntityService.removeByIds(Arrays.asList(ids.split(",")));
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
    @AutoLog(value = "用户关联通道-通过id查询")
    @ApiOperation(value = "用户关联通道-通过id查询", notes = "用户关联通道-通过id查询")
    @GetMapping(value = "/queryById")
    public Result<UserChannelEntity> queryById(@RequestParam(name = "id", required = true) String id) {
        Result<UserChannelEntity> result = new Result<UserChannelEntity>();
        UserChannelEntity userChannelEntity = userChannelEntityService.getById(id);
        if (userChannelEntity == null) {
            result.error500("未找到对应实体");
        } else {
            result.setResult(userChannelEntity);
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
        QueryWrapper<UserChannelEntity> queryWrapper = null;
        try {
            String paramsStr = request.getParameter("paramsStr");
            if (oConvertUtils.isNotEmpty(paramsStr)) {
                String deString = URLDecoder.decode(paramsStr, "UTF-8");
                UserChannelEntity userChannelEntity = JSON.parseObject(deString, UserChannelEntity.class);
                queryWrapper = QueryGenerator.initQueryWrapper(userChannelEntity, request.getParameterMap());
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        //Step.2 AutoPoi 导出Excel
        ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
        List<UserChannelEntity> pageList = userChannelEntityService.list(queryWrapper);
        //导出文件名称
        mv.addObject(NormalExcelConstants.FILE_NAME, "用户关联通道列表");
        mv.addObject(NormalExcelConstants.CLASS, UserChannelEntity.class);
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
                List<UserChannelEntity> listUserChannelEntitys = ExcelImportUtil.importExcel(file.getInputStream(), UserChannelEntity.class, params);
                userChannelEntityService.saveBatch(listUserChannelEntitys);
                return Result.ok("文件导入成功！数据行数:" + listUserChannelEntitys.size());
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
