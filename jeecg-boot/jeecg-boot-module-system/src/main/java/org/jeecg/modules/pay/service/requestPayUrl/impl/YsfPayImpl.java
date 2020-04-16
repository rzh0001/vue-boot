package org.jeecg.modules.pay.service.requestPayUrl.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.common.util.RedisUtil;
import org.jeecg.common.util.encryption.AES128Util;
import org.jeecg.modules.exception.RRException;
import org.jeecg.modules.pay.entity.OrderInfoEntity;
import org.jeecg.modules.pay.entity.UserBusinessEntity;
import org.jeecg.modules.pay.entity.YsfQueryOrderResult;
import org.jeecg.modules.pay.service.IOrderInfoEntityService;
import org.jeecg.modules.pay.service.factory.PayServiceFactory;
import org.jeecg.modules.pay.service.requestPayUrl.RequestPayUrl;
import org.jeecg.modules.util.*;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@Slf4j
public class YsfPayImpl implements RequestPayUrl<OrderInfoEntity, String, String, String, String, UserBusinessEntity, Object>, InitializingBean {
    @Autowired
    private IOrderInfoEntityService orderInfoEntityService;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private RequestUrlUtils utils;

    @Override
    public R requestPayUrl(OrderInfoEntity order, String userName, String url, String key, String callbackUrl, UserBusinessEntity userBusinessEntity) throws Exception {
        String ysfKey = userBusinessEntity.getApiKey();
        if (StringUtils.isBlank(ysfKey)) {
            log.info("云闪付通道未配置apikey");
            throw new RRException("云闪付通道未配置apikey");
        }
        String[] keys = ysfKey.split("=");
        if (keys.length != 2) {
            log.info("云闪付通道配置apikey规则不对");
            throw new RRException("云闪付通道未配置apikey");
        }
        String md5Key = keys[0];
        String aesKey = keys[1];
        String param = structuralYsfParam(order, md5Key, aesKey, order.getBusinessCode(), userName, callbackUrl, userBusinessEntity.getChannelCode());
        String payUrl = ysfCallBack(param, url);
        if (StringUtils.isEmpty(payUrl)) {
            throw new RRException("设备产码失败，请联系商户，查看设置状态");
        }
        redisUtil.del(order.getOuterOrderId());
        return R.ok().put("url", payUrl);
    }

    @Override
    public boolean orderInfoOk(OrderInfoEntity order, String url, UserBusinessEntity userBusinessEntity) throws Exception {
        String apiKey = userBusinessEntity.getApiKey();
        if (StringUtils.isBlank(apiKey)) {
            log.info("云闪付通道未配置apikey");
            throw new RRException("云闪付通道未配置apikey");
        }
        String[] keys = apiKey.split("=");
        if (keys.length != 2) {
            log.info("云闪付通道配置apikey规则不对");
            throw new RRException("云闪付通道未配置apikey");
        }
        String md5Key = keys[0];
        String aesKey = keys[1];
        JSONObject param = new JSONObject();
        param.put("agentcode", userBusinessEntity.getBusinessCode());
        long time = System.currentTimeMillis();
        param.put("timestamp", time);
        //agentcode+timestamp+agententity.getMd5key()
        String sign = DigestUtils.md5Hex(userBusinessEntity.getBusinessCode() + time + md5Key);
        param.put("sign", sign);
        JSONObject data = new JSONObject();
        data.put("orderids", order.getOrderId());
        data.put("isNew", "1");
        String datastr = AES128Util.encryptBase64(data.toJSONString(), aesKey);
        param.put("data", datastr);
        HttpResult r = HttpUtils.doPostJson(url, param.toJSONString());
        if (r.getCode() == BaseConstant.SUCCESS) {
            log.info("===订单查询返回结果：{}", r.getBody());
            YsfQueryOrderResult orderStatusResult = JSONObject.parseObject(r.getBody(), YsfQueryOrderResult.class);
            if (orderStatusResult != null && orderStatusResult.getCode() == 0) {
                //云闪付状态是成功已返回或成功未返回才能回调
                if (BaseConstant.ORDER_STATUS_SUCCESS_NOT_RETURN == orderStatusResult.getData().get(0).getStatus() ||
                        BaseConstant.ORDER_STATUS_SUCCESS == orderStatusResult.getData().get(0).getStatus()) {
                    order.setStatus(BaseConstant.ORDER_STATUS_SUCCESS_NOT_RETURN);
                    order.setPaymentAmount(new BigDecimal(orderStatusResult.getData().get(0).getApplyamount()));
                    orderInfoEntityService.updateById(order);
                    return true;
                }
            } else {
                return false;
            }
        }
        return false;
    }

