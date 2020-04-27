package org.jeecg.modules.pay.service.callBackServiceImpl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.jeecg.modules.pay.entity.OrderInfoEntity;
import org.jeecg.modules.pay.externalUtils.antUtil.GtpaiUtil;
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
 * @Author: wangjianbin
 * @Date: 2020/4/24 21:58
 */
@Service
public class CallBackGtpaiAlipayImpl extends AbstractCallBack implements InitializingBean {

    @Override
    public Object reply(Map<String, Object> map,String apiKey) throws Exception {
        log.info("==>GT派支付，回调参数为：{}",map);
        String json = (String)map.get("reqData");
        Map<String,Object> param = JSON.parseObject(json);
        String sign = (String)param.get("sign");
        String orderNo =(String) param.get("out_trade_no");
        param.remove("sign");
        Map<String, Object> sortedMap = new TreeMap<String, Object>(param);
        String localSign = GtpaiUtil.generateSignature(sortedMap,apiKey);
        if(!localSign.equals(sign)){
            log.info("==>GT派支付，回调签名为：{}，本地签名为：{}",sign,localSign);
            return "签名验证不通过";
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
    public boolean checkOrderStatusIsOK(Map<String, Object> param,String apiKey) throws Exception {
        //查询订单状态
        TreeMap<String, Object> queryMap =  new TreeMap<String,Object>();
        queryMap.put("mch_id", (String)param.get("mch_id"));
        String orderNo =(String) param.get("out_trade_no");
        queryMap.put("out_trade_no", orderNo);
        queryMap.put("store_id", (String)param.get("store_id"));
        String querySign = GtpaiUtil.generateSignature(queryMap,apiKey);
        queryMap.put("sign", querySign);
        String paramString = JSON.toJSONString(queryMap);
        TreeMap<String, Object> tmpMap =  new TreeMap<String,Object>();
        tmpMap.put("reqData", paramString);
        log.info("==>GT派支付支付宝，查询签名为：{} 查询参数为：{}",querySign, tmpMap);
        HttpResult result = HttpUtils.doPost("http://gttffp.com:8089/zhifpops/shOrderQuery", tmpMap);
        String body = result.getBody();
        log.info("==>GT派支付支付宝，查询返回结果为：{}",body);
        JSONObject queryRet = JSON.parseObject(body);
        if(!queryRet.getString("ret_code").equals("00")){
            log.info("==>GT派支付，查询订单状态失败");
            return false;
        }
        return true;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        CallBackServiceFactory.register(BaseConstant.REQUEST_GTPAI_ALIPAY,this);
    }
}
