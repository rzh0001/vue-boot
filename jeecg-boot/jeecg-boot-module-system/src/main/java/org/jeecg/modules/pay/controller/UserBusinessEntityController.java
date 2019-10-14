package org.jeecg.modules.pay.controller;

import java.math.BigDecimal;
import java.util.*;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.pay.entity.BusinessLabelValue;
import org.jeecg.modules.pay.entity.UserBusinessEntity;
import org.jeecg.modules.pay.service.IUserBusinessEntityService;
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
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;
import com.alibaba.fastjson.JSON;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * @Description: 代理关联挂码
 * @Author: jeecg-boot
 * @Date: 2019-07-26
 * @Version: V1.0
 */
@Slf4j
@Api(tags = "代理关联挂码")
@RestController
@RequestMapping("/pay/userBusinessEntity")
public class UserBusinessEntityController {
    @Autowired
    private IUserBusinessEntityService userBusinessEntityService;
    @Autowired
    private ISysUserService userService;

    /**
     * 分页列表查询
     *
     * @param userBusinessEntity
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @AutoLog(value = "用户关联商户-分页列表查询")
    @ApiOperation(value = "用户关联商户-分页列表查询", notes = "用户关联商户-分页列表查询")
    @GetMapping(value = "/list")
    public Result<IPage<UserBusinessEntity>> queryPageList(UserBusinessEntity userBusinessEntity,
                                                           @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                           @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                           HttpServletRequest req) {
        //获取系统用户
        LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        SysUser sysUser = userService.getUserByName(loginUser.getUsername());

        Result<IPage<UserBusinessEntity>> result = new Result<IPage<UserBusinessEntity>>();
        QueryWrapper<UserBusinessEntity> queryWrapper = QueryGenerator.initQueryWrapper(userBusinessEntity,
                req.getParameterMap());
        Page<UserBusinessEntity> page = new Page<UserBusinessEntity>(pageNo, pageSize);
        IPage<UserBusinessEntity> pageList = userBusinessEntityService.page(page, queryWrapper);
        List<UserBusinessEntity> newList = new ArrayList<>();
        //如果登录的用户是admin。则展示全部，如果是代理，则展示代理关联的信息
        if (BaseConstant.USER_AGENT.equals(sysUser.getMemberType())) {
            List<UserBusinessEntity> list = pageList.getRecords();
            for (UserBusinessEntity b : list) {
                if (b.getUserName().equals(sysUser.getUsername())) {
                    newList.add(b);
                }
            }
            pageList.setRecords(newList);
        } else if (BaseConstant.USER_MERCHANTS.equals(sysUser.getMemberType()) || BaseConstant.USER_REFERENCES.equals(sysUser.getMemberType())) {
            //商户、介绍人 不展示信息
            pageList.setRecords(newList);
        }
        result.setSuccess(true);
        result.setResult(pageList);
        return result;
    }

    @GetMapping(value = "/queryUserBusiness")
    @RequiresPermissions("user::business::detail")
    public Result<List<UserBusinessEntity>> queryUserBusiness(@RequestParam(name = "username") String username) {
        Result<List<UserBusinessEntity>> result = new Result<List<UserBusinessEntity>>();
        result.setResult(userBusinessEntityService.queryUserBusiness(username));
        return result;
    }

    @PostMapping(value = "/deleteUserBusiness")
    @RequiresPermissions("user::business::delete")
    public Result<Boolean> deleteUserBusiness(@RequestBody UserBusinessEntity userBusinessEntity) {
        Result<Boolean> result = new Result<Boolean>();
        userBusinessEntityService.deleteUserBusiness(userBusinessEntity);
        result.setResult(true);
        result.setMessage("删除成功");
        return result;
    }

    /**
     * 添加
     *
     * @param userBusinessEntity
     * @return
     */
    @AutoLog(value = "代理关联挂码-添加")
    @ApiOperation(value = "用户关联商户-添加", notes = "用户关联商户-添加")
    @PostMapping(value = "/add")
    @RequiresPermissions("user::business::add")
    public Result<UserBusinessEntity> add(@RequestBody UserBusinessEntity userBusinessEntity) {
        Result<UserBusinessEntity> result = new Result<UserBusinessEntity>();
        try {
            result = userBusinessEntityService.add(userBusinessEntity);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result.error500("操作失败");
        }
        return result;
    }

