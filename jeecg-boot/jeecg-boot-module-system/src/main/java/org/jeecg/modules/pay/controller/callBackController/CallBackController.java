package org.jeecg.modules.pay.controller.callBackController;

import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.api.constant.PayTypeEnum;
import org.jeecg.modules.pay.service.impl.CallBackServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/callBack")
@Slf4j
public class CallBackController {

    @Autowired
    private CallBackServiceImpl callBackService;

    @RequestMapping(value = "/order/{payType}/{orderNoField}")
    @ResponseBody
    public String callBack(@PathVariable String payType,@PathVariable String orderNoField) throws Exception {
        log.info("==>三方回调类型:{},单号标识：{}",payType,orderNoField);
        return (String)callBackService.callBack(orderNoField, payType);
    }

    @RequestMapping(value = "/yitongAlipayCallback", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String yitongAlipay() throws Exception {
        return (String)callBackService.callBack("sh_order", PayTypeEnum.YITONG_ALIPAY.getValue());
    }
}
