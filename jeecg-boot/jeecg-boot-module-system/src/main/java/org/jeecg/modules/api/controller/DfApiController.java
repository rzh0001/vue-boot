package org.jeecg.modules.api.controller;

import com.google.common.base.Stopwatch;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.api.entity.ApiRequestBody;
import org.jeecg.modules.api.entity.ApiResponseBody;
import org.jeecg.modules.api.service.IDfApiService;
import org.jeecg.modules.df.dto.AssignOrderParamDTO;
import org.jeecg.modules.df.entity.PayOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * @author ruanzh
 * @since 2019/11/16
 */
@RestController("dfApiController")
@RequestMapping("/api")
@Slf4j
public class DfApiController {
    @Autowired
    private IDfApiService apiService;
    
    
    @PostMapping(value = "/order/create", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public ApiResponseBody createPayOrder(@Valid @RequestBody ApiRequestBody req) {
        
        return apiService.createOrder(req);
        
    }
    
    @PostMapping(value = "/order/query", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public ApiResponseBody query(@Valid @RequestBody ApiRequestBody req) {
        
        return apiService.queryOrder(req);
        
    }

    @PostMapping(value = "/order/assignOrder", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public ApiResponseBody assignOrder(@Valid @RequestBody AssignOrderParamDTO dto){
        log.info("分配订单请求开始，入参：{}",dto);
        Stopwatch stopwatch = Stopwatch.createStarted();
        PayOrder order = apiService.assignOrder(dto);
        stopwatch.stop();
        log.info("分配订单请求结束，耗时：{}",stopwatch);
        ApiResponseBody resp = ApiResponseBody.ok();
        resp.setData(order);
        return resp;
    }
    @PostMapping(value = "/order/callback/{orderNo}", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public ApiResponseBody callback(@PathVariable("orderNo")String orderId){
        log.info("接受回调请求，入参orderId:{}",orderId);
        Stopwatch stopwatch = Stopwatch.createStarted();

        stopwatch.stop();
        log.info("回调处理完毕，耗时：{}",stopwatch);
        ApiResponseBody resp = ApiResponseBody.ok();
        return resp;
    }
    
}
