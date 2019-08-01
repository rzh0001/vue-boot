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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.*;

/**
 * @Description: 订单信息
 * @Author: jeecg-boot
 * @Date: 2019-07-24
 * @Version: V1.0
 */
@Slf4j
@Service
@Transactional
public class OrderInfoEntityServiceImpl extends ServiceImpl<OrderInfoEntityMapper, OrderInfoEntity> implements IOrderInfoEntityService {

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
    @Autowired
    private ISysUserService userService;
    @Autowired
    private IUserAmountEntityService amountService;


    /**
     * userName：商户
     * submitAmount： 支付金额
     * payType：通道
     * outerOrderId：外部订单号
     * callbackUrl：url
     *
     * @param reqobj
     * @return
     */
    @Override
    public R createOrder(JSONObject reqobj) {
        try {
            R checkParam = checkParam(reqobj, true);
            if (BaseConstant.CHECK_PARAM_SUCCESS.equals(checkParam.get(BaseConstant.CODE).toString())) {
                addOrder(checkParam);
            }
        } catch (Exception e) {
            log.info("创建订单异常，异常信息为:{}", e);
            return R.error("创建订单异常");
        }
        return R.ok("创建订单成功");
    }

    @Override
    public R queryOrderInfo(JSONObject reqobj) {
        try {
            R checkParam = checkParam(reqobj, false);
            if (BaseConstant.CHECK_PARAM_SUCCESS.equals(checkParam.get(BaseConstant.CODE).toString())) {
                String orderId = (String) checkParam.get(BaseConstant.ORDER_ID);
                OrderInfoEntity order = queryOrderInfoByOrderId(orderId);
                Map<String, Object> map = new HashMap<String, Object>();
                if (order != null) {
                    map.put(BaseConstant.STATUS, order.getStatus());
                    map.put(BaseConstant.ORDER_ID, order.getOrderId());
                    map.put(BaseConstant.OUTER_ORDER_ID, order.getOuterOrderId());
                    map.put(BaseConstant.SUBMIT_AMOUNT, order.getSubmitAmount());
                } else {
                    return R.error("无订单信息");
                }
                return R.ok().put(BaseConstant.ORDER_INFO, map);
            } else {
                return checkParam;
            }
        } catch (Exception e) {
            log.info("订单查询异常，异常信息为:{}", e);
            return R.error("订单查询异常");
        }
    }

    /**
     * 1、校验IP是否合法
     * 2、校验订单状态是否合法
     * 3、回调商户 , 回调商户的数据需要加密
     *
     * @param reqobj
     * @return
     */
    @Override
    public R callback(JSONObject reqobj, HttpServletRequest req) {
        try {
            //1
            checkIp(req);
            R checkParam = checkParam(reqobj, false);
            if (BaseConstant.CHECK_PARAM_SUCCESS.equals(checkParam.get(BaseConstant.CODE).toString())) {
                String orderId = (String) checkParam.get(BaseConstant.ORDER_ID);
                //2
                OrderInfoEntity order = queryOrderInfoByOrderId(orderId);
                if (order == null) {
                    return R.error("订单查询异常，无此订单信息");
                }
                String userName = (String) checkParam.get(BaseConstant.USER_NAME);
                ChannelBusinessEntity channelBusiness =
                        channelBusinessEntityService.queryChannelBusinessByUserName(userName);
                //3
                JSONObject callobj = encryptAESData(order, channelBusiness.getApiKey());
                HttpResult result = HttpUtils.doPostJson(order.getSuccessCallbackUrl(), callobj.toJSONString());
                return null;
            } else {
                return checkParam;
            }
        } catch (Exception e) {
            log.info("订单回调异常，异常信息为:{}", e);
            return R.error("订单回调异常");
        }
    }


