package org.jeecg.modules.pay.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.pay.dto.RequestParamDTO;
import org.jeecg.modules.pay.entity.OrderInfoEntity;
import org.jeecg.modules.pay.externalUtils.antUtil.YitongUtil;
import org.jeecg.modules.pay.service.IOrderInfoEntityService;
import org.jeecg.modules.util.HttpResult;
import org.jeecg.modules.util.HttpUtils;
import org.jeecg.modules.util.R;
import org.jeecg.modules.util.RequestHandleUtil;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import java.util.TreeMap;

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
    @RequestMapping(value = "/create", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public R create(@RequestBody JSONObject reqobj,HttpServletResponse response,HttpServletRequest req) {
        try {
            return orderInfoService.createOrder(reqobj, req,response);
        } catch (Exception e) {
            log.info("创建订单异常，异常信息为：", e);
            return R.error("创建订单异常，请联系管理员");
        }
    }

    /**
     * 订单回调
     * 1、根据订单号查询订单信息
     * 2、通过http回调给商户，通知商户成功
     *
     * @param
     * @return
     */
    @RequestMapping(value = "/callback", method = {RequestMethod.POST,RequestMethod.GET})
    @ResponseBody
    public Object callback() {
        try {
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            Object param = RequestHandleUtil.getReqParam(request);
            Map<String, Object> map = (Map<String, Object>) param;
            Map<String, Object> checkMap = orderInfoService.isInternalSystem(map);
            boolean isInternalSystem =(Boolean) checkMap.get("isInternalSystem");
            if (isInternalSystem) {
                //内部系统之间的调用
                return orderInfoService.callback(new JSONObject(map),
                        ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest());
            } else {
                //外接系统的调用
                String payType = (String) checkMap.get("payType");
                return orderInfoService.innerSysCallBack(payType, param);
            }
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

    @PostMapping("/testCallBack")
    @ResponseBody
    public String testCallBack() {
        JSONObject result = new JSONObject();
        result.put("code", 200);
        result.put("msg", "success");
        return result.toJSONString();
    }

    private String pay_url = "http://payqqsh001.payto89.com/order/placeForIndex";

    private String key = "428f88b71f834ba289202a036d93afea";
    @GetMapping("/test")
    @ResponseBody
    public void test(HttpServletResponse response) throws Exception {
//        RequestHandleUtil.doPost(((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest());
    }

}
