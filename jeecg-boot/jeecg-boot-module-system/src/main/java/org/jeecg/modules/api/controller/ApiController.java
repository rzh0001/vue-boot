package org.jeecg.modules.api.controller;

import com.alibaba.fastjson.JSONObject;
import org.jeecg.modules.api.vo.ResultApiVo;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @title:
 * @Description:
 * @author: wangjb
 * @create: 2019-06-20 10:38
 */
public class ApiController {

    /**
     * 订单创建：入参
     * pay_memberid 商户ID
     * pay_orderid 订单号
     * pay_amount  交易金额
     * pay_bankcode 通道编码
     * @param reqobj
     * @return
     */
    @RequestMapping(value = "/Pay_Index.html", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public ResultApiVo createQrOrder(@RequestBody JSONObject reqobj){
        return null;
    }

    /**
     * 订单回调
     * @param params
     * @return
     */
    @PostMapping("callback")
    public ResultApiVo callback(@RequestParam Map<String, Object> params){
        return null;
    }

    /**
     * 订单查询（AES）
     * @param reqobj
     * @return
     */
    @PostMapping("queryOrder")
    public ResultApiVo verifyQrOrder(@RequestBody JSONObject reqobj){
        return null;
    }
}
