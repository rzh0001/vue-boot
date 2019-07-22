package org.jeecg.modules.pay.service.impl;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.modules.pay.entity.OrderInfoEntity;
import org.jeecg.modules.pay.service.IOrderInfoService;
import org.jeecg.modules.system.entity.SysUser;
import org.jeecg.modules.system.service.ISysUserService;
import org.jeecg.modules.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @title:
 * @Description:
 * @author: wangjb
 * @create: 2019-07-22 10:36
 */
@Slf4j
@Service
public class OrderInfoServiceImpl implements IOrderInfoService {

    @Autowired
    private ISysUserService userService;

    private R checkParam(JSONObject reqobj){
        Assert.isBlank(reqobj.getString(BaseConstant.USER_NAME), "username不能为空");
        Assert.isBlank(reqobj.getString(BaseConstant.SIGN), "sign不能为空");
        Assert.isBlank(reqobj.getString(BaseConstant.DATA), "data不能为空");
        //用户账号登录名
        String userName=reqobj.getString(BaseConstant.USER_NAME);
        //时间戳
        Long timestamp=reqobj.getLong(BaseConstant.TIMESTAMP);
        //MD5值
        String sign=reqobj.getString(BaseConstant.SIGN);
        //RSA 加密data
        String data=reqobj.getString(BaseConstant.DATA);
        try{
            SysUser user = userService.getUserByName(userName);
            String localSgin= DigestUtils.md5Hex(timestamp+user.getSalt());
            if(!localSgin.equals(sign)) {
                return R.error("签名验证不通过");
            }
            String dataStr= AES128Util.decryptBase64(data, user.getSalt());
            if(StringUtils.isBlank(dataStr)){
                return R.error("数据解密出现异常");
            }
            JSONObject dataobj=JSONObject.parseObject(dataStr);
            if(dataobj.isEmpty()){
                return R.error("请求参数不能为空");
            }
            String orderId = dataobj.getString("orderId");
            if(StringUtils.isBlank(orderId)){
                return R.error("请求参数不能为空");
            }
            return R.ok().put("orderId",orderId);
        }catch (Exception e){
            log.info("参数解析异常，异常信息为:{}",e);
            return R.error("参数解密出现异常");
        }
    }
    @Override
    public R queryOrderInfo(JSONObject reqobj) {
        try{
            R checkParam = checkParam(reqobj);
            if("0".equals(checkParam.get("code").toString())){
                String orderId = (String) checkParam.get("orderId");
                OrderInfoEntity order = queryOrderInfoByOrderId(orderId);
                return null;
            }else{
                return checkParam;
            }
        }catch (Exception e){
            log.info("订单查询异常，异常信息为:{}",e);
            return R.error("订单查询异常");
        }
    }

    @Override
    public R callback(JSONObject reqobj) {
        try{
            R checkParam = checkParam(reqobj);
            if("0".equals(checkParam.get("code").toString())){
                String orderId = (String) checkParam.get("orderId");
                OrderInfoEntity order = queryOrderInfoByOrderId(orderId);
                if(order == null){
                    return R.error("订单查询异常，无此订单信息");
                }
                JSONObject callobj = encryptAESData(order,"");
                HttpResult result= HttpUtils.doPostJson(order.getCallbackUrl(), callobj.toJSONString());
                return null;
            }else{
                return checkParam;
            }
        }catch (Exception e){
            log.info("订单回调异常，异常信息为:{}",e);
            return R.error("订单回调异常");
        }
    }
    /**
     * AES加密回调参数
     * @param order
     * @param aseKey
     * @return
     */
    public JSONObject encryptAESData(OrderInfoEntity order,String aseKey) {
        JSONObject callobj=new JSONObject();
        log.info("====回调加密前数据===="+callobj);
        String data=AES128Util.encryptBase64(callobj.toJSONString(), aseKey);
        JSONObject callbackjson=new JSONObject();
        callbackjson.put(BaseConstant.SIGN, DigestUtils.md5Hex(""));
        log.info("====回调加密后数据===="+callbackjson);
        return callbackjson;
    }
    @Override
    public OrderInfoEntity queryOrderInfoByOrderId(String orderId) {
        return null;
    }
}