    /**
     * * 校验商户信息
     * * 1、查看该商户的类型，是普通商户还是高级代理
     * * 高级代理
     * *
     * * 普通商户
     * * 是否有推荐人，如果有推荐人，则需要给推荐人返点
     * * 2、限额：
     * * 查看该用户提交金额，是否在限额范围内
     *
     * @param userName
     * @param submitAmount
     * @throws Exception
     */
    private void countAmount(String userName, String submitAmount) throws Exception {
        SysUser user = userService.getUserByName(userName);
        //验证金额是否符合上下线要求
        if (Double.parseDouble(submitAmount) > user.getUpperLimit().doubleValue()) {
            throw new RRException("非法请求，申请金额超出申请上限");
        }
        if (Double.parseDouble(submitAmount) < user.getLowerLimit().doubleValue()) {
            throw new RRException("非法请求，申请金额低于申请下限");
        }
        //高级代理的利润
        BigDecimal submit = new BigDecimal(submitAmount);
        String rate = rateEntityService.getUserRateByUserName(userName);
        BigDecimal userRate = new BigDecimal(rate);
        BigDecimal agentMoney = submit.multiply(userRate);

        //只有普通商户才有权限走单子
        if (user.getMemberType().equals(BaseConstant.USER_MERCHANTS)) {
            if (StringUtils.isBlank(user.getAgentId())) {
                throw new RRException("非法请求，无对应的代理商户存在");
            } else {
                //统计商户的所得金额
                countUserRate(userName, user.getAgentId(), submit, agentMoney);
            }
            if (!StringUtils.isBlank(user.getSalesmanId())) {
                //统计介绍人的所得金额
                countSalesmanRate(user.getSalesmanUsername(), user.getAgentId(), agentMoney);
            }
        } else {
            throw new RRException("非法请求，请求类型不是商户");
        }
    }

    /**
     * 介绍人的所得额度 = 高级代理所得额度 * 介绍人的rate
     *
     * @param salesmanName 介绍人
     * @param agentId      高级代理的id
     * @param agentMoney   高级代理所得利润
     */
    private void countSalesmanRate(String salesmanName, String agentId, BigDecimal agentMoney) {
        String salesmanRate = rateEntityService.getUserRateByUserName(salesmanName);
        UserAmountEntity salesman = amountService.getUserAmountByUserName(salesmanName);
        salesman.setAgentId(agentId);
        BigDecimal salesmanNow = agentMoney.multiply(new BigDecimal(salesmanRate));
        salesman.setAmount(salesman.getAmount().add(salesmanNow));
        amountService.saveOrUpdate(salesman);
        return;
    }

    /**
     * 商户的所得额度 = 申请金额 - 高级代理所得额度
     *
     * @param userName   商户
     * @param agentId    高级代理id
     * @param amount     订单申请金额
     * @param agentMoney 高级代理所得利润
     */
    private void countUserRate(String userName, String agentId, BigDecimal amount, BigDecimal agentMoney) {
        UserAmountEntity user = amountService.getUserAmountByUserName(userName);
        user.setAgentId(agentId);
        BigDecimal userNow = amount.subtract(agentMoney);
        user.setAmount(userNow.add(user.getAmount()));
        amountService.saveOrUpdate(user);
        return;
    }

    /**
     * 添加订单信息
     *
     * @param checkParam
     */
    private void addOrder(R checkParam) throws Exception {
        String outerOrderId = (String) checkParam.get(BaseConstant.OUTER_ORDER_ID);
        String userName = (String) checkParam.get(BaseConstant.USER_NAME);
        String submitAmount = (String) checkParam.get(BaseConstant.SUBMIT_AMOUNT);
        String payType = (String) checkParam.get(BaseConstant.PAY_TYPE);
        String callbackUrl = (String) checkParam.get(BaseConstant.CALLBACK_URL);
        //校验用户通道是否存在
        if (channelIsOpen(payType, userName)) {
            throw new RRException("通道未定义，或用户无此通道权限");
        }
        //查询用户对应的商户
        String businessCode = businessEntityService.queryBusinessCodeByUserName(userName);
        if (StringUtils.isBlank(businessCode)) {
            throw new RRException("该用户无对应商户信息");
        }
        String orderId = generateOrderId();
        OrderInfoEntity order = new OrderInfoEntity();
        order.setOrderId(orderId);
        order.setOuterOrderId(outerOrderId);
        //order.setUserId(userId);
        order.setUserName(userName);
        order.setBusinessCode(businessCode);
        order.setSubmitAmount(BigDecimal.valueOf(Long.valueOf(submitAmount)));
        order.setStatus(BaseConstant.ORDER_STATUS_NOT_PAY);
        order.setPayType(payType);
        order.setSuccessCallbackUrl(callbackUrl);
        order.setCreateTime(new Date());
        order.setCreateBy("api");
        //保存订单信息
        this.save(order);
        //统计商户和介绍人的收入
        countAmount(userName, submitAmount);
        //请求挂马平台
        requestSupport(order);
    }

