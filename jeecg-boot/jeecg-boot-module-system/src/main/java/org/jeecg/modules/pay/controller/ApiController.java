package org.jeecg.modules.pay.controller;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.pay.service.IOrderInfoEntityService;
import org.jeecg.modules.util.R;
import org.jeecg.modules.util.RequestHandleUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

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
            return orderInfoService.createOrder(reqobj, ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest());
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
     * @param
     * @return
     */
    @RequestMapping(value = "/callback", method = {RequestMethod.POST,RequestMethod.GET})
    @ResponseBody
    public Object callback() {
        try {
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            Object param = RequestHandleUtil.getReqParam(request);
            String payType = null;
            Map<String, Object> map = (Map<String, Object>) param;
            if (map.get("attch") == null) {
                //内部系统之间的调用
                return orderInfoService.callback(new JSONObject(map),
                        ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest());
            } else if(map.get("attch") != null){
                //外接系统的调用
                payType = (String) map.get("attch");
                return orderInfoService.innerSysCallBack(payType, param);
            }else{
                log.info("未匹配到回调器");
                return null;
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
}
