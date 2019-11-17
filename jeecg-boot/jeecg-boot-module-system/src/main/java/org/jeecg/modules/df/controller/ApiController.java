package org.jeecg.modules.df.controller;

import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.df.entity.CommonRequestBody;
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
    
    
    @PostMapping(value = "/order", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public CommonResponseBody createPayOrder(@Valid @RequestBody CommonRequestBody req) {

//        RequestParam param = JSON.toJavaObject(req, RequestParam.class);
        
        // 检查商户、代理状态
        
        String data = req.getData();
        
        apiService.createOrder(req);
        
        return apiService.createOrder(req);
        
    }
}
