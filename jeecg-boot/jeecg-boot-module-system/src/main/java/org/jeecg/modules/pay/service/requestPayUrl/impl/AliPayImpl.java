package org.jeecg.modules.pay.service.requestPayUrl.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.modules.exception.RRException;
import org.jeecg.modules.pay.entity.AliPayCallBackParam;
import org.jeecg.modules.pay.entity.OrderInfoEntity;
import org.jeecg.modules.pay.entity.QueryOrderStatusResult;
import org.jeecg.modules.pay.entity.UserBusinessEntity;
import org.jeecg.modules.pay.service.IOrderInfoEntityService;
import org.jeecg.modules.pay.service.requestPayUrl.RequestPayUrl;
import org.jeecg.modules.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
/**
 * 支付宝转卡请求挂码
 */
@Service
@Slf4j
public class AliPayImpl implements RequestPayUrl<OrderInfoEntity, String, String, String,String,UserBusinessEntity> {

    @Autowired
    private IOrderInfoEntityService orderInfoEntityService;
    @Override
    public R requestPayUrl(OrderInfoEntity order, String userName, String url, String key,String callbackUrl,UserBusinessEntity userBusinessEntity) throws Exception {
        String type = null;
        String payType = null;
        if(userBusinessEntity.getChannelCode().equals(BaseConstant.REQUEST_ALI_ZZ)){
            type = "alipay_auto";
            payType=BaseConstant.REQUEST_ALI_ZZ;
        }else if(userBusinessEntity.getChannelCode().equals(BaseConstant.REQUEST_ALI_BANK)){
            type = "jdpay_auto";
            payType=BaseConstant.REQUEST_ALI_BANK;
        }else if(userBusinessEntity.getChannelCode().equals(BaseConstant.REQUEST_WECHAT_BANK)){
            type = "wechat_auto";
            payType = BaseConstant.REQUEST_WECHAT_BANK;
        }else if(userBusinessEntity.getChannelCode().equals(BaseConstant.REQUEST_ALI_TT)){
            type = "alipay_auto";
            payType=BaseConstant.REQUEST_ALI_TT;
        }
        if(StringUtils.isEmpty(type)){
            throw new RRException("请求支付宝通道不对");
        }
        AliPayCallBackParam param = structuralAliParam(order, "text", type, "3", "2",
                payType, userName,callbackUrl,key);
        if (StringUtils.isBlank(url)) {
            throw new RRException("未配置支付宝回调地址，请联系管理员配置回调地址");
        }
        log.info("四方回调挂马平台，加密前数据，url:{};param:{}", url, JSON.toJSONString(param));

        String data = AES128Util.encryptBase64(JSON.toJSONString(param), key);

        JSONObject p = new JSONObject();
        p.put("data", data);
        log.info("四方回调挂马平台，加密后数据，url:{};param:{}", url, p.toJSONString());
        HttpResult result = HttpUtils.doPostJson(url, p.toJSONString());
        String payUrl = null;
        if (result.getCode() == BaseConstant.SUCCESS) {
            if (StringUtils.isNotBlank(result.getBody())) {
                log.info("四方回调挂马平台成功，返回信息：{}", result.getBody());
                JSONObject r = JSON.parseObject(result.getBody());
                if (r != null) {
                    if ("200".equals(r.get("code").toString())) {
                        payUrl = (String) r.get("msg");
                        log.info("===请求挂码平台，返回支付链接为:{}", payUrl);
                    } else {
                        throw new RRException("四方回调挂马平台失败,订单创建失败：" + result.getBody());
                    }
                } else {
                    throw new RRException("四方回调挂马平台失败,订单创建失败：" + result.getBody());
                }
            } else {
                throw new RRException("四方回调挂马平台失败,订单创建失败：" + result.getBody());
            }
        } else {
            throw new RRException("四方回调挂马平台失败,订单创建失败：" + result.getBody());
        }
        if(StringUtils.isEmpty(payUrl)){
            throw new RRException("设备产码失败，请联系商户，查看设置状态");
        }
        return R.ok().put("url", payUrl);
    }

    @Override
    public boolean orderInfoOk(OrderInfoEntity order, String url, UserBusinessEntity userBusinessEntity) throws IOException, URISyntaxException {
        Map<String, String> data = new HashMap();
        data.put("id", order.getOrderId());
        String result = HttpUtils.doGet(url, data);
        if (StringUtils.isNotBlank(result)) {
            log.info("订单状态查询成功，返回信息为：{}", result);
            QueryOrderStatusResult orderStatusResult = JSONObject.parseObject(result, QueryOrderStatusResult.class);
            if (orderStatusResult != null && orderStatusResult.getCode() == BaseConstant.SUCCESS) {
                if (BaseConstant.QUERY_ORDER_STATUS_SUCCESS.equals(orderStatusResult.getData().getStatus())) {
                    order.setStatus(BaseConstant.ORDER_STATUS_SUCCESS_NOT_RETURN);
                    order.setPaymentAmount(new BigDecimal(orderStatusResult.getData().getAmount()));
                    orderInfoEntityService.updateById(order);
                    return true;
                }
            } else {
                return false;
            }
        } else {
            log.info("订单状态查询失败，单号为：{}", order.getOrderId());
            return false;
        }
        return false;
    }

    @Override
    public boolean notifyOrderFinish(OrderInfoEntity order, String key, UserBusinessEntity userBusiness,String url) throws Exception {
        JSONObject param = new JSONObject();
        param.put("orderId",order.getOrderId());
        log.info("==>手动补单，回调挂马平台url：{}，param:{}",url,param.toJSONString());
        String data = AES128Util.encryptBase64(param.toJSONString(), key);
        JSONObject requestParam = new JSONObject();
        requestParam.put("data",data);
        log.info("==>手动补单，回调挂马平台，加密后的入参为：{}",requestParam.toJSONString());
        HttpResult result = HttpUtils.doPostJson(url, requestParam.toJSONString());
        log.info("==>手动补单，挂马平台返回状态码为：{}；内容为为：{}",result.getCode(),result.getBody());
        if(result.getCode() == BaseConstant.SUCCESS ){
            JSONObject r = JSON.parseObject(result.getBody());
            if("200".equals(r.get("code").toString())){
                return true;
            }
        }
        return false;
    }


    private AliPayCallBackParam structuralAliParam(OrderInfoEntity order, String contentType, String thoroughfare,
                                                   String type, String robin, String payType, String userName,String callbackUrl,String key) throws Exception {
        AliPayCallBackParam param = new AliPayCallBackParam();
        param.setAccount_id(order.getBusinessCode());
        param.setContent_type(contentType);
        param.setThoroughfare(thoroughfare);
        param.setType(type);
        param.setOut_trade_no(order.getOrderId());
        param.setRobin(robin);
        param.setKeyId("");
        param.setAmount(order.getSubmitAmount().toString());
        param.setCallback_url(callbackUrl);
        param.setPayType(payType);
        param.setUserName(userName);
        param.setSign(sign(order,key));
        return param;
    }
    private String sign(OrderInfoEntity order,String key) throws Exception {
        StringBuilder sign = new StringBuilder();
        sign.append(key).append(order.getSubmitAmount()).append(order.getOrderId());
        log.info("===支付宝签名内容===》：{}",sign.toString());
        return DigestUtils.md5Hex(sign.toString());
    }
}
