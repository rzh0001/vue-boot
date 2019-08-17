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

import javax.annotation.PostConstruct;
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
@Transactional(rollbackFor = Exception.class)
public class OrderInfoEntityServiceImpl extends ServiceImpl<OrderInfoEntityMapper, OrderInfoEntity> implements IOrderInfoEntityService {

    @Autowired
    public ISysDictService dictService;
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

    private static OrderInfoEntityServiceImpl orderInfoEntityService;
    private static String aliPayUrl = null;
    private static String bankPayUrl = null;
    private static String ysfPayUrl = null;
    private static String nxysWxPayUrl = null;
    private static String nxysAliPayUrl = null;
    private static String key = null;
    private static String innerCallBackUrl = null;

    @PostConstruct
    public void init() {
        orderInfoEntityService = this;
        orderInfoEntityService.dictService = this.dictService;
        List<DictModel> payUrl = dictService.queryDictItemsByCode(BaseConstant.REQUEST_URL);
        for (DictModel dict : payUrl) {
            if (BaseConstant.REQUEST_ALI_ZZ.equals(dict.getText())) {
                aliPayUrl = dict.getValue();
            }
            if (BaseConstant.REQUEST_ALI_BANK.equals(dict.getText())) {
                bankPayUrl = dict.getValue();
            }
            if (BaseConstant.REQUEST_YSF.equals(dict.getText())) {
                ysfPayUrl = dict.getValue();
            }
            if (BaseConstant.REQUEST_NXYS_WX.equals(dict.getText())) {
                nxysWxPayUrl = dict.getValue();
            }
            if (BaseConstant.REQUEST_NXYS_ALIPAY.equals(dict.getText())) {
                nxysAliPayUrl = dict.getValue();
            }
        }
        List<DictModel> apiKey = dictService.queryDictItemsByCode(BaseConstant.API_KEY);
        for (DictModel k : apiKey) {
            if (BaseConstant.API_KEY.equals(k.getText())) {
                key = k.getValue();
            }
        }
        List<DictModel> innerUrl = dictService.queryDictItemsByCode(BaseConstant.INNER_CALL_BACK_URL);
        for (DictModel k : innerUrl) {
            if (BaseConstant.INNER_CALL_BACK_URL.equals(k.getText())) {
                innerCallBackUrl = k.getValue();
                break;
            }
        }
    }

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
    public R createOrder(JSONObject reqobj) throws Exception {
        R checkParam = checkParam(reqobj, true, false);
        if (BaseConstant.CHECK_PARAM_SUCCESS.equals(checkParam.get(BaseConstant.CODE).toString())) {
            return addOrder(checkParam);
        } else {
            return R.error("创建订单异常");
        }
    }

