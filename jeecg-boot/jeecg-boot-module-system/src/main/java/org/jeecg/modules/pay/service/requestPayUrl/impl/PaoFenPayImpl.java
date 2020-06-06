package org.jeecg.modules.pay.service.requestPayUrl.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.util.RedisUtil;
import org.jeecg.modules.api.service.ICommonApiService;
import org.jeecg.modules.api.service.impl.SfApiServiceImpl;
import org.jeecg.modules.exception.RRException;
import org.jeecg.modules.pay.entity.PaofenParam;
import org.jeecg.modules.pay.entity.OrderInfoEntity;
import org.jeecg.modules.pay.service.IOrderInfoEntityService;
import org.jeecg.modules.pay.service.factory.PayServiceFactory;
import org.jeecg.modules.pay.service.requestPayUrl.RequestPayUrl;
import org.jeecg.modules.system.entity.SysUser;
import org.jeecg.modules.system.service.ISysUserService;
import org.jeecg.modules.util.BaseConstant;
import org.jeecg.modules.util.HttpResult;
import org.jeecg.modules.util.HttpUtils;
import org.jeecg.modules.util.R;
import org.jeecg.modules.v2.entity.PayBusiness;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

/**
 * @title: 跑分支付
 * @Description:
 * @author:
 * @create: 2019-11-05 14:20
 */
