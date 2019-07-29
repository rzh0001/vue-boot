package org.jeecg.modules.pay.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.common.system.vo.DictModel;
import org.jeecg.common.util.DateUtils;
import org.jeecg.modules.exception.RRException;
import org.jeecg.modules.pay.entity.*;
import org.jeecg.modules.pay.mapper.OrderInfoEntityMapper;
import org.jeecg.modules.pay.service.*;
import org.jeecg.modules.system.entity.SysUser;
import org.jeecg.modules.system.service.ISysDictService;
import org.jeecg.modules.system.service.ISysUserService;
import org.jeecg.modules.system.util.IPUtils;
import org.jeecg.modules.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.util.CollectionUtils;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.*;

/**
 * @Description: 订单信息
 * @Author: jeecg-boot
 * @Date:   2019-07-24
 * @Version: V1.0
 */
@Slf4j
@Service
public class OrderInfoEntityServiceImpl extends ServiceImpl<OrderInfoEntityMapper, OrderInfoEntity> implements IOrderInfoEntityService {

    @Autowired
    private ISysUserService userService;
    @Autowired
    private ISysDictService dictService;
    @Autowired
    private IChannelEntityService chnannelDao;
    @Autowired
    private IUserChannelEntityService channelUserDao;
    @Autowired
    private IUserBusinessEntityService businessEntityService;
    @Autowired
    private IUserRateEntityService rateEntityService;
    @Autowired
    private IChannelBusinessEntityService channelBusinessEntityService;
    /**
     * userId：商户ID
     * submitAmount： 支付金额
     * payType：通道
     * outerOrderId：外部订单号
     * callbackUrl：url
     * @param reqobj
     * @return
     */
    @Override
    public R createOrder(JSONObject reqobj) {
        try {
            R checkParam = checkParam(reqobj,false);
            if(BaseConstant.CHECK_PARAM_SUCCESS.equals(checkParam.get("code").toString())){
                addOrder(checkParam);
            }
        }catch (Exception e){
            log.info("创建订单异常，异常信息为:{}",e);
            return R.error("创建订单异常");
        }
        return null;
    }

    @Override
    public R queryOrderInfo(JSONObject reqobj) {
        try{
            R checkParam = checkParam(reqobj,false);
            if(BaseConstant.CHECK_PARAM_SUCCESS.equals(checkParam.get("code").toString())){
                String orderId = (String) checkParam.get("orderId");
                OrderInfoEntity order = queryOrderInfoByOrderId(orderId);
                Map<String,Object> map=new HashMap<String,Object>();
                if(order != null){
                    map.put(BaseConstant.STATUS,order.getStatus());
                    map.put(BaseConstant.ORDER_ID,order.getOrderId());
                    map.put(BaseConstant.OUTER_ORDER_ID,order.getOuterOrderId());
                }else {
                    return R.error("无订单信息");
                }
                return R.ok().put("data",map);
            }else{
                return checkParam;
            }
        }catch (Exception e){
            log.info("订单查询异常，异常信息为:{}",e);
            return R.error("订单查询异常");
        }
    }