    @GetMapping(value = "/queryBusinessByUserName")
    public Result<Map<String, Object>> queryBusinessByUserName(@RequestParam(name = "userName") String userName,
                                                               @RequestParam(name = "channelCode") String channelCode) {
        Result<Map<String, Object>> result = new Result<Map<String, Object>>();
        Map<String, Object> re = new HashMap<>();
        List<String> associated = new ArrayList<>();
        List<BusinessLabelValue> all = new ArrayList<>();
        UserBusinessEntity userBusinessEntity = new UserBusinessEntity();
        userBusinessEntity.setUserName(userName);
        userBusinessEntity.setChannelCode(channelCode);
        List<UserBusinessEntity> list = userBusinessEntityService.queryAllBusiness(userBusinessEntity);
        for (UserBusinessEntity b : list) {
            StringBuilder msg = new StringBuilder();
            BusinessLabelValue labelValue = new BusinessLabelValue();
            labelValue.setLabel(msg.append("子账号名称：").append(b.getBusinessCode()).append("；余额=").append(b.getRechargeAmount() == null ?
                    0.00 : b.getRechargeAmount()).toString());
            labelValue.setValue(b.getBusinessCode());
            all.add(labelValue);
            if ("1".equals(b.getActive())) {
                associated.add(b.getBusinessCode());
            }
        }
        re.put("all", all);
        re.put("associated", org.apache.commons.lang.StringUtils.join(associated.toArray(), ","));
        result.setResult(re);
        return result;
    }

    @GetMapping(value = "/getBusinessCodesByAgentName")
    public Result<List<String>> getBusinessCodesByAgentName(@RequestParam(name = "userName") String userName,
                                                            @RequestParam(name = "channelCode") String channelCode) {
        Result<List<String>> result = new Result<List<String>>();
        List<String> businessCodes = userBusinessEntityService.getBusinessCodesByAgentName(userName, channelCode);
        result.setResult(businessCodes);
        return result;
    }

    @GetMapping("/activeBusiness")
    @RequiresPermissions("user:business:activeBusiness")
    public Result<String> activeBusiness(@RequestParam(name = "userName") String userName, @RequestParam(name =
            "channelCode") String channelCode, @RequestParam(name = "businesses") String businesses) {
        Result<String> result = new Result<String>();
        if (StringUtils.isBlank(channelCode)) {
            result.setResult("请选择通道");
            return result;
        }
        if (StringUtils.isBlank(businesses)) {
            //如果为空，标识全部不激活
            userBusinessEntityService.disableAllBusiness(userName, channelCode);
            result.setResult("激活成功");
            return result;
        }
        String[] codes = businesses.split(",");
        userBusinessEntityService.activeBusiness(userName, channelCode, codes);
        userBusinessEntityService.disableBusiness(userName, channelCode, codes);
        result.setResult("激活成功");
        return result;
    }

    @GetMapping("/rechargeAmount")
    @RequiresPermissions("user:business:rechargeAmount")
    public Result<String> rechargeAmount(@RequestParam(name = "userName") String userName, @RequestParam(name =
            "channelCode") String channelCode, @RequestParam(name = "businessCode") String businesses,
                                         @RequestParam(name = "rechargeAmount") String amount) {
        Result<String> result = new Result<String>();
        if (StringUtils.isBlank(userName) || StringUtils.isBlank(channelCode) || StringUtils.isBlank(businesses) || StringUtils.isBlank(amount)) {
            result.setResult("充值失败，参数不全");
            return result;
        }
        try {
            userBusinessEntityService.rechargeAmount(userName, channelCode, businesses, Double.parseDouble(amount));
            result.setResult(businesses + "：充值金额[" + amount + "]成功");
            return result;
        } catch (Exception e) {
            log.info("充值金额异常，异常信息：{}", e);
            result.setResult("金额充值异常");
            return result;
        }
    }