@Service
@Slf4j
public class PaoFenPayImpl implements
    RequestPayUrl<OrderInfoEntity, String, String, String, String, PayBusiness, Object>, InitializingBean {
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private IOrderInfoEntityService orderInfoEntityService;
    @Autowired
    private ISysUserService userService;
    @Autowired
    private RequestUrlUtils utils;

    @Override
    public R requestPayUrl(OrderInfoEntity order, String userName, String url, String key, String callbackUrl,
        PayBusiness userBusiness) throws Exception {
        PaofenParam param = valueOf(order, userBusiness.getBusinessCode(), callbackUrl, userBusiness.getBusinessApiKey());
        String json = JSON.toJSONString(param);
        Map<String, Object> mapTypes = JSON.parseObject(json);
        log.info("===>请求跑分，获取支付链接，请求入参为：{}", json);
        HttpResult r = HttpUtils.doPost(url, mapTypes);
        log.info("===>请求跑分，获取支付链接,请求返回code为：{}，返回内容为：{}", r.getCode(), r.getBody());
        String payUrl = null;
        if (r != null && "200".equals(r.getCode().toString())) {
            String body = r.getBody();
            JSONObject result = JSONObject.parseObject(body);
            if ("200".equals(result.get("code").toString())) {
                // JSONObject data = (JSONObject) result.get("data");
                payUrl = (String)result.get("url").toString();
                log.info("===>跑分，支付链接地址为：{}", payUrl);
            } else {
                log.info("===>订单为：{}，请求跑分平台，获取支付链接，返回的code为：{}", order.getOrderId(), result.get("code").toString());
                throw new RRException("设备产码失败，请联系商户");
            }
        } else {
            log.info("===>===>订单为：{}，请求跑分平台，获取支付链接，请求返回的状态码为：{}", order.getOrderId(), r.getCode());
            throw new RRException("设备产码失败，请联系商户");
        }
        redisUtil.del(order.getOuterOrderId());
        if (StringUtils.isEmpty(payUrl)) {
            throw new RRException("跑分获取支付地址失败");
        }
        return R.ok().put("url", payUrl);
    }

    @Override
    public boolean orderInfoOk(OrderInfoEntity order, String url, PayBusiness userBusiness) throws Exception {
        return false;
    }

    @Override
    public boolean notifyOrderFinish(OrderInfoEntity order, String key, PayBusiness userBusiness, String url)
        throws Exception {
        return false;
    }

    @Autowired
    private ICommonApiService apiService;

    @Override
    public Object callBack(Object object) throws Exception {
        Map<String, Object> map = (Map<String, Object>)object;
        String orderId = (String)map.get("orderid");
        log.info("===>跑分回调四方，回调单号为：{}", orderId);
        SortedMap<Object, Object> params1 = new TreeMap<Object, Object>();
        params1.put("orderid", map.get("orderid"));
        params1.put("amount", map.get("amount"));
        params1.put("shid", map.get("shid"));

        OrderInfoEntity order = orderInfoEntityService.queryOrderInfoByOrderId(orderId);
        if (order == null) {
            log.info("===>跑分回调，根据订单号：{}，查询不到订单信息", orderId);
            return "订单不存在";
        }
        SysUser user = userService.getUserByName(order.getUserName());
        log.info("===>回调，跑分回调订单号：{},服务端sign串为：{}", orderId, params1);
        String payType = (String)map.get("class");
        if(payType.equals("1")){
            payType = "wx";
        }else if(payType.equals("2")) {
            payType = "zfb";
        }else{
            payType = "yl";
        }
        PayBusiness useBusinesses =
                apiService.findBusiness(order.getAgentUsername(), order.getPayType(), order.getProductCode());
        if (useBusinesses==null) {
            log.info("===>跑分回调，根据订单号：{}，查询不到代理信息", orderId);
            return "代理不存在";
        }
        String sign = signForInspiry(params1, useBusinesses.getBusinessApiKey());
        log.info("===>跑分回调，订单号：{},服务端sign为：{}", orderId, sign);
        if (!sign.equals(map.get("shkey"))) {
            log.info("===>跑分回调,签名验证不通过，入参的签名为：{},本地签名为：{}", map.get("sign").toString(), sign);
            return "签名验证失败";
        }
        // 假如当前同一个单号有多个请求进来，则，只针对一个线程进行处理，其余的不处理
        String exist = (String)redisUtil.get("callBack" + orderId);
        if (!StringUtils.isEmpty(exist)) {
            return "不能重复回调";
        }
        if (!redisUtil.setIfAbsent("callBack" + orderId, orderId, 30)) {
            return "不能重复回调";
        }

        if(map.get("status") != "1")
        {
            log.info("===>跑分回调状态不成功：状态{}", map.get("status"));
        }

        order.setStatus(BaseConstant.ORDER_STATUS_SUCCESS_NOT_RETURN);
        R r = orderInfoEntityService.notifyCustomer(order, user, payType);
        redisUtil.del("callBack" + orderId);
        if ("0".equals(r.get("code"))) {
            log.info("==>跑分回调四方结束，返回状态为success，订单号为：{}", orderId);
            return "success";
        } else {
            log.info("==>跑分回调四方结束，fail，订单号为：{}", orderId);
            return "fail";
        }
    }

    private PaofenParam valueOf(OrderInfoEntity order, String businessCode, String callBackUrl, String apiKey) {
        PaofenParam param = new PaofenParam();
        param.setShid(businessCode);
        param.setUrl(callBackUrl);
        param.setKey(apiKey);
        param.setOrderid(order.getOrderId());
        if (order.getPayType().equals(BaseConstant.REQUEST_PAOFEN_ALIPAY)) {
            param.setPay("zfb");
        } else if (order.getPayType().equals(BaseConstant.REQUEST_PAOFEN_YINLIAN)){
            param.setPay("yl");
        } else{
            param.setPay("wx");
        }

        // 金额要求保留2位小数
        BigDecimal amount = new BigDecimal(order.getSubmitAmount().toString()).setScale(2, RoundingMode.HALF_UP);
        param.setAmount(amount.toString());

        return param;
    }

    /**
     * 生成签名；
     *
     * @param params
     * @return
     */
    private String signForInspiry(Map params, String key) {
        StringBuilder sbkey = new StringBuilder();
        Set es = params.entrySet();
        Iterator it = es.iterator();

        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry)it.next();
            String k = (String)entry.getKey();
            Object v = entry.getValue();
            // 空值不传递，不参与签名组串
            if (null != v && !"".equals(v)) {
                sbkey.append(v);
            }
        }
        String sign = MD5(sbkey.toString());
        String shkey = sign + key;
        log.info("==>请求跑分挂马平台，生成的签名串为：{}", sbkey);
        // MD5加密,结果转换为大写字符
        shkey = MD5(shkey.toString());
        log.info("==>请求跑分挂马平台，生成的签名为：{}", shkey);
        return shkey;
    }

    /**
     * 对字符串进行MD5加密
     *
     * @param str
     *            需要加密的字符串
     * @return 小写MD5字符串 32位
     */
    private String MD5(String str) {
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("MD5");
            digest.update(str.getBytes());
            return new BigInteger(1, digest.digest()).toString(16);
        } catch (NoSuchAlgorithmException e) {
            log.info("==>请求跑分挂马平台,生成签名异常。异常信息为：{}", e);
        }
        return null;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        PayServiceFactory.register(BaseConstant.REQUEST_PAOFEN_WECHAT, this);
        PayServiceFactory.register(BaseConstant.REQUEST_PAOFEN_ALIPAY, this);
        PayServiceFactory.register(BaseConstant.REQUEST_PAOFEN_YINLIAN, this);
        PayServiceFactory.registerUrl(BaseConstant.REQUEST_PAOFEN_ALIPAY,
            utils.getRequestUrl(BaseConstant.REQUEST_PAOFEN_ALIPAY));
        PayServiceFactory.registerUrl(BaseConstant.REQUEST_PAOFEN_WECHAT,
            utils.getRequestUrl(BaseConstant.REQUEST_PAOFEN_WECHAT));
        PayServiceFactory.registerUrl(BaseConstant.REQUEST_PAOFEN_ALIPAY,
                utils.getRequestUrl(BaseConstant.REQUEST_PAOFEN_ALIPAY));
    }
}