    /**
     * 请求挂马平台
     *
     * @param order
     * @throws Exception
     */
    private void requestSupport(OrderInfoEntity order) throws Exception {
        if (order.getPayType().equals(BaseConstant.ALI_PAY)) {
            aliPayCallBack(order);
        }

    }

    /**
     * 校验通道是否是开启的
     *
     * @param channelCode
     * @param userName
     * @return
     */
    private boolean channelIsOpen(String channelCode, String userName) {
        ChannelEntity channel = chnannelDao.queryChannelByCode(channelCode);
        if (channel == null) {
            return false;
        }
        UserChannelEntity channelUser = channelUserDao.queryChannelAndUserName(channelCode, userName);
        if (channelUser == null) {
            return false;
        }
        return true;
    }


    /**
     * 请求挂马后台，生成付款页面
     * 回调挂马平台的地址，从数据字典中获取
     */
    private void aliPayCallBack(OrderInfoEntity order) throws Exception {
        AliPayCallBackParam param = new AliPayCallBackParam();
        param.setSign(sign(order));
        //获取挂马平台的地址
        List<DictModel> aliPayUrl = dictService.queryDictItemsByCode(BaseConstant.ALIPAY_URL);
        if (CollectionUtils.isEmpty(aliPayUrl)) {
            throw new RRException("未配置回调地址，请联系管理员配置回调地址");
        }
        String url = null;
        for (DictModel dict : aliPayUrl) {
            url = dict.getValue();
        }
        if (StringUtils.isBlank(url)) {
            throw new RRException("未配置回调地址，请联系管理员配置回调地址");
        }
        log.info("四方回调挂马平台，url:{};param:{}", url, JSON.toJSONString(param));
        HttpResult result = HttpUtils.doPostJson(url, JSON.toJSONString(param));
        if (result.getCode() == BaseConstant.SUCCESS) {

        } else {
            throw new RRException("四方回调挂马平台失败");
        }
    }


    /**
     * 生成签名信息
     * businessCode+submitAmount+apiKey 进行MD5
     *
     * @param order
     * @return
     */
    private String sign(OrderInfoEntity order) throws Exception {
        ChannelBusinessEntity channelBusiness =
                channelBusinessEntityService.queryChannelBusiness(order.getBusinessCode(), order.getPayType());
        if (channelBusiness == null) {
            throw new RRException("用户对应的商户无此通道信息");
        }
        String apiKey = channelBusiness.getApiKey();
        StringBuilder sign = new StringBuilder();
        return DigestUtils.md5Hex(sign.append(order.getBusinessCode()).append(order.getSubmitAmount()).append(apiKey).toString());
    }

    /**
     * 生成四方系统的订单号
     * 线程安全，保证生成的订单不一致
     */
    private synchronized static String generateOrderId() {
        StringBuilder orderId = new StringBuilder();
        String dateStr = DateUtils.date2Str(DateUtils.yyyymmddhhmmss);
        String randomStr = RandomStringUtils.randomAlphabetic(5);
        return orderId.append(dateStr).append(randomStr).toString();
    }

    /**
     * 校验IP是否合法
     * IP白名单从数据字典中获取
     *
     * @return
     */
    private boolean checkIp(HttpServletRequest req) {
        String ip = IPUtils.getIpAddr(req);
        List<DictModel> ipWhiteList = dictService.queryDictItemsByCode(BaseConstant.IP_WHITE_LIST);
        List<String> ips = new ArrayList<>();
        for (DictModel dictModel : ipWhiteList) {
            ips.add(dictModel.getValue());
        }
        if (!ips.contains(ip)) {
            return false;
        }
        return true;
    }

