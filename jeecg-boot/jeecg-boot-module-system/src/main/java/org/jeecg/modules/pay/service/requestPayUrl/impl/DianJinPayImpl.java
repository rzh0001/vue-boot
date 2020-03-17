package org.jeecg.modules.pay.service.requestPayUrl.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.jeecg.common.util.RedisUtil;
import org.jeecg.modules.pay.entity.DianJinPayParam;
import org.jeecg.modules.pay.entity.OrderInfoEntity;
import org.jeecg.modules.pay.entity.QiPayParam;
import org.jeecg.modules.pay.entity.UserBusinessEntity;
import org.jeecg.modules.pay.service.IOrderInfoEntityService;
import org.jeecg.modules.pay.service.factory.PayServiceFactory;
import org.jeecg.modules.pay.service.requestPayUrl.RequestPayUrl;
import org.jeecg.modules.system.entity.SysUser;
import org.jeecg.modules.system.service.ISysUserService;
import org.jeecg.modules.util.BaseConstant;
import org.jeecg.modules.util.HttpResult;
import org.jeecg.modules.util.HttpUtils;
import org.jeecg.modules.util.R;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.Map;

/**
 * 点金术支付
 * @Author: wangjianbin
 * @Date: 2020/2/7 23:04
 */
@Slf4j
@Service
public class DianJinPayImpl implements RequestPayUrl<OrderInfoEntity, String, String, String, String, UserBusinessEntity,
    Object>, InitializingBean, ApplicationContextAware {
    public static final String DOMAIN = "http://39.98.76.217";
    public static final String key = "44843f1629e8b1142636fd799fb2e373b1feb096eb79bdcbeba8be8b1a65e752a2d08b88e0e0732a3b6a5f5572b1f7464e11f769d140d2675c74f9cdc99cfd2f1d33ecb9d0ffdb45df2e1665678c788c1a6ce5b69e539fdfb6c1daef8703c1e2";
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private IOrderInfoEntityService orderInfoEntityService;
    @Autowired
    private ISysUserService userService;
    @Autowired
    private RequestUrlUtils utils;
    /**
     * @param order        订单
     * @param userName     用户名
     * @param url          支付地址
     * @param key          秘钥
     * @param callbackUrl  回调地址
     * @param userBusiness
     * @return
     * @throws Exception
     */
    @Override
    public R requestPayUrl(OrderInfoEntity order, String userName, String url, String key, String callbackUrl,
        UserBusinessEntity userBusiness) throws Exception {
        DianJinPayParam param = new DianJinPayParam();
        param.setUid("6");
        param.setMerchantTransNo(order.getOrderId());
        param.setTotalAmount(order.getSubmitAmount().setScale(2, BigDecimal.ROUND_HALF_UP).toString());
        //1:支付宝二维码
        param.setPaymentType("1");
        param.setRemark(BaseConstant.REQUEST_DIANJIN_ALIPAY);
        StringBuilder sign = new StringBuilder();
        sign.append("merchantTransNo=").append(param.getMerchantTransNo())
            .append("&").append("paymentType=").append(param.getPaymentType())
            .append("&").append("totalAmount=").append(param.getTotalAmount())
            .append("&").append("uid=").append(param.getUid())
            .append("&").append("key=").append(userBusiness.getApiKey());
        log.info("点金术签名值sign={}",sign.toString());
        log.info("点金术签名值sign的MD5值={}",DigestUtils.md5Hex(sign.toString()));
        param.setSign(DigestUtils.md5Hex(sign.toString()));
        String jsonstring = JSON.toJSONString(param);
        Map map = JSON.parseObject(jsonstring);
        log.info("请求入参为：{}",map);
        HttpResult result = HttpUtils.doPost(url, map);
        //{"Status":1,"Data":"/Order/QueryQrCode?orderId=31"}
        log.info("点金术，请求二维码，返回内容为：{}",result.getBody());
        String body = result.getBody();
        JSONObject json = JSON.parseObject(body);
        redisUtil.del(order.getOuterOrderId());
        if(json != null){
            return R.ok().put("url", DOMAIN+json.getString("Data"));
        }
        return R.error("获取支付地址失败");
    }

    @Override public boolean orderInfoOk(OrderInfoEntity order, String url, UserBusinessEntity userBusiness)
        throws Exception {
        return false;
    }

    @Override
    public boolean notifyOrderFinish(OrderInfoEntity order, String key, UserBusinessEntity userBusiness, String url)
        throws Exception {
        return false;
    }

    @Override public Object callBack(Object object) throws Exception {
        Map<String, Object> map = (Map<String, Object>) object;
        String orderId =(String) map.get("transNo");
        log.info("点金术回调，回调单号为:{}",orderId);
        String amount = (String)map.get("amount");
        String signature = (String)map.get("signature");
        String remark = (String)map.get("remark");
        StringBuilder sign = new StringBuilder();
        sign.append(key).append(orderId).append(new BigDecimal(amount).setScale(2, BigDecimal.ROUND_HALF_UP).toString()).append(remark);
        log.info("点金术回调，sign = {}",sign.toString());
        String md5 = DigestUtils.md5Hex(sign.toString());
        log.info("点金术，md5：{}",md5);
        if(!signature.equals(md5)){
            log.info("点金术，回调失败，签名验证不通过，入参签名为：{}，本地签名为：{}",signature,md5);
            return 2;
        }
        //假如当前同一个单号有多个请求进来，则，只针对一个线程进行处理，其余的不处理
        String exist = (String) redisUtil.get("callBack"+orderId);
        if(!StringUtils.isEmpty(exist)){
            return 2;
        }
        if(!redisUtil.setIfAbsent("callBack"+orderId,orderId,30)){
            return 2;
        }
        OrderInfoEntity order = orderInfoEntityService.queryOrderInfoByOrderId(orderId);
        if(order == null){
            log.info("===>点金术回调，根据订单号：{}，查询不到订单信息",orderId);
            return 2;
        }
        //成功已返回的订单不能回调
        if ("2".equals(order.getStatus().toString()) ) {
            log.info("该订单已经回调过了，不能重复回调:{}", order.getOrderId());
            return 2;
        }
        order.setStatus(BaseConstant.ORDER_STATUS_SUCCESS_NOT_RETURN);
        SysUser user = userService.getUserByName(order.getUserName());
        String payType = (String) map.get("remark");
        R r = orderInfoEntityService.notifyCustomer(order,user,payType);
        redisUtil.del("callBack"+orderId);
        if("0".equals(r.get("code"))){
            log.info("==>点金术回调回调四方结束，返回信付为success，订单号为：{}",orderId);
            return 1;
        }else {
            log.info("==>点金术回调回调四方结束，fail，订单号为：{}",orderId);
            return 2;
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        PayServiceFactory.register(BaseConstant.REQUEST_DIANJIN_ALIPAY, this);
        PayServiceFactory.registerUrl(BaseConstant.REQUEST_DIANJIN_ALIPAY, utils.getRequestUrl(BaseConstant.REQUEST_DIANJIN_ALIPAY));
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {

    }
}
