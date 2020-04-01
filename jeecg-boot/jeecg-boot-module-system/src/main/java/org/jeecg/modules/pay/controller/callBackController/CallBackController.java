package org.jeecg.modules.pay.controller.callBackController;

import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.pay.service.impl.CallBackServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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

    @RequestMapping(value = "/tengfeiAlipay", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String tengFeiAlipay() {
        try{
            return callBackService.callBackTengFeiAlipay();
        }catch (Exception e){
            log.info("==>腾飞支付回调异常，异常信息为：{}",e);
        }
        return "fail";
    }
    @RequestMapping(value = "/leTianAlipay", method = RequestMethod.POST, produces = "application/x-www-form-urlencoded;charset=UTF-8")
    @ResponseBody
    public String leTianAlipay(){
        try{
            return  callBackService.callBackLeTianAlipay();
        }catch (Exception e){
            log.info("==>乐天支付，回调异常：{}",e);
        }
        return "fail";
    }
}
