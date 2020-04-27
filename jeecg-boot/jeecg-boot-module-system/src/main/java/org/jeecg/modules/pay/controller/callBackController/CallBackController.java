package org.jeecg.modules.pay.controller.callBackController;

import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.api.constant.PayTypeEnum;
import org.jeecg.modules.pay.service.AbstractCallBack;
import org.jeecg.modules.pay.service.factory.CallBackServiceFactory;
import org.jeecg.modules.pay.service.impl.CallBackServiceImpl;
import org.jeecg.modules.util.BaseConstant;
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
    public String niuNanAlipay() throws Exception {
        return (String)callBackService.callBack("orderNo", PayTypeEnum.NIUNAN_APILAY.getValue());
    }

    @RequestMapping(value = "/tengfeiAlipay", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String tengFeiAlipay() throws Exception {
        return (String)callBackService.callBack("orderNo",PayTypeEnum.TENGFEI_ALIPAY.getValue());
    }
    @RequestMapping(value = "/leTianAlipay", method = RequestMethod.POST, produces = "application/x-www-form-urlencoded;charset=UTF-8")
    @ResponseBody
    public String leTianAlipay() throws Exception {
        return (String)callBackService.callBack("outTradeNo",PayTypeEnum.LETIAN_ALIPAY.getValue());
    }

    @RequestMapping(value = "/antAlipayCallback", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String antAlipay() throws Exception {
        return (String)callBackService.callBack("out_trade_no",PayTypeEnum.ANT_ALIPAY.getValue());
    }

    @RequestMapping(value = "/gtpaiAlipayCallback", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String gtpaiAlipay() throws Exception {
        return (String)callBackService.callBack("out_trade_no", PayTypeEnum.GTPAI_ALIPAY.getValue());
    }
}
