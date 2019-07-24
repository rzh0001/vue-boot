package org.jeecg.modules.pay.controller;

import com.alibaba.fastjson.JSONObject;
import org.jeecg.modules.pay.service.IOrderInfoEntityService;
import org.jeecg.modules.util.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @title:
 * @Description:
 * @author: wangjb
 * @create: 2019-06-20 10:38
 */
public class ApiController {
    @Autowired
    private IOrderInfoEntityService orderInfoService;
    /**
     * 订单创建：入参
     * pay_memberid 商户ID
     * pay_orderid 订单号
     * pay_amount  交易金额
     * pay_bankcode 通道编码
     * @param reqobj
     * @return
     */
    @RequestMapping(value = "/create", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public R create(@RequestBody JSONObject reqobj){
        return null;
    }

    /**
     * 订单回调
     * 1、根据订单号查询订单信息
     * 2、通过http回调给商户，通知商户成功
     * @param reqobj
     * @return
     */
    @PostMapping("/callback")
    @ResponseBody
    public R callback(@RequestParam JSONObject reqobj, HttpServletRequest req){
        return orderInfoService.callback(reqobj,req);
    }


    /**
     * 订单查询（AES）
     * @param reqobj
     * @return
     */
    @PostMapping("/queryOrder")
    @ResponseBody
    public R queryOrder(@RequestBody JSONObject reqobj){
        return orderInfoService.queryOrderInfo(reqobj);
    }
}
