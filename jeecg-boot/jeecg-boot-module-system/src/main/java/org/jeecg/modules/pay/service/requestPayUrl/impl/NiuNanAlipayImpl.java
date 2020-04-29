package org.jeecg.modules.pay.service.requestPayUrl.impl;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.system.vo.DictModel;
import org.jeecg.modules.pay.entity.OrderInfoEntity;
import org.jeecg.modules.pay.entity.UserBusinessEntity;
import org.jeecg.modules.pay.entity.NiuNanAlipayParam;
import org.jeecg.modules.pay.service.factory.PayServiceFactory;
import org.jeecg.modules.pay.service.requestPayUrl.RequestPayUrl;
import org.jeecg.modules.system.service.ISysDictService;
import org.jeecg.modules.util.BaseConstant;
import org.jeecg.modules.util.HttpResult;
import org.jeecg.modules.util.HttpUtils;
import org.jeecg.modules.util.R;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.security.MessageDigest;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class NiuNanAlipayImpl implements
    RequestPayUrl<OrderInfoEntity, String, String, String, String, UserBusinessEntity, Object>, InitializingBean {

    private static final String CALLBACK_URL = "/callBack/order/niuNanAlipay/orderNo";
    @Autowired
    public ISysDictService dictService;
    @Autowired
    private RequestUrlUtils utils;
    @Override
    public R requestPayUrl(OrderInfoEntity order, String userName, String url, String key, String callbackUrl,
        UserBusinessEntity userBusiness) throws Exception {
        NiuNanAlipayParam param = new NiuNanAlipayParam();
        param.setMerCode(userBusiness.getBusinessCode());
        param.setOrderNo(order.getOrderId());
        //金额单位为分
        String amount = order.getSubmitAmount().multiply(new BigDecimal("100")).toString();
        param.setOrderAmount(amount);
        param.setCallbackUrl(this.getDomain()+CALLBACK_URL);
        if(order.getPayType().equals(BaseConstant.REQUEST_NIUNAN_ONLINE_BANK)){
            //网银
            param.setPayType("10");
        }else {
            param.setPayType("2");
        }
        param.setProductDesc("alipay");
        StringBuilder buffer = new StringBuilder();
        buffer.append("callbackUrl=").append(param.getCallbackUrl())
            .append("&merCode=").append(param.getMerCode())
            .append("&orderAmount=").append(param.getOrderAmount())
            .append("&orderNo=").append(param.getOrderNo())
            .append("&payType=").append(param.getPayType())
            .append("&productDesc=").append(param.getProductDesc())
            .append(userBusiness.getApiKey());
        log.info("==>牛腩支付，签名为：{}",buffer.toString());
        String sign = md5Hash(buffer.toString());
        log.info("==>牛腩支付,MD5为：{}",sign);
        param.setSign(sign);
        String json = JSONObject.toJSONString(param);
        HttpResult result = HttpUtils.doPostJson(url,json);
        log.info("==>牛腩支付,返回值为：{}",result.getBody());
        String body = result.getBody();
        JSONObject resultJson = JSONObject.parseObject(body);
        String payUrl = null;
        if("SUCCESS".equals((String)resultJson.get("resultStatus"))){
            payUrl = (String)resultJson.get("resultMsg");
        }else {
            throw new RuntimeException((String)resultJson.get("resultMsg"));
        }
        return R.ok().put("url", payUrl);
    }
    /**
     * MD5
     * @param dataStr
     * @return
     */
    public  String md5Hash(String dataStr) {
        try {
            MessageDigest m = MessageDigest.getInstance("MD5");
            m.update(dataStr.getBytes("UTF8"));
            byte s[] = m.digest();
            String result = "";
            for (int i = 0; i < s.length; i++) {
                result += Integer.toHexString((0x000000FF & s[i]) | 0xFFFFFF00).substring(6);
            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
    private String getDomain(){
        String domain = null;
        List<DictModel> apiKey = dictService.queryDictItemsByCode(BaseConstant.DOMAIN);
        for (DictModel k : apiKey) {
            if (BaseConstant.DOMAIN.equals(k.getText())) {
                domain = k.getValue();
            }
        }
        return domain;
    }

    @Override
    public boolean orderInfoOk(OrderInfoEntity order, String url, UserBusinessEntity userBusiness)
        throws Exception {
        return false;
    }

    @Override
    public boolean notifyOrderFinish(OrderInfoEntity order, String key, UserBusinessEntity userBusiness, String url)
        throws Exception {
        return false;
    }

    @Override
    public Object callBack(Object object) throws Exception {
        return null;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        PayServiceFactory.register(BaseConstant.REQUEST_NIUNAN_ALIPAY, this);
        PayServiceFactory.registerUrl(BaseConstant.REQUEST_NIUNAN_ALIPAY, utils.getRequestUrl(BaseConstant.REQUEST_NIUNAN_ALIPAY));
        PayServiceFactory.register(BaseConstant.REQUEST_NIUNAN_ONLINE_BANK, this);
        PayServiceFactory.registerUrl(BaseConstant.REQUEST_NIUNAN_ONLINE_BANK, utils.getRequestUrl(BaseConstant.REQUEST_NIUNAN_ALIPAY));
    }
}