    @GetMapping("/queryRechargeAmount")
    public Result<Double> getRechargeAmount(@RequestParam(name = "userName") String userName, @RequestParam(name =
            "channelCode") String channelCode, @RequestParam(name = "businesses") String businesses) {
        Result<Double> result = new Result<Double>();
        if (StringUtils.isBlank(userName) || StringUtils.isBlank(channelCode) || StringUtils.isBlank(businesses)) {
            result.setResult(null);
            return result;
        }
        try {
            BigDecimal rechagerAmount = userBusinessEntityService.getRechargeAmount(userName, channelCode, businesses);
            result.setResult(rechagerAmount.doubleValue());
            return result;
        } catch (Exception e) {
            log.info("查询充值金额异常，异常信息：{}", e);
            result.setResult(null);
            return result;
        }
    }

    /**
     * 编辑
     *
     * @param userBusinessEntity
     * @return
     */
    @AutoLog(value = "用户关联商户-编辑")
    @ApiOperation(value = "用户关联商户-编辑", notes = "用户关联商户-编辑")
    @PutMapping(value = "/edit")
    @RequiresPermissions("user::business::edit")
    public Result<UserBusinessEntity> edit(@RequestBody UserBusinessEntity userBusinessEntity) {
        Result<UserBusinessEntity> result = new Result<UserBusinessEntity>();
        UserBusinessEntity userBusinessEntityEntity = userBusinessEntityService.getById(userBusinessEntity.getId());
        if (userBusinessEntityEntity == null) {
            result.error500("未找到对应实体");
        } else {
            boolean ok = userBusinessEntityService.updateById(userBusinessEntity);
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
    @AutoLog(value = "用户关联商户-通过id删除")
    @ApiOperation(value = "用户关联商户-通过id删除", notes = "用户关联商户-通过id删除")
    @DeleteMapping(value = "/delete")
    @RequiresPermissions("user::business::delete")
    public Result<?> delete(@RequestParam(name = "id", required = true) String id) {
        try {
            userBusinessEntityService.removeById(id);
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
    @AutoLog(value = "用户关联商户-批量删除")
    @ApiOperation(value = "用户关联商户-批量删除", notes = "用户关联商户-批量删除")
    @DeleteMapping(value = "/deleteBatch")
    public Result<UserBusinessEntity> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        Result<UserBusinessEntity> result = new Result<UserBusinessEntity>();
        if (ids == null || "".equals(ids.trim())) {
            result.error500("参数不识别！");
        } else {
            this.userBusinessEntityService.removeByIds(Arrays.asList(ids.split(",")));
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
    @AutoLog(value = "用户关联商户-通过id查询")
    @ApiOperation(value = "用户关联商户-通过id查询", notes = "用户关联商户-通过id查询")
    @GetMapping(value = "/queryById")
    public Result<UserBusinessEntity> queryById(@RequestParam(name = "id", required = true) String id) {
        Result<UserBusinessEntity> result = new Result<UserBusinessEntity>();
        UserBusinessEntity userBusinessEntity = userBusinessEntityService.getById(id);
        if (userBusinessEntity == null) {
            result.error500("未找到对应实体");
        } else {
            result.setResult(userBusinessEntity);
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
        QueryWrapper<UserBusinessEntity> queryWrapper = null;
        try {
            String paramsStr = request.getParameter("paramsStr");
            if (oConvertUtils.isNotEmpty(paramsStr)) {
                String deString = URLDecoder.decode(paramsStr, "UTF-8");
                UserBusinessEntity userBusinessEntity = JSON.parseObject(deString, UserBusinessEntity.class);
                queryWrapper = QueryGenerator.initQueryWrapper(userBusinessEntity, request.getParameterMap());
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        //Step.2 AutoPoi 导出Excel
        ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
        List<UserBusinessEntity> pageList = userBusinessEntityService.list(queryWrapper);
        //导出文件名称
        mv.addObject(NormalExcelConstants.FILE_NAME, "用户关联商户列表");
        mv.addObject(NormalExcelConstants.CLASS, UserBusinessEntity.class);
        mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("用户关联商户列表数据", "导出人:Jeecg", "导出信息"));
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
                List<UserBusinessEntity> listUserBusinessEntitys = ExcelImportUtil.importExcel(file.getInputStream(),
                        UserBusinessEntity.class, params);
                userBusinessEntityService.saveBatch(listUserBusinessEntitys);
                return Result.ok("文件导入成功！数据行数:" + listUserBusinessEntitys.size());
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
