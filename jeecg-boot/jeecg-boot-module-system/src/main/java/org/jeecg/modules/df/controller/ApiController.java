package org.jeecg.modules.df.controller;

import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.df.entity.ApiRequestBody;
import org.jeecg.modules.df.entity.CommonResponseBody;
import org.jeecg.modules.df.service.IApiService;
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
public class ApiController {
    @Autowired
    private IApiService apiService;
    
    
    @PostMapping(value = "/order/create", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public CommonResponseBody createPayOrder(@Valid @RequestBody ApiRequestBody req) {
        
        return apiService.createOrder(req);
        
    }
    
    @PostMapping(value = "/order/query", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public CommonResponseBody query(@Valid @RequestBody ApiRequestBody req) {
        
        return apiService.queryOrder(req);
        
    }
    
    
}
