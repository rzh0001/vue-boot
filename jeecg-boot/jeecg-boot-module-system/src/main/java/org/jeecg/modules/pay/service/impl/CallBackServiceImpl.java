package org.jeecg.modules.pay.service.impl;

import cn.hutool.crypto.SecureUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.jeecg.common.util.RedisUtil;
import org.jeecg.modules.pay.entity.OrderInfoEntity;
import org.jeecg.modules.pay.service.ICallBackService;
import org.jeecg.modules.pay.service.IOrderInfoEntityService;
import org.jeecg.modules.pay.service.requestPayUrl.impl.RequestUrlUtils;
import org.jeecg.modules.system.entity.SysUser;
import org.jeecg.modules.system.service.ISysUserService;
import org.jeecg.modules.util.BaseConstant;
import org.jeecg.modules.util.R;
import org.jeecg.modules.util.RequestHandleUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.security.MessageDigest;
import java.util.Map;

/**
 * @Author: wangjianbin
 * @Date: 2020/3/15 22:48
 */
@Service
@Slf4j
public class CallBackServiceImpl implements ICallBackService {
    @Autowired
    private IOrderInfoEntityService orderInfoEntityService;
    @Autowired
    private ISysUserService userService;
    @Override
    public String callBack4niuNanAlipay() throws Exception{
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        Object param = RequestHandleUtil.getReqParam(request);
        Map<String, Object> map = (Map<String, Object>) param;
        log.info("==>牛腩支付 回调，回调参数为：{}",map);
        String merCode = (String)map.get("merCode");
        String orderNo = (String)map.get("orderNo");
        String orderAmount = (String)map.get("orderAmount");
        String payDate = (String)map.get("payDate");
        String payCompletionDate = (String)map.get("payCompletionDate");
        String resultCode = (String)map.get("resultCode");
        String resultStatus = (String)map.get("resultStatus");
        String resultMsg = (String)map.get("resultMsg");
        String resultTime = (String)map.get("resultTime");
        String sign = (String)map.get("sign");
        StringBuilder md5buffer = new StringBuilder();
        md5buffer.append("merCode=").append(merCode)
            .append("&orderAmount=").append(orderAmount)
            .append("&orderNo=").append(orderNo)
            .append("&payCompletionDate=").append(payCompletionDate)
            .append("&payDate=").append(payDate)
            .append("&resultCode=").append(resultCode)
            .append("&resultStatus=").append(resultStatus)
            .append("&resultTime=").append(resultTime);
        if(StringUtils.isNotBlank(resultMsg)){
            md5buffer.append("&resultMsg=").append(resultMsg);
        }
        md5buffer.append("156f65ad6fde4f11b6be73552f143bdb");
        log.info("==>牛腩支付 回调，签名：{}",md5buffer.toString());
        String localSign = this.md5Hash(md5buffer.toString());
        log.info("==>牛腩支付 回调，MD5 :{}",localSign);
        if(!localSign.equals(sign)){
            log.info("==>签名验证不通过，入参sign:{},本地：{}",sign,localSign);
            return "fail";
        }
        return this.notify(orderNo);
    }

    @Override
    public String callBackTengFeiAlipay() throws Exception {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        Object param = RequestHandleUtil.getReqParam(request);
        Map<String, Object> map = (Map<String, Object>) param;
        log.info("==>腾飞支付 回调，回调参数为：{}",map);
        String openid = (String)map.get("openid");
        String orderNo = (String)map.get("orderNo");
        String tradeNo = (String)map.get("tradeNo");
        String orderPrice = (String)map.get("orderPrice");
        String sign = (String)map.get("sign");
        StringBuilder md5buffer = new StringBuilder();
        md5buffer.append("openid=").append(openid)
        .append("&orderNo=").append(orderNo)
        .append("&orderPrice=").append(orderPrice)
         .append("&tradeNo=").append(tradeNo).append("&key=b5b71fc279531ce2d7ffc57993fa3a5e");
        log.info("==>腾飞支付 签名：{}",md5buffer.toString());
        String localSign = SecureUtil.md5(md5buffer.toString()).toUpperCase();
        if(!sign.equals(localSign)){
            log.info("==>签名不匹配，localSign：{}；sign：{}",localSign,sign);
            return "fail";
        }
        return this.notify(orderNo);
    }

    private String notify(String orderNo) throws Exception {
        OrderInfoEntity order = orderInfoEntityService.queryOrderInfoByOrderId(orderNo);
        if (order == null || order.getStatus()==2) {
            return "非法访问";
        }
        order.setStatus(BaseConstant.ORDER_STATUS_SUCCESS_NOT_RETURN);
        SysUser user = userService.getUserByName(order.getUserName());
        R r = orderInfoEntityService.notifyCustomer(order, user, BaseConstant.REQUEST_NIUNAN_ALIPAY);
        if ("0".equals(r.get("code"))) {
            return "success";
        } else {
            return "fail";
        }
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
}
