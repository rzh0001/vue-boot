package org.jeecg.modules.pay.service.callBackServiceImpl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.jeecg.modules.pay.entity.OrderInfoEntity;
import org.jeecg.modules.pay.externalUtils.antUtil.GtpaiUtil;
import org.jeecg.modules.pay.externalUtils.antUtil.YitongUtil;
import org.jeecg.modules.pay.service.AbstractCallBack;
import org.jeecg.modules.pay.service.ICallBackService;
import org.jeecg.modules.pay.service.IOrderInfoEntityService;
import org.jeecg.modules.pay.service.factory.CallBackServiceFactory;
import org.jeecg.modules.util.BaseConstant;
import org.jeecg.modules.util.HttpResult;
import org.jeecg.modules.util.HttpUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.TreeMap;

/**
 * @Author: heihei
 * @Date: 2020/4/26 21:58
 */
@Service
public class CallBackYitongAlipayImpl extends AbstractCallBack implements InitializingBean {
    @Autowired
    ICallBackService callBackService;
    @Autowired
    private IOrderInfoEntityService orderInfoEntityService;

    @Override
    public Object reply(Map<String, Object> map,String apiKey) throws Exception {
        log.info("==>易通支付，回调参数为：{}",map);
        String sign = (String)map.get("sign");
        String orderNo =(String) map.get("sh_order");
        map.remove("sign");
        String localSign = YitongUtil.generateSignature(map,apiKey);
        if(!localSign.equals(sign)){
            log.info("==>易通支付，回调签名为：{}，本地签名为：{}",sign,localSign);
            return "签名验证不通过";
        }

        //查询订单状态
        TreeMap<String, Object> queryMap =  new TreeMap<String,Object>();
        queryMap.put("mch_id", (String)map.get("mch_id"));
        queryMap.put("out_order_sn", orderNo);
        queryMap.put("time", (String)map.get("time"));
        String querySign = GtpaiUtil.generateSignature(queryMap,apiKey);
        queryMap.put("sign", querySign);

        log.info("==>易通支付支付宝，查询签名为：{} 查询参数为：{}",querySign, queryMap);
        HttpResult result = HttpUtils.doPost("http://pay.ccloudpay.com/?c=Pay&a=query", queryMap);
        String body = result.getBody();
        log.info("==>易通支付支付宝，查询返回结果为：{}",body);
        JSONObject queryRet = JSON.parseObject(body);
        String strData = queryRet.getString("data");
        String strCode = queryRet.getString("code");
        if (strCode.equals("1")){
            log.info("==>易通下单返回失败，返回码：{}",strCode);
            return "易通支付订单查询失败";
        }
        JSONObject bodyData = JSON.parseObject(strData);
        String strStatus = bodyData.getString("status");
        if(!strStatus.equals("9")){
            log.info("==>易通支付，查询订单状态失败");
            return "查询订单状态失败";
        }
        OrderInfoEntity order = orderInfoEntityService.queryOrderInfoByOrderId(orderNo);
        if (order == null || order.getStatus() == 2) {
            log.info("==>无订单信息，订单号为：{}",orderNo);
            return "非法访问";
        }
        return "success";
    }

    @Override
    public Map<String, Object> getCallBackParam(Map<String, Object> map) {
        String json = (String)map.get("reqData");
        Map<String,Object> param = JSON.parseObject(json);
        return param;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        CallBackServiceFactory.register(BaseConstant.REQUEST_YITONG_ALIPAY,this);
    }
}