    /**
     * 参数校验
     * data：使用AES加密
     * md5：userId+timestamp+data
     * timestamp：时间戳
     * userId：商户
     *
     * @param reqobj
     * @return
     */
    private R checkParam(JSONObject reqobj, boolean createOrder) throws Exception {
        //时间戳
        Long timestamp = reqobj.getLong(BaseConstant.TIMESTAMP);
        //MD5值
        String sign = reqobj.getString(BaseConstant.SIGN);
        //RSA 加密data
        String data = reqobj.getString(BaseConstant.DATA);
        //四方系统用户
        String userName = reqobj.getString(BaseConstant.USER_NAME);

        Assert.isBlank(sign, "签名不能为空");
        Assert.isBlank(data, "数据不能为空");
        Assert.isBlank(timestamp, "时间戳不能为空");
        Assert.isBlank(userName, "商户不能为空");

        R decryptData = decryptData(data, userName, timestamp, sign);
        if (BaseConstant.CHECK_PARAM_SUCCESS.equals(decryptData.get(BaseConstant.CODE).toString())) {
            JSONObject dataObj = (JSONObject) decryptData.get(BaseConstant.DECRYPT_DATA);
            if (!createOrder) {
                return R.ok().put(BaseConstant.ORDER_ID, dataObj.getString(BaseConstant.ORDER_ID))
                        .put(BaseConstant.USER_NAME, dataObj.getString(BaseConstant.USER_NAME));
            }
            return R.ok().put(BaseConstant.OUTER_ORDER_ID, dataObj.getString(BaseConstant.OUTER_ORDER_ID))
                    .put(BaseConstant.USER_NAME, dataObj.getString(BaseConstant.USER_NAME))
                    .put(BaseConstant.SUBMIT_AMOUNT, dataObj.getString(BaseConstant.SUBMIT_AMOUNT))
                    .put(BaseConstant.PAY_TYPE, dataObj.getString(BaseConstant.PAY_TYPE))
                    .put(BaseConstant.CALLBACK_URL, dataObj.getString(BaseConstant.CALLBACK_URL));
        } else {
            return decryptData;
        }
    }

    /**
     * 解密数据
     *
     * @param data
     * @return
     * @throws Exception
     */
    private R decryptData(String data, String userName, Long timestamp, String sign) throws Exception {
        ChannelBusinessEntity channelBusiness = channelBusinessEntityService.queryChannelBusinessByUserName(userName);
        StringBuilder local = new StringBuilder();
        local.append(userName).append(timestamp).append(data).append(channelBusiness.getApiKey());
        String localSgin = DigestUtils.md5Hex(local.toString());
        if (!localSgin.equals(sign)) {
            return R.error("签名验证不通过");
        }
        String dataStr = AES128Util.decryptBase64(data, channelBusiness.getApiKey());
        if (StringUtils.isBlank(dataStr)) {
            return R.error("数据解密出现异常");
        }
        JSONObject dataObj = JSONObject.parseObject(dataStr);
        if (dataObj.isEmpty()) {
            return R.error("请求参数不能为空");
        }
        return R.ok().put(BaseConstant.DECRYPT_DATA, dataObj);
    }

    /**
     * AES加密回调参数
     *
     * @param order
     * @param aseKey
     * @return
     */
    public JSONObject encryptAESData(OrderInfoEntity order, String aseKey) {
        JSONObject callobj = new JSONObject();
        Long timestamp = System.currentTimeMillis();
        callobj.put(BaseConstant.ORDER_ID, order.getOrderId());
        callobj.put(BaseConstant.OUTER_ORDER_ID, order.getOuterOrderId());
        callobj.put(BaseConstant.SUBMIT_AMOUNT, order.getSubmitAmount());
        callobj.put(BaseConstant.STATUS, order.getStatus());

        log.info("====回调商户加密前数据====" + callobj.toJSONString());
        //加密数据
        String data = AES128Util.encryptBase64(callobj.toJSONString(), aseKey);
        JSONObject callbackjson = new JSONObject();
        StringBuilder sign = new StringBuilder();
        //sign = orderID+outOrderId+submitAmount+timestamp
        sign.append(order.getOrderId()).append(order.getOuterOrderId()).append(order.getSubmitAmount()).append(timestamp);
        callbackjson.put(BaseConstant.SIGN, DigestUtils.md5Hex(sign.toString()));
        callbackjson.put(BaseConstant.DATA, data);
        callbackjson.put(BaseConstant.TIMESTAMP, timestamp);
        log.info("====回调商户加密后数据====" + callbackjson);
        return callbackjson;
    }

    @Override
    public OrderInfoEntity queryOrderInfoByOrderId(String orderId) {
        return null;
    }

}