    /**
     * 1、校验IP是否合法
     * 2、校验订单状态是否合法
     * 3、回调
     * @param reqobj
     * @return
     */
    @Override
    public R callback(JSONObject reqobj, HttpServletRequest req) {
        try{
            //1
            checkIp(req);
            R checkParam = checkParam(reqobj,false);
            if(BaseConstant.CHECK_PARAM_SUCCESS.equals(checkParam.get("code").toString())){
                String orderId = (String) checkParam.get("orderId");
                //2
                OrderInfoEntity order = queryOrderInfoByOrderId(orderId);
                if(order == null){
                    return R.error("订单查询异常，无此订单信息");
                }
                //3
                JSONObject callobj = encryptAESData(order,"");
                HttpResult result= HttpUtils.doPostJson(order.getSuccessCallbackUrl(), callobj.toJSONString());
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
     * 添加订单信息
     * @param checkParam
     */
    private void addOrder(R checkParam) throws Exception{
        String outerOrderId = (String) checkParam.get(BaseConstant.OUTER_ORDER_ID);
        String userId = (String) checkParam.get(BaseConstant.USER_ID);
        String submitAmount = (String) checkParam.get(BaseConstant.SUBMIT_AMOUNT);
        String payType = (String) checkParam.get(BaseConstant.PAY_TYPE);
        String callbackUrl = (String) checkParam.get(BaseConstant.CALLBACK_URL);
        //校验用户通道是否存在
        if(channelIsOpen(payType,userId)){
            throw new RRException("通道未定义，或用户无此通道权限");
        }
        //查询用户对应的商户
        String businessCode = businessEntityService.queryBusinessCodeByUserId(userId);
        if(StringUtils.isBlank(businessCode)){
            throw new RRException("该用户无对应商户信息");
        }
        String orderId = generateOrderId();
        OrderInfoEntity order = new OrderInfoEntity();
        order.setOrderId(orderId);
        order.setOuterOrderId(outerOrderId);
        order.setUserId(userId);
        order.setBusinessCode(businessCode);
        order.setSubmitAmount(BigDecimal.valueOf(Long.valueOf(submitAmount)));
        order.setStatus(BaseConstant.ORDER_STATUS_NOT_PAY);
        order.setPayType(payType);
        order.setSuccessCallbackUrl(callbackUrl);
        order.setCreateTime(new Date());
        order.setCreateBy("api");
        //计算费率
        countOrderRate(order);
        //保存订单信息
        this.save(order);
        //请求挂马平台
        requestSupport(order);
    }

    /**
     * 请求挂马平台
     * @param order
     * @throws Exception
     */
    private void requestSupport(OrderInfoEntity order) throws Exception{
        if(order.getPayType().equals(BaseConstant.ALI_PAY)){
            aliPayCallBack(order);
        }

    }
    /**
     * 校验通道是否是开启的
     * @param channelCode
     * @param useriId
     * @return
     */
    private boolean channelIsOpen(String channelCode,String useriId){
        ChannelEntity channel = chnannelDao.queryChannelByCode(channelCode);
        if(channel == null){
            return false;
        }
        UserChannelEntity channelUser = channelUserDao.queryChannelAndUser(channelCode,useriId);
        if(channelUser == null){
            return false;
        }
        return true;
    }

    /**
     * 计算订单汇率
     */
    private void countOrderRate(OrderInfoEntity order)throws Exception{
        String userRate = rateEntityService.getUserRate(order.getUserId(),order.getPayType());
        BigDecimal rate = new BigDecimal(Double.valueOf(userRate));
        BigDecimal poundage = order.getSubmitAmount().multiply(rate);
        order.setPoundage(poundage);
    }

    /**
     * 请求挂马后台，生成付款页面
     * 回调挂马平台的地址，从数据字典中获取
     */
    private void aliPayCallBack(OrderInfoEntity order) throws Exception{
        AliPayCallBackParam param = new AliPayCallBackParam();
        param.setSign(sign(order));
        //获取挂马平台的地址
        List<DictModel> aliPayUrl =  dictService.queryDictItemsByCode(BaseConstant.ALIPAY_URL);
        if(CollectionUtils.isEmpty(aliPayUrl)){
            throw new RRException("未配置回调地址，请联系管理员配置回调地址");
        }
        String url = null;
        for(DictModel dict : aliPayUrl){
            url = dict.getValue();
        }
        if(StringUtils.isBlank(url)){
            throw new RRException("未配置回调地址，请联系管理员配置回调地址");
        }
        log.info("四方回调挂马平台，url:{};param:{}",url,JSON.toJSONString(param));
        HttpResult result = HttpUtils.doPostJson(url, JSON.toJSONString(param));
        if(result.getCode() == BaseConstant.SUCCESS){

        }else {
            throw new RRException("四方回调挂马平台失败");
        }
    }


    /**
     * 生成签名信息
     * businessCode+submitAmount+apiKey 进行MD5
     * @param order
     * @return
     */
    private String sign(OrderInfoEntity order) throws Exception{
        ChannelBusinessEntity channelBusiness = channelBusinessEntityService.queryChannelBusiness(order.getBusinessCode(),order.getPayType());
        if(channelBusiness == null){
            throw  new RRException("用户对应的商户无此通道信息");
        }
        String apiKey = channelBusiness.getApiKey();
        StringBuilder sign = new StringBuilder();
        return DigestUtils.md5Hex(sign.append(order.getBusinessCode()).append(order.getSubmitAmount()).append(apiKey).toString());
    }

    /**
     * 生成四方系统的订单号
     * 线程安全，保证生成的订单不一致
     */
    private synchronized static String generateOrderId(){
        StringBuilder orderId = new StringBuilder();
        String dateStr = DateUtils.date2Str(DateUtils.yyyymmddhhmmss);
        String randomStr = RandomStringUtils.randomAlphabetic(5);
        return orderId.append(dateStr).append(randomStr).toString();
    }

    /**
     * 校验IP是否合法
     * @return
     */
    private boolean checkIp(HttpServletRequest req){
        String ip = IPUtils.getIpAddr(req);
        List<DictModel> ipWhiteList =  dictService.queryDictItemsByCode("ipWhiteList");
        List<String> ips = new ArrayList<>();
        for(DictModel dictModel : ipWhiteList){
            ips.add(dictModel.getValue());
        }
        if(!ips.contains(ip)){
            return false;
        }
        return true;
    }
    /**
     * 参数校验
     * data：使用AES加密
     * md5：
     *      订单创建：businessCode+timestamp+data
     *      查询：userName+timestamp+data
     * timestamp：时间戳
     * userName：四方系统的用户
     * businessCode：商户编码
     * @param reqobj
     * @return
     */
    private R checkParam(JSONObject reqobj,boolean createOrder){
        //用户账号登录名
        String userName = null;
        String businessCode = null;
        //时间戳
        Long timestamp=reqobj.getLong(BaseConstant.TIMESTAMP);
        //MD5值
        String sign=reqobj.getString(BaseConstant.SIGN);
        //RSA 加密data
        String data=reqobj.getString(BaseConstant.DATA);

        String localSgin = null;
        if(createOrder){
            Assert.isBlank(reqobj.getString(BaseConstant.CALLBACK_URL), "回调地址不能为空");
            Assert.isBlank(reqobj.getString(BaseConstant.USER_ID), "商户名不能为空");
            businessCode = reqobj.getString(BaseConstant.USER_ID);
            localSgin= DigestUtils.md5Hex(businessCode+timestamp+data);
        }else{
            Assert.isBlank(reqobj.getString(BaseConstant.USER_NAME), "username不能为空");
            localSgin= DigestUtils.md5Hex(userName+timestamp+data);
            userName=reqobj.getString(BaseConstant.USER_NAME);
        }
        Assert.isBlank(reqobj.getString(BaseConstant.SIGN), "签名不能为空");
        Assert.isBlank(reqobj.getString(BaseConstant.DATA), "数据不能为空");
        Assert.isBlank(reqobj.getString(BaseConstant.TIMESTAMP), "时间戳不能为空");

        try{
            if(!localSgin.equals(sign)) {
                return R.error("签名验证不通过");
            }
            SysUser user = userService.getUserByName(userName);
            String dataStr= AES128Util.decryptBase64(data, user.getSalt());
            if(StringUtils.isBlank(dataStr)){
                return R.error("数据解密出现异常");
            }
            JSONObject dataobj=JSONObject.parseObject(dataStr);
            if(!createOrder){
                if(dataobj.isEmpty() || StringUtils.isBlank(dataobj.getString("orderId"))){
                    return R.error("请求参数不能为空");
                }
                return R.ok().put("orderId",dataobj.getString("orderId"));
            }
            return R.ok().put(BaseConstant.OUTER_ORDER_ID,dataobj.getString(BaseConstant.OUTER_ORDER_ID))
                    .put(BaseConstant.USER_ID,dataobj.getString(BaseConstant.USER_ID))
                    .put(BaseConstant.SUBMIT_AMOUNT,dataobj.getString(BaseConstant.SUBMIT_AMOUNT))
                    .put(BaseConstant.PAY_TYPE,dataobj.getString(BaseConstant.PAY_TYPE))
                    .put(BaseConstant.CALLBACK_URL,dataobj.getString(BaseConstant.CALLBACK_URL));

        }catch (Exception e){
            log.info("参数解析异常，异常信息为:{}",e);
            return R.error("参数解密出现异常");
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