    @Override
    public R queryOrderInfo(JSONObject reqobj) {
        try {
            R checkParam = checkParam(reqobj, false, false);
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
     * 挂马 --> 四方
     * <p>
     * 1、校验IP是否合法；ip来源是否来自四方
     * 2、校验订单状态是否合法；订单是否已经支付
     * 3、回调商户;使用用户密钥加密后再进行回调
     * 4、修改订单状态为已支付
     * 5、统计高级代理、商户、介绍人的收入情况
     *
     * @param reqobj
     * @return
     */
    @Override
    public R callback(JSONObject reqobj, HttpServletRequest req) throws Exception {
        boolean flag = false;
        //1 校验ip是否来源于挂马平台
        if (!checkIpOk(req)) {
            throw new RRException("IP非法");
        }
        R checkParam = checkParam(reqobj, false, true);
        String orderId = (String) checkParam.get(BaseConstant.ORDER_ID);
        String payType = (String) checkParam.get(BaseConstant.PAY_TYPE);
        String userName = (String) checkParam.get(BaseConstant.USER_NAME);
        //2 校验订单状态 ，从挂马平台查询
        if (!orderStatusOk(orderId, payType)) {
            log.info("订单回调过程中，订单查询异常,orderID:{}", orderId);
            return R.error("订单查询异常，无此订单信息");
        }

        SysUser user = userService.getUserByName(userName);
        OrderInfoEntity order = queryOrderInfoByOrderId(orderId);
        String submitAmount = order.getSubmitAmount().toString();
        JSONObject callobj = encryptAESData(order, user.getApiKey());
        StringBuilder msg = new StringBuilder();
        if (BaseConstant.CHECK_PARAM_SUCCESS.equals(checkParam.get(BaseConstant.CODE).toString())) {
            try {
                //捕获异常的目的是为了防止各种异常情况下，仍然会去修改订单状态
                //3 数据加密之后，通知下游商户
                HttpResult result = HttpUtils.doPostJson(order.getSuccessCallbackUrl(), callobj.toJSONString());
                //4、修改订单状态
                if (result.getCode() == BaseConstant.SUCCESS) {
                    CallBackResult callBackResult = JSONObject.parseObject(result.getBody(), CallBackResult.class);
                    if (callBackResult.getCode() == BaseConstant.SUCCESS) {
                        updateOrderStatusSuccessByOrderId(orderId);
                        log.info("通知商户成功，并且商户返回成功,orderID:{}", orderId);
                        flag = true;
                        msg.append("通知商户成功，并且商户返回成功");
                    } else {
                        log.info("通通知商户失败,orderID:{}", orderId);
                        updateOrderStatusNoBackByOrderId(orderId);
                        msg.append("通知商户失败");
                    }
                } else {
                    log.info("通通知商户失败,orderID:{}", orderId);
                    updateOrderStatusNoBackByOrderId(orderId);
                    msg.append("通知商户失败");
                }
            } catch (Exception e) {
                log.info("订单回调商户异常，异常信息为:{}", e);
                updateOrderStatusNoBackByOrderId(orderId);
                msg.append("通知商户失败");
            } finally {
                if (flag) {
                    //5、只有在通知商户成功，才统计高级代理。商户。介绍人的收入情况
                    countAmount(userName, submitAmount);
                }
            }
            return R.ok(msg.toString());
        } else {
            return checkParam;
        }
    }

    /**
     * 从挂马平台查询，订单状态
     *
     * @param orderId 订单号
     * @param payType 支付通道类型
     * @return
     */
    private boolean orderStatusOk(String orderId, String payType) throws Exception {
        //通过支付通道从数据字典中获取要查询的地址
        List<DictModel> queryOrderStatusUrls = dictService.queryDictItemsByCode(BaseConstant.QUERY_ORDER_STATUS_URL);
        String url = null;
        for (DictModel model : queryOrderStatusUrls) {
            if (payType.equals(model.getText())) {
                url = model.getValue();
                break;
            }
        }
        if (StringUtils.isBlank(url)) {
            throw new RRException("未配置四方系统查询挂马平台的订单状态地址,单号：" + orderId + ";通道为：" + payType);
        }
        Map<String, String> data = new HashMap();
        data.put("id", orderId);
        String result = HttpUtils.doGet(url, data);
        if (StringUtils.isNotBlank(result)) {
            log.info("订单状态查询成功，返回信息为：{}", result);
            QueryOrderStatusResult orderStatusResult = JSONObject.parseObject(result, QueryOrderStatusResult.class);
            if (orderStatusResult != null && orderStatusResult.getCode() == BaseConstant.SUCCESS) {
                if (BaseConstant.QUERY_ORDER_STATUS_SUCCESS.equals(orderStatusResult.getData().getStatus())) {
                    return true;
                }
            } else {
                return false;
            }
        } else {
            log.info("订单状态查询失败，单号为：{}", orderId);
            return false;
        }
        return false;
    }

    /**
     * 校验申请金额
     *
     * @param userName
     * @param submitAmount
     * @return
     */
    private R checkAmountValidity(String userName, String submitAmount) {
        SysUser user = userService.getUserByName(userName);
        if (user.getMemberType().equals(BaseConstant.USER_MERCHANTS) && StringUtils.isNotBlank(user.getAgentUsername())) {
            //是普通商户且存在高级代理，验证才能通过
            //验证金额是否符合上下线要求
            if (Double.parseDouble(submitAmount) > user.getUpperLimit().doubleValue()) {
                return R.error("非法请求，申请金额超出申请上限");
            }
            if (Double.parseDouble(submitAmount) < user.getLowerLimit().doubleValue()) {
                return R.error("非法请求，申请金额低于申请下限");
            }
            return R.ok();
        } else {
            return R.error("非法请求，请求类型不是商户" + userName);
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
        //高级代理的利润
        BigDecimal submit = new BigDecimal(submitAmount);
        String rate = rateEntityService.getUserRateByUserNameAndAngetCode(userName, user.getAgentUsername());
        BigDecimal userRate = new BigDecimal(rate);
        BigDecimal agentMoney = submit.multiply(userRate).setScale(2, BigDecimal.ROUND_HALF_UP);

        //统计高级代理所得额度
        countAgentMoney(user.getAgentUsername(), submit);
        //统计商户的所得金额
        countUserRate(userName, user.getAgentUsername(), submit, agentMoney);
        //统计介绍人的所得金额
        if (!StringUtils.isBlank(user.getSalesmanUsername())) {
            countSalesmanRate(userName, user.getSalesmanUsername(), user.getAgentUsername(), agentMoney);
        }
    }

    /**
     * 统计高级代理所得总额
     *
     * @param agentName 高级代理
     * @param submit    申请金额
     */
    private void countAgentMoney(String agentName, BigDecimal submit) {
        UserAmountEntity agent = amountService.getUserAmountByUserName(agentName);
        if (agent == null) {
            agent = new UserAmountEntity();
            agent.setAmount(new BigDecimal(0));
        }
        agent.setUserName(agentName);
        agent.setAmount(agent.getAmount().add(submit).setScale(2, BigDecimal.ROUND_HALF_UP));
        amountService.saveOrUpdate(agent);
    }

    /**
     * 介绍人的所得额度 = 高级代理所得额度 * 介绍人的rate
     *
     * @param userName     被介绍人
     * @param salesmanName 介绍人
     * @param agentName    高级代理
     * @param agentMoney   高级代理所得利润
     */
    private void countSalesmanRate(String userName, String salesmanName, String agentName, BigDecimal agentMoney) {
        //介绍人费率
        String salesmanRate = rateEntityService.getBeIntroducerRate(salesmanName, agentName, userName);
        UserAmountEntity salesman = amountService.getUserAmountByUserName(salesmanName);
        if (salesman == null) {
            salesman = new UserAmountEntity();
            salesman.setAmount(new BigDecimal(0));
        }
        salesman.setUserName(salesmanName);
        salesman.setAgentId(agentName);
        BigDecimal salesmanNow = agentMoney.multiply(new BigDecimal(salesmanRate)).setScale(2,
                BigDecimal.ROUND_HALF_UP);
        salesman.setAmount(salesman.getAmount().add(salesmanNow).setScale(2, BigDecimal.ROUND_HALF_UP));
        amountService.saveOrUpdate(salesman);
    }

    /**
     * 商户的所得额度 = 申请金额 - 高级代理所得额度
     *
     * @param userName   商户
     * @param agentName  高级代理
     * @param amount     订单申请金额
     * @param agentMoney 高级代理所得利润
     */
    private void countUserRate(String userName, String agentName, BigDecimal amount, BigDecimal agentMoney) {
        UserAmountEntity user = amountService.getUserAmountByUserName(userName);
        if (user == null) {
            user = new UserAmountEntity();
            user.setAmount(new BigDecimal(0));
        }
        user.setAgentId(agentName);
        user.setUserName(userName);
        BigDecimal userNow = amount.subtract(agentMoney).setScale(2, BigDecimal.ROUND_HALF_UP);
        user.setAmount(userNow.add(user.getAmount()).setScale(2, BigDecimal.ROUND_HALF_UP));
        amountService.saveOrUpdate(user);
    }

    /**
     * 校验外部订单是否已经创建过
     *
     * @param outerOrderId
     * @return
     */
    private boolean outerOrderIdIsOnly(String outerOrderId) {
        String orderId = baseMapper.queryOrderByOuterOrderId(outerOrderId);
        if (StringUtils.isNotBlank(orderId)) {
            return false;
        }
        return true;
    }

    /**
     * 添加订单信息
     *
     * @param checkParam
     */
    private R addOrder(R checkParam) throws Exception {
        String outerOrderId = (String) checkParam.get(BaseConstant.OUTER_ORDER_ID);
        String userName = (String) checkParam.get(BaseConstant.USER_NAME);
        String submitAmount = (String) checkParam.get(BaseConstant.SUBMIT_AMOUNT);
        String payType = (String) checkParam.get(BaseConstant.PAY_TYPE);
        String callbackUrl = (String) checkParam.get(BaseConstant.CALLBACK_URL);
        String agentName = (String) checkParam.get(BaseConstant.AGENT_NAME);
        //校验是否是重复订单
        if (!outerOrderIdIsOnly(outerOrderId)) {
            log.info("该订单已经创建过，无需重复创建;orderid:{}", outerOrderId);
            throw new RRException("该订单已经创建过，无需重复创建" + outerOrderId);
        }
        //校验用户通道是否存在
        if (!channelIsOpen(payType, userName)) {
            log.info("通道未定义，或用户无此通道权限,用户为：{}", userName);
            throw new RRException("通道未定义，或用户无此通道权限,用户为：" + userName);
        }
        //查询用户对应的商户
        String businessCode = businessEntityService.queryBusinessCodeByUserName(userName);
        if (StringUtils.isBlank(businessCode)) {
            log.info("用户:{},无对应商户信息", userName);
            throw new RRException(userName + "用户无对应商户信息");
        }
        //校验金额的合法性
        R checkAmountValidity = checkAmountValidity(userName, submitAmount);
        if (!BaseConstant.CHECK_PARAM_SUCCESS.equals(checkAmountValidity.get(BaseConstant.CODE))) {
            return checkAmountValidity;
        }
        String orderId = generateOrderId();
        OrderInfoEntity order = new OrderInfoEntity();
        order.setOrderId(orderId);
        order.setOuterOrderId(outerOrderId);
        order.setUserName(userName);
        order.setBusinessCode(businessCode);
        order.setSubmitAmount(BigDecimal.valueOf(Long.valueOf(submitAmount)));
        order.setStatus(BaseConstant.ORDER_STATUS_NOT_PAY);
        order.setPayType(payType);
        order.setSuccessCallbackUrl(callbackUrl);
        order.setCreateTime(new Date());
        order.setCreateBy("api");
        order.setParentUser(agentName);
        //保存订单信息
        this.save(order);
        //请求挂马平台
        ChannelBusinessEntity channelBusinessEntity = channelBusinessEntityService.queryChannelBusiness(businessCode,
                payType);
        return requestSupport(order, channelBusinessEntity, userName, businessCode);
    }

    /**
     * 请求挂马平台
     *
     * @param order
     * @throws Exception
     */
    private R requestSupport(OrderInfoEntity order, ChannelBusinessEntity channelBusinessEntity, String userName,
                                String agentCode) throws Exception {
        //支付宝转账
        if (order.getPayType().equals(BaseConstant.REQUEST_ALI_ZZ)) {
            AliPayCallBackParam param = structuralAliParam(order, "text", "alipay_auto", "3", "2");
            aliPayCallBack(param, aliPayUrl);
            return R.ok();
        }
        //转卡
        if (order.getPayType().equals(BaseConstant.REQUEST_ALI_BANK)) {
            AliPayCallBackParam param = structuralAliParam(order, "text", "jdpay_auto", "3", "2");
            aliPayCallBack(param, bankPayUrl);
            return R.ok();
        }
        //云闪付
        if (order.getPayType().equals(BaseConstant.REQUEST_YSF)) {
            String apiKey = channelBusinessEntity.getApiKey();
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
            String param = structuralYsfParam(order, md5Key, aesKey, agentCode, userName);
            String url = ysfCallBack(param, ysfPayUrl);
            return R.ok().put("url",url);
        }
        //农信易扫
        if (order.getPayType().equals(BaseConstant.REQUEST_NXYS_WX) || order.getPayType().equals(BaseConstant.REQUEST_NXYS_ALIPAY)) {
            nxysCallBack(order);
            return R.ok();
        }
        return  R.error("无匹配通道");
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
     * 支付宝转卡、转账请求挂马平台
     *
     * @param param
     * @param url
     * @throws Exception
     */
    private void aliPayCallBack(AliPayCallBackParam param, String url) throws Exception {
        if (StringUtils.isBlank(url)) {
            throw new RRException("未配置支付宝回调地址，请联系管理员配置回调地址");
        }
        log.info("四方回调挂马平台，加密前数据，url:{};param:{}", url, JSON.toJSONString(param));

        String data = AES128Util.encryptBase64(JSON.toJSONString(param), key);

        log.info("四方回调挂马平台，加密后数据，url:{};param:{}", url, data);
        JSONObject p = new JSONObject();
        p.put("data",data);
        HttpResult result = HttpUtils.doPostJson(url, p.toJSONString());
        if (result.getCode() == BaseConstant.SUCCESS) {
            JSONObject r = JSONObject.parseObject(result.getBody());
            log.info("四方回调挂马平台成功，返回信息：{}", r.toJSONString());
        } else {
            throw new RRException("四方回调挂马平台失败,订单创建失败：" +result.getBody());
        }
    }

    /**
     * 农信易扫请求挂马平台
     *
     * @param order
     * @throws Exception
     */
    private void nxysCallBack(OrderInfoEntity order) throws Exception {
        NxysCallBackParam nxys = new NxysCallBackParam();
        nxys.setMerchantid(order.getBusinessCode());
        nxys.setOrderid(order.getOrderId());
        nxys.setAmount(order.getSubmitAmount().toString());
        nxys.setPaytype(order.getPayType());
        nxys.setClient_ip("");
        nxys.setNotify_url(innerCallBackUrl);
        nxys.setReturn_url(innerCallBackUrl);
        nxys.setExt(order.getOuterOrderId());
        nxys.setSign(sign(order));

        String param = JSON.toJSONString(nxys);
        log.info("农信易扫请求挂马后台，入参：{}", param);
        String url = nxysAliPayUrl;
        if (order.getPayType().equals(BaseConstant.REQUEST_NXYS_WX)) {
            url = nxysWxPayUrl;
        }
        if (StringUtils.isBlank(url)) {
            throw new RRException("农信易扫未配置回调挂马地址");
        }
        HttpResult result = HttpUtils.doPostJson(url, param);
        if (result.getCode() == BaseConstant.SUCCESS) {
            log.info("农信易扫请求挂马后台;四方回调挂马平台成功");
            JSONObject r = JSONObject.parseObject(result.getBody());
            if ((int) r.get("code") == 0) {
                String qrcode = (String) r.get("data");
                //展示农信易扫的二维码
            }
        } else {
            throw new RRException("农信易扫请求挂马后台;四方回调挂马平台失败");
        }
    }

    /**
     * 云闪付请求挂马平台
     *
     * @param param
     * @param url
     * @throws Exception
     */
    private String ysfCallBack(String param, String url) throws Exception {
        log.info("云闪付请求挂马平台，入参为：{}", param);
        if (StringUtils.isBlank(url)) {
            throw new RRException("未配置云闪付回调地址，请联系管理员配置回调地址");
        }
        HttpResult result = HttpUtils.doPostJson(url, param);
        if (result.getCode() == BaseConstant.SUCCESS) {
            log.info("云闪付挂马平台返回信息为：{}", result.getBody());
            JSONObject r = JSON.parseObject(result.getBody());
            String resultUrl = (String) r.get("payurl");
            if (StringUtils.isNotBlank(resultUrl)) {
                //打开返回的链接
                return resultUrl;
            }
        } else {
            throw new RRException("云闪付请求挂马平台，四方回调挂马平台失败,订单创建失败：" + param);
        }
        return null;
    }

    /**
     * 构造支付宝转卡转账的入参
     *
     * @param order
     * @param contentType
     * @param thoroughfare
     * @param type
     * @param robin
     * @return
     * @throws Exception
     */
    private AliPayCallBackParam structuralAliParam(OrderInfoEntity order, String contentType, String thoroughfare,
                                                   String type, String robin) throws Exception {
        AliPayCallBackParam param = new AliPayCallBackParam();
        param.setAccount_id(order.getBusinessCode());
        param.setContent_type(contentType);
        param.setThoroughfare(thoroughfare);
        param.setType(type);
        param.setOut_trade_no(order.getOrderId());
        param.setRobin(robin);
        param.setKeyId("");
        param.setAmount(order.getSubmitAmount().toString());
        param.setCallback_url(innerCallBackUrl);
        param.setSign(sign(order));
        return param;
    }

    /**
     * @param order
     * @param md5Key
     * @param aesKey
     * @param agentCode
     * @param userName
     * @return
     */
    private String structuralYsfParam(OrderInfoEntity order, String md5Key, String aesKey, String agentCode,
                                      String userName) {
        Long timestamp = System.currentTimeMillis() / 1000;
        String sign = DigestUtils.md5Hex(agentCode + timestamp + md5Key);

        JSONObject reqdata = new JSONObject(true);
        reqdata.put("orderchannel", 3);
        reqdata.put("trscode", "dynamicunionpay");
        reqdata.put("agentorderid", order.getOrderId());
        reqdata.put("applyamount", order.getSubmitAmount());
        reqdata.put("web_username", userName);
        reqdata.put("clienttype", "H5");
        //这里的回调地址，是挂马回调四方的地址，配置在数据字典中
        reqdata.put("callbackurl", innerCallBackUrl);
        String data = AES128Util.encryptBase64(reqdata.toJSONString(), aesKey);
        JSONObject reqobj = new JSONObject();
        reqobj.put("agentcode", agentCode);
        reqobj.put("timestamp", timestamp);
        reqobj.put("sign", sign);
        reqobj.put("data", data);
        return reqobj.toJSONString();
    }

    /**
     * businessCode+submitAmount+apiKey 进行MD5
     *
     * @param order
     * @return
     * @throws Exception
     */
    private String sign(OrderInfoEntity order) throws Exception {
        StringBuilder sign = new StringBuilder();
        return DigestUtils.md5Hex(sign.append(order.getBusinessCode()).append(order.getSubmitAmount()).append(key).toString());
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
     * 校验ip是否是从挂马平台过来的
     * IP白名单从数据字典中获取
     *
     * @return
     */
    private boolean checkIpOk(HttpServletRequest req) {
        String ip = IPUtils.getIpAddr(req);
        List<DictModel> ipWhiteList = dictService.queryDictItemsByCode(BaseConstant.IP_WHITE_LIST);
        List<String> ips = new ArrayList<>();
        if (!CollectionUtils.isEmpty(ipWhiteList)) {
            for (DictModel dictModel : ipWhiteList) {
                ips.add(dictModel.getValue());
            }
            if (!ips.contains(ip)) {
                log.info("非法访问ip，ip={}", ip);
                return false;
            }
        }
        return true;
    }

    /**
     * 参数校验
     * data：使用AES加密
     * md5：userName+timestamp+data+apikey
     * timestamp：时间戳
     * userName：商户
     *
     * @param reqobj
     * @return
     */
    private R checkParam(JSONObject reqobj, boolean createOrder, boolean fromInner) throws Exception {
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

        SysUser user = userService.getUserByName(userName);
        if (user == null) {
            log.info("userName参数校验-->用户不存在，username:{}", userName);
            return R.error("用户不存在");
        }
        if (createOrder) {
            if (!BaseConstant.USER_MERCHANTS.equals(user.getMemberType())) {
                log.info("用户类型不是商户，无法提交订单，用户名为：{}", userName);
                return R.error("用户类型不是商户，无法提交订单");
            }
            checkRate(user);
        }

        String apiKey = null;
        if (fromInner) {
            apiKey = key;
        } else {
            apiKey = user.getApiKey();
        }
        R decryptData = decryptData(data, userName, timestamp, sign, user.getApiKey());
        if (BaseConstant.CHECK_PARAM_SUCCESS.equals(decryptData.get(BaseConstant.CODE).toString())) {
            JSONObject dataObj = (JSONObject) decryptData.get(BaseConstant.DECRYPT_DATA);
            if (!createOrder) {
                return R.ok().put(BaseConstant.ORDER_ID, dataObj.getString(BaseConstant.ORDER_ID))
                        .put(BaseConstant.USER_NAME, dataObj.getString(BaseConstant.USER_NAME))
                        .put(BaseConstant.AGENT_NAME, user.getAgentUsername())
                        .put(BaseConstant.PAY_TYPE, dataObj.getString(BaseConstant.PAY_TYPE));
            }
            return R.ok().put(BaseConstant.OUTER_ORDER_ID, dataObj.getString(BaseConstant.OUTER_ORDER_ID))
                    .put(BaseConstant.USER_NAME, dataObj.getString(BaseConstant.USER_NAME))
                    .put(BaseConstant.SUBMIT_AMOUNT, dataObj.getString(BaseConstant.SUBMIT_AMOUNT))
                    .put(BaseConstant.PAY_TYPE, dataObj.getString(BaseConstant.PAY_TYPE))
                    .put(BaseConstant.CALLBACK_URL, dataObj.getString(BaseConstant.CALLBACK_URL))
                    .put(BaseConstant.AGENT_NAME, user.getAgentUsername());
        } else {
            return decryptData;
        }
    }

    /**
     * 检查用户的费率是否完整
     *
     * @param user
     * @throws Exception
     */
    private void checkRate(SysUser user) throws Exception {
        if (StringUtils.isBlank(user.getAgentUsername())) {
            throw new RRException("用户未配置高级代理");
        }
        //商户
        String rate = rateEntityService.getUserRateByUserNameAndAngetCode(user.getUsername(), user.getAgentUsername());
        if (StringUtils.isBlank(rate)) {
            throw new RRException("用户未配置费率，请联系管理员配置");
        }
        if (StringUtils.isNotBlank(user.getSalesmanUsername())) {
            //介绍人
            SysUser sale = userService.getUserByName(user.getSalesmanUsername());
            String salesRate = rateEntityService.getBeIntroducerRate(user.getSalesmanUsername(),
                    sale.getAgentUsername(), user.getUsername());
            if (StringUtils.isBlank(salesRate)) {
                throw new RRException("用户的介绍人未配置费率，请联系管理员配置");
            }
        }
    }

    /**
     * @param data      加密数据
     * @param userName  商户
     * @param timestamp 时间戳
     * @param sign      签名 -》 MD5(userName+timestamp+data+apiKey)
     * @param apiKey    商户密钥
     * @return
     * @throws Exception
     */
    private R decryptData(String data, String userName, Long timestamp, String sign, String apiKey) throws Exception {
        StringBuilder local = new StringBuilder();
        local.append(userName).append(timestamp).append(data).append(apiKey);
        String localSgin = DigestUtils.md5Hex(local.toString());
        if (!localSgin.equals(sign)) {
            return R.error("签名验证不通过");
        }
        //解密
        String dataStr = AES128Util.decryptBase64(data, apiKey);
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
    public JSONObject encryptAESData(OrderInfoEntity order, String aseKey) throws Exception {
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
        return baseMapper.queryOrderByOrderId(orderId);
    }

    @Override
    public void updateOrderStatusSuccessByOrderId(String orderId) {
        baseMapper.updateOrderStatusSuccessByOrderId(orderId);
    }

    @Override
    public void updateOrderStatusNoBackByOrderId(String orderId) {
        baseMapper.updateOrderStatusNoBackByOrderId(orderId);
    }

}
