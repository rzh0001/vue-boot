package org.jeecg.modules.pay.controller.callBackController;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.pay.service.impl.CallBackServiceImpl;
import org.jeecg.modules.util.R;
import org.jeecg.modules.util.RequestHandleUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@RequestMapping("/callBack")
@Slf4j
public class CallBackController {

    @Autowired
    private CallBackServiceImpl callBackService;
    @RequestMapping(value = "/niuNanAlipay", method = RequestMethod.POST, produces = "application/x-www-form-urlencoded;charset=UTF-8")
    @ResponseBody
    public String niuNanAlipay() {
        try{
           return callBackService.callBack4niuNanAlipay();
        }catch (Exception e){
            log.info("==>牛腩支付回调异常，异常信息为：{}",e);
        }
        return "fail";
    }
}
