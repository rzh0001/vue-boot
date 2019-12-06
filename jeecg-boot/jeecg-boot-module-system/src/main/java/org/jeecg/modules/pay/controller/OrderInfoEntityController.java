package org.jeecg.modules.pay.controller;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.constant.PayConstant;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.system.vo.DictModel;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.modules.pay.entity.CallBackResult;
import org.jeecg.modules.pay.entity.OrderInfoEntity;
import org.jeecg.modules.pay.service.IOrderInfoEntityService;
import org.jeecg.modules.system.entity.SysUser;
import org.jeecg.modules.system.service.ISysDictService;
import org.jeecg.modules.system.service.ISysUserService;
import org.jeecg.modules.util.BaseConstant;
import org.jeecg.modules.util.HttpResult;
import org.jeecg.modules.util.HttpUtils;
import org.jeecg.modules.util.R;
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
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;

/**
 * @Description: 订单信息
 * @Author: jeecg-boot
 * @Date: 2019-07-26
 * @Version: V1.0
 */
@Slf4j
@Api(tags = "订单信息")
@RestController
@RequestMapping("/pay/orderInfoEntity")
public class OrderInfoEntityController {
    @Autowired
    private IOrderInfoEntityService orderInfoEntityService;
    @Autowired
    private ISysUserService userService;
    @Autowired
    public ISysDictService dictService;

