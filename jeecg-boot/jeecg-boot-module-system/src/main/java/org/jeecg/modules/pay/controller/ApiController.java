package org.jeecg.modules.pay.controller;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.exception.RRException;
import org.jeecg.modules.pay.service.IOrderInfoEntityService;
import org.jeecg.modules.util.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * @title:
 * @Description:
 * @author: wangjb
 * @create: 2019-06-20 10:38
 */
@RestController
@RequestMapping("/api")
@Slf4j
public class ApiController {
    @Autowired
    private IOrderInfoEntityService orderInfoService;

    /**
     * 订单创建：入参
     * userId 商户ID
     * outerOrderId 订单号
     * callbackUrl 回调商户地址
     * submitAmount  交易金额
     * payType 通道编码
     *
     * @param reqobj
     * @return
     */
    @RequestMapping(value = "/create", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public R create(@RequestBody JSONObject reqobj) {
        try {
            return orderInfoService.createOrder(reqobj);
        } catch (Exception e) {
            log.info("创建订单异常，异常信息为：", e);
            return R.error(e.getMessage());
        }
    }

    /**
     * 订单回调
     * 1、根据订单号查询订单信息
     * 2、通过http回调给商户，通知商户成功
     *
     * @param reqobj
     * @return
     */
    @PostMapping(value = "/callback", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public R callback(@RequestBody JSONObject reqobj) {
        try {
            return orderInfoService.callback(reqobj,((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getRequest());
        } catch (Exception e) {
            log.info("订单回调异常，异常信息为：", e);
            return R.error(e.getMessage());
        }
    }


    /**
     * 订单查询（AES）
     *
     * @param reqobj
     * @return
     */
    @PostMapping("/queryOrder")
    @ResponseBody
    public R queryOrder(@RequestBody JSONObject reqobj) {
        return orderInfoService.queryOrderInfo(reqobj);
    }
}
