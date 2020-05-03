package org.jeecg.modules.pay.service.callBackServiceImpl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.jeecg.modules.api.constant.PayTypeEnum;
import org.jeecg.modules.pay.entity.OrderInfoEntity;
import org.jeecg.modules.pay.entity.UserBusinessEntity;
import org.jeecg.modules.pay.externalUtils.antUtil.GtpaiUtil;
import org.jeecg.modules.pay.externalUtils.antUtil.YitongUtil;
import org.jeecg.modules.pay.service.AbstractCallBack;
import org.jeecg.modules.pay.service.ICallBackService;
import org.jeecg.modules.pay.service.IOrderInfoEntityService;
import org.jeecg.modules.pay.service.IUserBusinessEntityService;
import org.jeecg.modules.pay.service.factory.CallBackServiceFactory;
import org.jeecg.modules.util.BaseConstant;
import org.jeecg.modules.util.HttpResult;
import org.jeecg.modules.util.HttpUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
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
    @Autowired
    private IUserBusinessEntityService businessService;
    @Override
    public Object reply(Map<String, Object> map,String apiKey) throws Exception {
        log.info("==>易通支付，回调参数为：{}",map);
        String sign = (String)map.get("sign");
        map.remove("sign");
        String localSign = YitongUtil.generateSignature(map,apiKey);
        if(!localSign.equals(sign)){
            log.info("==>易通支付，回调签名为：{}，本地签名为：{}",sign,localSign);
            return "签名验证不通过";
        }
        return "success";
    }

    @Override
    public Map<String, Object> getCallBackParam(Map<String, Object> map) {
        return map;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        CallBackServiceFactory.register(PayTypeEnum.YITONG_ALIPAY.getValue(),this);
    }

    @Override
    public boolean checkOrderStatusIsOK(Map<String, Object> map, String apiKey) throws Exception {
        OrderInfoEntity order = orderInfoEntityService.queryOrderInfoByOrderId((String)map.get("sh_order"));
        //查询订单状态
        TreeMap<String, Object> queryMap =  new TreeMap<String,Object>();
        queryMap.put("mch_id", order.getBusinessCode());
        queryMap.put("out_order_sn",  map.get("sh_order"));
        queryMap.put("time", map.get("time"));
        String querySign = YitongUtil.generateSignature(queryMap,apiKey);
        queryMap.put("sign", querySign);

        log.info("==>易通支付支付宝，查询签名为：{} 查询参数为：{}",querySign, queryMap);
        HttpResult result = HttpUtils.doPost("http://pay.ccloudpay.com/?c=Pay&a=query", queryMap);
        String body = result.getBody();
        log.info("==>易通支付支付宝，查询返回结果为：{}",body);
        JSONObject queryRet = JSON.parseObject(body);
        String strData = queryRet.getString("data");
        JSONObject bodyData = JSON.parseObject(strData);
        String strStatus = bodyData.getString("status");
        if(!strStatus.equals("9")){
            log.info("==>易通支付，查询订单状态失败");
            return false;
        }
        return true;
    }
}