    /**
     * 分页列表查询
     *
     * @param orderInfoEntity
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @AutoLog(value = "订单信息-分页列表查询")
    @ApiOperation(value = "订单信息-分页列表查询", notes = "订单信息-分页列表查询")
    @GetMapping(value = "/list")
    public Result<IPage<OrderInfoEntity>> queryPageList(OrderInfoEntity orderInfoEntity,
                                                        @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                        @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                        HttpServletRequest req) {
        Result<IPage<OrderInfoEntity>> result = new Result<IPage<OrderInfoEntity>>();
        QueryWrapper<OrderInfoEntity> queryWrapper = initQueryCondition(orderInfoEntity, req);
        queryWrapper.lambda().orderByDesc(OrderInfoEntity::getCreateTime);
        Page<OrderInfoEntity> page = new Page<OrderInfoEntity>(pageNo, pageSize);
        IPage<OrderInfoEntity> pageList = orderInfoEntityService.page(page, queryWrapper);
        result.setSuccess(true);
        result.setResult(pageList);
        return result;
    }

    // 查询条件独立成方法，查询、统计、导出 三个接口使用
    private QueryWrapper<OrderInfoEntity> initQueryCondition(OrderInfoEntity orderInfoEntity, HttpServletRequest req) {
        LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        SysUser opUser = userService.getUserByName(loginUser.getUsername());

        QueryWrapper<OrderInfoEntity> queryWrapper = QueryGenerator.initQueryWrapper(orderInfoEntity,
                req.getParameterMap());
        if (StringUtils.isNotBlank(opUser.getMemberType())) {
            switch (opUser.getMemberType()) {
                case PayConstant.MEMBER_TYPE_AGENT:
                    queryWrapper.lambda().eq(OrderInfoEntity::getAgentId, opUser.getId());
                    break;
                case PayConstant.MEMBER_TYPE_SALESMAN:
                    queryWrapper.lambda().eq(OrderInfoEntity::getSalesmanId, opUser.getId());
                    break;
                case PayConstant.MEMBER_TYPE_MEMBER:
                    queryWrapper.lambda().eq(OrderInfoEntity::getUserId, opUser.getId());
                    break;
                default:
            }
        }
        return queryWrapper;
    }

    @GetMapping(value = "/summary")
    public Result<Map<String, Object>> summary(OrderInfoEntity orderInfoEntity, HttpServletRequest req) {
        Result<Map<String, Object>> result = new Result<>();
        QueryWrapper<OrderInfoEntity> queryWrapper = initQueryCondition(orderInfoEntity, req);

        Map<String, Object> map = orderInfoEntityService.summary(queryWrapper);
        Map<String, Object> map1 = new HashMap<>();
        map.put("totalCount", 5);

        result.setResult(map);
        result.setSuccess(true);

        return result;
    }

    /**
     * 添加
     *
     * @param orderInfoEntity
     * @return
     */
    @AutoLog(value = "订单信息-添加")
    @ApiOperation(value = "订单信息-添加", notes = "订单信息-添加")
    @PostMapping(value = "/add")
    public Result<OrderInfoEntity> add(@RequestBody OrderInfoEntity orderInfoEntity) {
        Result<OrderInfoEntity> result = new Result<OrderInfoEntity>();
        try {
            orderInfoEntityService.save(orderInfoEntity);
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
     * @param orderInfoEntity
     * @return
     */
    @AutoLog(value = "订单信息-编辑")
    @ApiOperation(value = "订单信息-编辑", notes = "订单信息-编辑")
    @PutMapping(value = "/edit")
    public Result<OrderInfoEntity> edit(@RequestBody OrderInfoEntity orderInfoEntity) {
        Result<OrderInfoEntity> result = new Result<OrderInfoEntity>();
        OrderInfoEntity orderInfoEntityEntity = orderInfoEntityService.getById(orderInfoEntity.getId());
        if (orderInfoEntityEntity == null) {
            result.error500("未找到对应实体");
        } else {
            boolean ok = orderInfoEntityService.updateById(orderInfoEntity);
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
    @AutoLog(value = "订单信息-通过id删除")
    @ApiOperation(value = "订单信息-通过id删除", notes = "订单信息-通过id删除")
    @DeleteMapping(value = "/delete")
    public Result<?> delete(@RequestParam(name = "id", required = true) String id) {
        try {
            orderInfoEntityService.removeById(id);
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
    @AutoLog(value = "订单信息-批量删除")
    @ApiOperation(value = "订单信息-批量删除", notes = "订单信息-批量删除")
    @DeleteMapping(value = "/deleteBatch")
    public Result<OrderInfoEntity> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        Result<OrderInfoEntity> result = new Result<OrderInfoEntity>();
        if (ids == null || "".equals(ids.trim())) {
            result.error500("参数不识别！");
        } else {
            this.orderInfoEntityService.removeByIds(Arrays.asList(ids.split(",")));
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
    @AutoLog(value = "订单信息-通过id查询")
    @ApiOperation(value = "订单信息-通过id查询", notes = "订单信息-通过id查询")
    @GetMapping(value = "/queryById")
    public Result<OrderInfoEntity> queryById(@RequestParam(name = "id", required = true) String id) {
        Result<OrderInfoEntity> result = new Result<OrderInfoEntity>();
        OrderInfoEntity orderInfoEntity = orderInfoEntityService.getById(id);
        if (orderInfoEntity == null) {
            result.error500("未找到对应实体");
        } else {
            result.setResult(orderInfoEntity);
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
    public ModelAndView exportXls(OrderInfoEntity orderInfoEntity, HttpServletRequest request, HttpServletResponse response) {
        // Step.1 组装查询条件
        QueryWrapper<OrderInfoEntity> queryWrapper = initQueryCondition(orderInfoEntity, request);
        queryWrapper.lambda().in(OrderInfoEntity::getStatus, CollUtil.newArrayList("2", "3"));

        //Step.2 AutoPoi 导出Excel
        List<OrderInfoEntity> pageList = orderInfoEntityService.list(queryWrapper);
        if (pageList.isEmpty()) {
            ModelAndView mv = new ModelAndView(new MappingJackson2JsonView());
            mv.addObject("code", "-1");
            mv.addObject("msg", "查询结果无成功订单");
            return mv;
        }
        log.info("文件导出{}条记录", pageList.size());
        //导出文件名称

        ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
        mv.addObject(NormalExcelConstants.FILE_NAME, "订单列表");
        mv.addObject(NormalExcelConstants.CLASS, OrderInfoEntity.class);
        mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("订单列表数据", "导出人:Jeecg", "导出信息"));
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
                List<OrderInfoEntity> listOrderInfoEntitys = ExcelImportUtil.importExcel(file.getInputStream(),
                        OrderInfoEntity.class, params);
                orderInfoEntityService.saveBatch(listOrderInfoEntitys);
                return Result.ok("文件导入成功！数据行数:" + listOrderInfoEntitys.size());
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

    @RequestMapping(value = "/againRequest", method = RequestMethod.GET)
    @RequiresPermissions("business::order::againRequest")
    public R againRequest(@RequestParam(name = "id") String id) {
        OrderInfoEntity order = orderInfoEntityService.queryOrderInfoByOrderId(id);
        //手动补单，密钥取订单中用户的密钥
        LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        SysUser sysUser = userService.getUserByName(loginUser.getUsername());
        log.info("==》手动补单操作==补单操作人：{}；单号为：{}", sysUser.getUsername(), id);
        if (order == null) {
            return R.error("订单不存在");
        }
//        if (order.getStatus() != BaseConstant.ORDER_STATUS_SUCCESS_NOT_RETURN) {
//            return R.error("订单状态不是‘成功未返回’，不能补单");
//        }
        if (order.getStatus() == BaseConstant.ORDER_STATUS_SUCCESS) {
            return R.error("订单状态已成功，不能补单");
        }
        return notifyCustomer(order,id);
    }

    /**
     * 线下补单
     * 线下补单：不通知商户
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "/offline", method = RequestMethod.GET)
    @RequiresPermissions("business::order::offline")
    public R offline(@RequestParam(name = "id") String id) {
        OrderInfoEntity order = orderInfoEntityService.queryOrderInfoByOrderId(id);
        LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        SysUser sysUser = userService.getUserByName(loginUser.getUsername());
        log.info("==》线下补单操作==补单操作人：{}；单号为：{}", sysUser.getUsername(), id);
        if (order == null) {
            return R.error("订单不存在");
        }
//        if (order.getStatus() != BaseConstant.ORDER_STATUS_SUCCESS_NOT_RETURN) {
//            return R.error("订单状态不是‘成功未返回’，不能补单");
//        }
        if (order.getStatus() == BaseConstant.ORDER_STATUS_SUCCESS) {
            return R.error("订单状态已成功，不能补单");
        }
        //状态更新为成功，已返回
        order.setStatus(BaseConstant.ORDER_STATUS_SUCCESS);
        order.setReplacementOrder("1");
        orderInfoEntityService.updateById(order);
        try {
            orderInfoEntityService.countAmount(id, order.getUserName(), order.getSubmitAmount().toString(),
                    order.getPayType());
            return R.ok("线下补单成功:" + id);
        } catch (Exception e) {
            log.info("线下补单失败，单号为：{}，失败原因为：{}", id, e);
        }
        return R.error("线下补单失败：" + id);
    }

    /**
     * 掉单补单入口
     *
     * @param id ：四方系统订单号
     * @return
     */
    @RequestMapping(value = "/notifyBusiness", method = RequestMethod.GET)
    @RequiresPermissions("business::order::notifyBusiness")
    public R notifyBusiness(@RequestParam(name = "id") String id) throws IOException, URISyntaxException {
        OrderInfoEntity order = orderInfoEntityService.queryOrderInfoByOrderId(id);
        LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        SysUser sysUser = userService.getUserByName(loginUser.getUsername());
        log.info("==》掉单补单操作==补单操作人：{}；单号为：{}", sysUser.getUsername(), id);
        if (order == null) {
            return R.error("订单不存在");
        }
//        List<DictModel> businessUrls = dictService.queryDictItemsByCode(BaseConstant.REQUEST_BUSINESS_URL);
//        Optional<DictModel> urlModel = businessUrls.stream().filter(model -> order.getPayType().equals(model.getText())).findFirst();
//        DictModel model = urlModel.orElse(null);
//        if (model == null) {
//            return R.error(order.getPayType() + "：未配置通知挂马地址");
//        }
        return notifyCustomer(order,id);
//        Map<String, String> params = new HashMap<>();
//        params.put("orderId", order.getOrderId());
//        String result = HttpUtils.doGet(model.getText(), params);
//        if ("success".equals(result)) {
//            return notifyCustomer(order,id);
//        } else {
//            return R.error("通知挂马失败，请联系管理员");
//        }
    }

    /**
     * 通知商户
     * @param order
     * @param id
     * @return
     */
    private R notifyCustomer(OrderInfoEntity order, String id) {
        boolean flag = false;
        try {
            order.setReplacementOrder("1");
            orderInfoEntityService.updateById(order);
            StringBuilder msg = new StringBuilder();
            SysUser orderUser = userService.getUserByName(order.getUserName());
            JSONObject callobj = orderInfoEntityService.encryptAESData(order, orderUser.getApiKey());
            log.info("===手动补单回调商户，url:{},param:{}", order.getSuccessCallbackUrl(), callobj.toJSONString());
            HttpResult result = HttpUtils.doPostJson(order.getSuccessCallbackUrl(), callobj.toJSONString());
            log.info("===手动补单商户返回信息=={}", result.getBody());
            if (result.getCode() == BaseConstant.SUCCESS) {
                CallBackResult callBackResult = JSONObject.parseObject(result.getBody(), CallBackResult.class);
                if (callBackResult.getCode() == BaseConstant.SUCCESS) {
                    msg.append("通知商户成功，并且商户返回成功");
                    orderInfoEntityService.updateOrderStatusSuccessByOrderId(id);
                    log.info("通知商户成功，并且商户返回成功,orderID:{}", id);
                    flag = true;
                    return R.ok(msg.toString());
                } else {
                    msg.append("通知商户失败,原因：").append(callBackResult.getMsg());
                    log.info("通通知商户失败,orderID:{}", id);
                    orderInfoEntityService.updateOrderStatusNoBackByOrderId(id);
                    return R.error(msg.toString());
                }
            } else {
                msg.append("通知商户失败,返回信息：").append(result.getBody());
                log.info("通通知商户失败,orderID:{}", id);
                orderInfoEntityService.updateOrderStatusNoBackByOrderId(id);
                return R.error(msg.toString());
            }
        } catch (Exception e) {
            log.info("补单失败，失败原因：{}", e);
            return R.error("通知商户失败，原因：商户未返回json格式数据");
        } finally {
            if (flag) {
                //5、只有在通知商户成功，才统计高级代理。商户。介绍人的收入情况
                try {
                    orderInfoEntityService.countAmount(id, order.getUserName(), order.getSubmitAmount().toString(),
                            order.getPayType());
                } catch (Exception e) {
                    log.info("==补单，统计额度异常，异常信息为：{}", e);
                }
            }
        }
    }
}