    @Override
    public boolean notifyOrderFinish(OrderInfoEntity order, String key, UserBusinessEntity userBusiness, String url) throws Exception {
        JSONObject param = new JSONObject();
        param.put("orderId", order.getOrderId());
        log.info("==>手动补单，回调挂马平台url：{}，param:{}", url, param.toJSONString());
        String apiKey = userBusiness.getApiKey();
        String[] keys = apiKey.split("=");
        if (keys.length != 2) {
            log.info("云闪付通道配置apikey规则不对");
            throw new RRException("云闪付通道未配置apikey,通道为：" + order.getPayType());
        }
        String md5Key = keys[0];
        String aesKey = keys[1];
        String data = AES128Util.encryptBase64(param.toJSONString(), aesKey);
        JSONObject requestParam = new JSONObject();
        requestParam.put("data", data);
        requestParam.put("businessCode", userBusiness.getBusinessCode());
        log.info("==>手动补单，回调挂马平台，加密后的入参为：{}", requestParam.toJSONString());
        HttpResult result = HttpUtils.doPostJson(url, requestParam.toJSONString());
        log.info("==>手动补单，挂马平台返回状态码为：{}；内容为为：{}", result.getCode(), result.getBody());
        if (result.getCode() == BaseConstant.SUCCESS) {
            JSONObject r = JSON.parseObject(result.getBody());
            if ("200".equals(r.get("code").toString())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public R callBack(Object object) throws Exception {
//        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
//        String json = getParm(request);
//        JSONObject reqobj = JSONObject.parseObject(json);
        return null;
    }


    private String structuralYsfParam(OrderInfoEntity order, String md5Key, String aesKey, String agentCode,
                                      String userName, String innerCallBackUrl, String channelCode) {
        Long timestamp = System.currentTimeMillis() / 1000;
        String sign = DigestUtils.md5Hex(agentCode + timestamp + md5Key);

        JSONObject reqdata = new JSONObject(true);
        int channel = 3;
        switch (channelCode) {
            case BaseConstant.REQUEST_ALI_ZZ:
                channel = 5;
                break;
            case BaseConstant.REQUEST_ALI_BANK:
                channel = 6;
                break;
            case BaseConstant.REQUEST_WECHAT_BANK:
                channel = 7;
                break;
            case BaseConstant.REQUEST_INTERNET_BANK:
                channel = 8;
                break;
            case BaseConstant.REQUEST_COPY_ALI_BANK:
                channel = 9;
                break;
            default:
                channel = 3;
                break;
        }
        reqdata.put("orderchannel", channel);
        reqdata.put("trscode", "dynamicunionpay");
        reqdata.put("agentorderid", order.getOrderId());
        reqdata.put("applyamount", order.getSubmitAmount());
        reqdata.put("web_username", userName);
        reqdata.put("clienttype", "H5");
        //这里的回调地址，是挂马回调四方的地址，配置在数据字典中
        reqdata.put("callbackurl", innerCallBackUrl);
        log.info("===请求云闪付挂码平台的data加密之前的数据为：{}", reqdata.toJSONString());
        String data = AES128Util.encryptBase64(reqdata.toJSONString(), aesKey);
        JSONObject reqobj = new JSONObject();
        reqobj.put("agentcode", agentCode);
        reqobj.put("timestamp", timestamp);
        reqobj.put("sign", sign);
        reqobj.put("data", data);
        return reqobj.toJSONString();
    }

    private String ysfCallBack(String param, String url) throws Exception {
        log.info("云闪付请求挂马平台，请求url:{};入参为：{}",url, param);
        if (StringUtils.isBlank(url)) {
            throw new RRException("未配置云闪付回调地址，请联系管理员配置回调地址");
        }
        String resultUrl = null;
        for (int i = 0; i < 3; i++) {
            HttpResult result = HttpUtils.doPostJson(url, param);
            log.info("==>第：{} 次请求云闪付挂马平台", i);
            if (result.getCode() == BaseConstant.SUCCESS) {
                log.info("云闪付挂马平台返回信息为：{}", result.getBody());
                JSONObject r = JSON.parseObject(result.getBody());
                resultUrl = (String) r.get("payurl");
                log.info("===请求挂码平台，返回支付链接为:{}", resultUrl);
                if (StringUtils.isNotBlank(resultUrl)) {
                    break;
                }
            } else {
                throw new RRException("云闪付请求挂马平台，四方回调挂马平台失败,订单创建失败：" + param);
            }
        }
        return resultUrl;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        PayServiceFactory.register(BaseConstant.REQUEST_YSF, this);
        PayServiceFactory.register(BaseConstant.REQUEST_COPY_ALI_BANK, this);
        PayServiceFactory.registerUrl(BaseConstant.REQUEST_YSF, utils.getRequestUrl(BaseConstant.REQUEST_YSF));
        PayServiceFactory.register(BaseConstant.REQUEST_ALI_BANK, this);
        PayServiceFactory.register(BaseConstant.REQUEST_ALI_ZZ, this);
        PayServiceFactory.register(BaseConstant.REQUEST_WECHAT_BANK, this);
        PayServiceFactory.register(BaseConstant.REQUEST_INTERNET_BANK, this);
        PayServiceFactory.registerUrl(BaseConstant.REQUEST_ALI_BANK, utils.getRequestUrl(BaseConstant.REQUEST_ALI_BANK));
        PayServiceFactory.registerUrl(BaseConstant.REQUEST_COPY_ALI_BANK, utils.getRequestUrl(BaseConstant.REQUEST_COPY_ALI_BANK));
        PayServiceFactory.registerUrl(BaseConstant.REQUEST_ALI_ZZ, utils.getRequestUrl(BaseConstant.REQUEST_ALI_ZZ));
        PayServiceFactory.registerUrl(BaseConstant.REQUEST_WECHAT_BANK, utils.getRequestUrl(BaseConstant.REQUEST_WECHAT_BANK));
        PayServiceFactory.registerUrl(BaseConstant.REQUEST_INTERNET_BANK, utils.getRequestUrl(BaseConstant.REQUEST_INTERNET_BANK));
    }
}
