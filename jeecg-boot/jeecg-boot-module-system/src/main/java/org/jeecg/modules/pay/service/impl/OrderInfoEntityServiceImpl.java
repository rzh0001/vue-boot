package org.jeecg.modules.pay.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.util.StringUtil;
import org.jeecg.common.system.vo.DictModel;
import org.jeecg.common.util.DateUtils;
import org.jeecg.common.util.RedisUtil;
import org.jeecg.modules.exception.RRException;
import org.jeecg.modules.pay.entity.*;
import org.jeecg.modules.pay.mapper.OrderInfoEntityMapper;
import org.jeecg.modules.pay.service.*;
import org.jeecg.modules.pay.service.factory.PayServiceFactory;
import org.jeecg.modules.pay.service.requestPayUrl.RequestPayUrl;
import org.jeecg.modules.system.entity.SysUser;
import org.jeecg.modules.system.service.ISysDictService;
import org.jeecg.modules.system.service.ISysUserService;
import org.jeecg.modules.system.util.IPUtils;
import org.jeecg.modules.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @Description: 订单信息
 * @Author: jeecg-boot
 * @Date: 2019-07-24
 * @Version: V1.0
 */
@Slf4j
@Service
public class OrderInfoEntityServiceImpl extends ServiceImpl<OrderInfoEntityMapper, OrderInfoEntity> implements IOrderInfoEntityService {


    @Autowired
    private IChannelEntityService chnannelDao;
    @Autowired
    private IUserChannelEntityService channelUserDao;
    @Autowired
    private IUserBusinessEntityService businessEntityService;
    @Autowired
    private IUserRateEntityService rateEntityService;
    @Autowired
    private ISysUserService userService;
    @Autowired
    private IUserAmountEntityService amountService;
    @Autowired
    private PayServiceFactory factory;
    @Autowired
    public ISysDictService dictService;
    @Autowired
    public IUserAmountDetailService amountDetailService;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private IBusinessIncomeLogService bi;
    /**
     * 请求挂码平台的秘钥
     */
    private static String key = null;
    /**
     * 挂码平台回调四方的地址
     */
    private static String innerCallBackUrl = null;

    @PostConstruct
    public void init() {
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
    @Transactional(rollbackFor = Exception.class)
    public R createOrder(JSONObject reqobj, HttpServletRequest req) throws Exception {
        R checkParam = checkParam(reqobj, true, false, false);
        if (BaseConstant.CHECK_PARAM_SUCCESS.equals(checkParam.get(BaseConstant.CODE).toString())) {
            return addOrder(checkParam, req);
        } else {
            return checkParam;
        }
    }

    @Override
    public R queryOrderInfo(JSONObject reqobj) {
        try {
            R checkParam = checkParam(reqobj, false, false, true);
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

    @Override
    public Object innerSysCallBack(String payTpye, Object param) throws Exception {
        RequestPayUrl requestPayUrl = factory.getPay(payTpye);
        return requestPayUrl.callBack(param);
    }

    /**
     * 通过回调的参数来区分，是否是内部系统的调用
     *
     * @param param
     * @return
     */
    @Override
    public Map<String, Object> isInternalSystem(Map<String, Object> param) {
        String payType = null;
        Map<String, Object> map = new HashMap<>();
        //获取数据字典配置的属于外部系统标识通道的字段
        List<DictModel> fields = dictService.queryDictItemsByCode(BaseConstant.EXTERNAL_FIELD);
        log.info("==》挂马平台回调四方平台，数据字典externalField：{}",fields.toString());
        List<String> payTypeFields = new ArrayList<>();
        boolean isInternalSystem = true;
        if (!CollectionUtils.isEmpty(fields)) {
            for (DictModel dictModel : fields) {
                payTypeFields.add(dictModel.getValue());
            }
        }
        if(!CollectionUtils.isEmpty(payTypeFields)){
            for(String field:payTypeFields){
                if(param.get(field) != null){
                    isInternalSystem = false;
                    payType = (String) param.get(field);
                    log.info("===>外部挂马平台回调四方，通道为：{}",payType);
                    break;
                }
            }
        }
        map.put("isInternalSystem", isInternalSystem);
        map.put("payType", payType);
        return map;
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
        //1 校验ip是否来源于挂马平台
        if (!checkIpOk(req)) {
            throw new RRException("IP非法");
        }
        R checkParam = checkParam(reqobj, false, true, false);
        if (!BaseConstant.CHECK_PARAM_SUCCESS.equals(checkParam.get(BaseConstant.CODE).toString())) {
            return checkParam;
        }
        String orderId = (String) checkParam.get(BaseConstant.ORDER_ID);
        String payType = (String) checkParam.get(BaseConstant.PAY_TYPE);
        String userName = (String) checkParam.get(BaseConstant.USER_NAME);
        RequestPayUrl requestPayUrl = (RequestPayUrl) checkParam.get(BaseConstant.REQUEST);
        OrderInfoEntity order = queryOrderInfoByOrderId(orderId);
        if (order == null) {
            log.info("订单查询异常，无此订单信息:{}", orderId);
            return R.error("订单查询异常，无此订单信息");
        }
        //预防多次回调
        if (order.getStatus() == BaseConstant.ORDER_STATUS_SUCCESS) {
            log.info("该订单已经回调过了，不能重复回调:{}", order.getOrderId());
            return R.error("该订单已经回调过了，不能重复回调");
        }
        //2 校验订单状态 ，从挂马平台查询
        //校验在此通道下的该商户对应的代理是否有定义挂码账号
        SysUser user = userService.getUserByName(userName);
        List<UserBusinessEntity> useBusinesses =
                businessEntityService.queryBusinessCodeByUserName(user.getAgentUsername(), payType);
        String queryUrl = null;
        //通过支付通道从数据字典中获取要查询的地址
        List<DictModel> queryOrderStatusUrls = dictService.queryDictItemsByCode(BaseConstant.QUERY_ORDER_STATUS_URL);
        for (DictModel model : queryOrderStatusUrls) {
            if (payType.equals(model.getText())) {
                queryUrl = model.getValue();
                break;
            }
        }
        if (StringUtils.isBlank(queryUrl)) {
            throw new RRException("未配置四方系统查询挂马平台的订单状态地址,单号：" + orderId + ";通道为：" + payType);
        }
        //校验订单信息，并更新订单状态
        if (!requestPayUrl.orderInfoOk(order, queryUrl, useBusinesses.get(0))) {
            log.info("订单回调过程中，订单查询异常,orderID:{}", orderId);
            return R.error("订单查询异常，无此订单信息");
        }
        return notifyCustomer(order, user, payType);
    }

    @Override
    public R notifyCustomer(OrderInfoEntity order, SysUser user, String payType) throws Exception {
        boolean flag = false;
        String submitAmount = order.getSubmitAmount().toString();
        JSONObject callobj = encryptAESData(order, user.getApiKey());
        StringBuilder msg = new StringBuilder();
        String body = null;
        try {
            log.info("===回调商户，url:{},param:{}", order.getSuccessCallbackUrl(), callobj.toJSONString());
            //捕获异常的目的是为了防止各种异常情况下，仍然会去修改订单状态
            //3 数据加密之后，通知下游商户
            HttpResult result = HttpUtils.doPostJson(order.getSuccessCallbackUrl(), callobj.toJSONString());
            //4、修改订单状态,同时更新订单的update_time;标示订单的回调时间
            body = result.getBody();
            log.info("===商户返回信息：{}", body);
            if (result.getCode() == BaseConstant.SUCCESS) {
                JSONObject callBackResult = JSON.parseObject(result.getBody());
                //CallBackResult callBackResult = JSONObject.parseObject(result.getBody(), CallBackResult.class);
                if ("200".equals(callBackResult.get("code").toString())) {
                    updateOrderStatusSuccessByOrderId(order.getOrderId());
                    updateBusinessIncomeAmount(order);
                    log.info("通知商户成功，并且商户返回成功,orderID:{}", order.getOrderId());
                    flag = true;
                    msg.append("通知商户成功，并且商户返回成功");
                    return R.ok(msg.toString());
                } else {
                    log.info("通通知商户失败,orderID:{}", order.getOrderId());
                    updateOrderStatusNoBackByOrderId(order.getOrderId());
                    msg.append("通知商户失败，原因：").append(callBackResult.get("msg"));
                    return R.error(msg.toString());
                }
            } else {
                log.info("通通知商户失败,orderID:{}", order.getOrderId());
                updateOrderStatusNoBackByOrderId(order.getOrderId());
                msg.append("通知商户失败，返回状态码为：").append(result.getCode());
                return R.error(msg.toString());
            }
        } catch (Exception e) {
            log.info("订单回调商户异常，异常信息为:{}", e);
            updateOrderStatusNoBackByOrderId(order.getOrderId());
            msg.append("通知商户失败，原因：商户未返回json格式数据,返回内容：").append(body);
            return R.error(msg.toString());
        } finally {
            if (flag) {
                //5、只有在通知商户成功，才统计高级代理。商户。介绍人的收入情况
                countAmount(order.getOrderId(), user.getUsername(), submitAmount, payType);
            }
        }
    }

    private Lock lock = new ReentrantLock();

    /**
     * 更新挂马账户的收入情况
     *
     * @param order
     */
    @Transactional
    public void updateBusinessIncomeAmount(OrderInfoEntity order) {
        BusinessIncomeLog income = new BusinessIncomeLog();
        income.setOrderId(order.getOrderId());
        income.setBusinessCode(order.getBusinessCode());
        income.setChannelCode(order.getPayType());
        income.setSubmitamount(order.getSubmitAmount());
        income.setType("2");
        bi.save(income);
        lock.lock();
        try {
            businessEntityService.updateBusinessIncomeAmount(order);
        } catch (Exception e) {
            log.info("==》更新挂马账号收入异常，异常信息为:{}", e);
        } finally {
            lock.unlock();
        }

    }

    /**
     * 校验申请金额
     *
     * @param submitAmount
     * @return
     */
    private void checkAmountValidity(String userName, String submitAmount, String payType) {
        UserChannelEntity channel = channelUserDao.queryChannelAndUserName(payType, userName);
        if (channel == null) {
            throw new RRException("用户通道通道不存在:" + payType);
        }
        if (channel.getUpperLimit() != null && channel.getUpperLimit().doubleValue() < Double.parseDouble(submitAmount)) {
            throw new RRException("非法请求，申请金额超出申请上限");
        }
        if (channel.getLowerLimit() != null && channel.getLowerLimit().doubleValue() > Double.parseDouble(submitAmount)) {
            throw new RRException("非法请求，申请金额低于申请下限");
        }
    }

    /**
     * 统计规则：
     * 查看订单的商户是否存在介绍人：
     * 存在介绍人：
     * 介绍人收入=订单金额*（介绍人费率-代理费率）
     * 代理收入 = 订单金额*代理费率
     * 商户收入 = 订单金额 - 介绍人收入
     * 不存在介绍人：
     * 代理收入 = 订单金额 * 商户费率
     * 商户收入 = 订单金额 - 代理收入
     *
     * @param orderId      四方系统的订单id
     * @param userName     订单用户
     * @param submitAmount 申请金额
     * @param payType      通道
     * @throws Exception
     */
    @Override
    public void countAmount(String orderId, String userName, String submitAmount, String payType) throws Exception {
        SysUser user = userService.getUserByName(userName);
        BigDecimal submit = new BigDecimal(submitAmount);
        //介绍人为空
        if (org.springframework.util.StringUtils.isEmpty(user.getSalesmanUsername())) {
            //商户的费率
            String rateString = rateEntityService.getUserRateByUserNameAndAngetCode(userName,
                    user.getAgentUsername(), payType);
            BigDecimal rate = new BigDecimal(rateString);
            UserAmountEntity agent = amountService.getUserAmountByUserName(user.getAgentUsername());
            //代理获利
            BigDecimal agentAmount = submit.multiply(rate).setScale(2, BigDecimal.ROUND_HALF_UP);
            changeAmount(user.getAgentUsername(), user.getAgentId(), agentAmount, agent, null, orderId, payType, null);
            //商户收入
            UserAmountEntity customer = amountService.getUserAmountByUserName(userName);
            changeAmount(userName, user.getId(), submit.subtract(agentAmount), customer, user.getAgentId(), orderId,
                    payType, null);
        } else {
            //介绍人不为空
            //介绍人对商户设置的费率
            String introducerRate = rateEntityService.getBeIntroducerRate(userName, user.getAgentUsername(),
                    user.getSalesmanUsername(), payType);
            //代理对介绍人设置的费率
            SysUser sale = userService.getUserByName(user.getSalesmanUsername());
            String agentRate = rateEntityService.getUserRateByUserNameAndAngetCode(user.getSalesmanUsername(),
                    sale.getAgentUsername(), payType);
            //介绍人对商户设置的费率 - 代理对介绍人的费率 = 介绍人的利率差
            BigDecimal rateDifference = new BigDecimal(introducerRate).subtract(new BigDecimal(agentRate));
            //介绍人获利 = 订单金额*（介绍人费率-代理费率）
            BigDecimal saleAmount = submit.multiply(rateDifference).setScale(2, BigDecimal.ROUND_HALF_UP);
            UserAmountEntity saleDbAmount = amountService.getUserAmountByUserName(user.getSalesmanUsername());
            changeAmount(user.getSalesmanUsername(), sale.getId(), saleAmount, saleDbAmount, sale.getAgentId(),
                    orderId, payType, null);

            //代理获利 = 订单金额*代理费率
            BigDecimal agentAmout = submit.multiply(new BigDecimal(agentRate)).setScale(2, BigDecimal.ROUND_HALF_UP);
            UserAmountEntity agentDbAmount = amountService.getUserAmountByUserName(user.getAgentUsername());
            changeAmount(user.getAgentUsername(), user.getAgentId(), agentAmout, agentDbAmount, null, orderId,
                    payType, null);

            //商户金额 =  订单金额 - 介绍人收入
            BigDecimal customerAmount = submit.subtract(submit.multiply(new BigDecimal(introducerRate))).setScale(2,
                    BigDecimal.ROUND_HALF_UP);
            UserAmountEntity customerDbAmount = amountService.getUserAmountByUserName(userName);
            changeAmount(userName, user.getId(), customerAmount, customerDbAmount, user.getAgentId(), orderId,
                    payType, user.getSalesmanUsername());
        }
    }

    /**
     * 更新收入金额和记录流水
     *
     * @param name
     * @param userId
     * @param amount
     * @param userAmountEntity
     * @param agentId
     * @param orderId
     * @param payType
     * @throws Exception
     */
    private synchronized void changeAmount(String name, String userId, BigDecimal amount,
                                           UserAmountEntity userAmountEntity, String agentId, String orderId,
                                           String payType, String saleUserNmae) throws Exception {
        //记录明细
        UserAmountDetail agentDetail = new UserAmountDetail();
        agentDetail.setUserId(userId);
        agentDetail.setUserName(name);
        agentDetail.setType(BaseConstant.RATE);
        agentDetail.setPayType(payType);
        agentDetail.setAmount(amount);
        agentDetail.setOrderId(orderId);
        agentDetail.setCreateTime(new Date());
        BigDecimal initialAmount = new BigDecimal("0.00");
        if (userAmountEntity != null) {
            initialAmount = userAmountEntity.getAmount();
        }
        agentDetail.setInitialAmount(initialAmount);
        agentDetail.setUpdateAmount(amount.add(initialAmount));

        if (!org.springframework.util.StringUtils.isEmpty(agentId)) {
            SysUser agent = userService.getUserById(agentId);
            agentDetail.setAgentId(agentId);
            agentDetail.setAgentUsername(agent.getUsername());
            agentDetail.setAgentRealname(agent.getRealname());
        }
        if (!org.springframework.util.StringUtils.isEmpty(saleUserNmae)) {
            SysUser sale = userService.getUserByName(saleUserNmae);
            agentDetail.setSalesmanId(sale.getId());
            agentDetail.setSalesmanUsername(saleUserNmae);
            agentDetail.setSalesmanRealname(sale.getRealname());
        }
        amountDetailService.save(agentDetail);

        if (userAmountEntity == null) {
            //第一次插入，无值
            userAmountEntity = new UserAmountEntity();
            userAmountEntity.setUserId(userId);
            userAmountEntity.setUserName(name);
            userAmountEntity.setAmount(amount);
            userAmountEntity.setCreateTime(new Date());
            userAmountEntity.setAgentId(agentId);
            amountService.save(userAmountEntity);
        } else {
            amountService.changeAmountByUserName(name, amount);
        }

    }

    @Override
    public int updateOrderStatusBatch(List<String> orderIds) {
        return baseMapper.updateOrderStatusBatch(orderIds);
    }

    @Override
    public Map<String, Object> summary(Wrapper wrapper) {
        return baseMapper.summary(wrapper);
    }

    @Override
    public Map<String, Object> summaryUserTodayOrderAmount(String userId, Date date) {
        Map<String, Object> resultMap = baseMapper.summaryUserTodayOrderAmount(userId, date);
        BigDecimal paidAmount = (BigDecimal) resultMap.get("paidAmount");
        if (paidAmount == null) {
            resultMap.put("paidAmount", BigDecimal.ZERO);
        }
        Long paidCount = (Long) resultMap.get("paidCount");
        if (paidCount == null) {
            resultMap.put("paidCount", 0);
        }
        BigDecimal payFee = (BigDecimal) resultMap.get("payFee");
        if (payFee == null) {
            resultMap.put("payFee", BigDecimal.ZERO);
        }
        return resultMap;
    }

    @Override
    public boolean notifyOrderFinish(String orderId, String payType) throws Exception {
        List<DictModel> notifyUrls = dictService.queryDictItemsByCode(BaseConstant.NOTIFY_ORDER_FINISH_URL);
        String url = null;
        for (DictModel model : notifyUrls) {
            if (payType.equals(model.getText())) {
                url = model.getValue();
                break;
            }
        }
        if (org.springframework.util.StringUtils.isEmpty(url)) {
            throw new RRException("未配置通知挂马平台地址,key={}" + payType);
        }
        OrderInfoEntity order = queryOrderInfoByOrderId(orderId);
        SysUser user = userService.getUserByName(order.getUserName());
        List<UserBusinessEntity> useBusinesses =
                businessEntityService.queryBusinessCodeByUserName(user.getAgentUsername(), payType);
        RequestPayUrl request = factory.getPay(payType);
        return request.notifyOrderFinish(order, key, useBusinesses, url);
    }

    @Override
    public List<String> getOrderByTime(String time) {
        return baseMapper.getOrderByTime(time);
    }

    @Override
    public void updateCustomerIncomeAmount(String orderId, BigDecimal amount) throws Exception {
        OrderInfoEntity order = queryOrderInfoByOrderId(orderId);
        if (order == null) {
            throw new RRException("订单不存在");
        }
        String businessCode = order.getBusinessCode();
        String channelType = order.getPayType();
        String userName = order.getUserName();
        baseMapper.updateCustomerIncomeAmount(userName, businessCode, channelType, amount);
    }

    /**
     * 校验外部订单是否已经创建过
     *
     * @param outerOrderId
     * @return
     */
    private boolean outerOrderIdIsOnly(String outerOrderId) {
        String redisValue = (String) redisUtil.get(outerOrderId);
        log.info("===>从redis中获取订单号，校验订单是否重复，申请的订单号为：{}，redis返回值为：{}", outerOrderId, redisValue);
        //如果redis中存在值，则说明该订单是重复创建的
        if (!org.springframework.util.StringUtils.isEmpty(redisValue)) {
            return false;
        }
        redisUtil.set(outerOrderId, outerOrderId, 10);
        String orderId = baseMapper.queryOrderByOuterOrderId(outerOrderId);
        if (StringUtils.isNotBlank(orderId)) {
            return false;
        }
        return true;
    }

    /**
     * 校验是否在IP黑名单中
     *
     * @param ip
     * @return
     */
    public boolean isIpBlacklist(String ip) {
        List<DictModel> ipBlacklist = dictService.queryDictItemsByCode(BaseConstant.IP_BLACK_LIST);
        List<String> ips = new ArrayList<>();
        if (!CollectionUtils.isEmpty(ipBlacklist)) {
            for (DictModel dictModel : ipBlacklist) {
                ips.add(dictModel.getValue());
            }
            if (!CollectionUtils.isEmpty(ips) && ips.contains(ip)) {
                log.info("ip黑名单为：{}", ips.toArray());
                log.info("创建订单拦截IP --》非法访问ip，ip={}", ip);
                return true;
            }
        }
        return false;
    }

    /**
     * 添加订单信息
     *
     * @param checkParam
     */
    private R addOrder(R checkParam, HttpServletRequest req) throws Exception {
        String ip = (String) checkParam.get(BaseConstant.IP);
        //String ip = IPUtils.getIpAddr(req);
        if (!org.springframework.util.StringUtils.isEmpty(ip) && isIpBlacklist(ip)) {
            throw new RRException("非法访问，请联系管理员");
        }
        String outerOrderId = (String) checkParam.get(BaseConstant.OUTER_ORDER_ID);
        String userName = (String) checkParam.get(BaseConstant.USER_NAME);
        String submitAmount = (String) checkParam.get(BaseConstant.SUBMIT_AMOUNT);
        String payType = (String) checkParam.get(BaseConstant.PAY_TYPE);
        String callbackUrl = (String) checkParam.get(BaseConstant.CALLBACK_URL);
        String agentName = (String) checkParam.get(BaseConstant.AGENT_NAME);
        RequestPayUrl requestPayUrl = (RequestPayUrl) checkParam.get(BaseConstant.REQUEST);
        String requestUrl = (String) checkParam.get(BaseConstant.REQUEST_URL);
        String remark = (String) checkParam.get(BaseConstant.REMARK);
        log.info("请求创建订单，商户单号为:{};通道为：{};用户名为:{};申请金额为:{}", new String[]{outerOrderId, payType, userName,
                submitAmount});
        //校验是否是重复订单
        if (!outerOrderIdIsOnly(outerOrderId)) {
            log.info("该订单已经创建过，无需重复创建;orderid:{}", outerOrderId);
            throw new RRException("该订单已经创建过，无需重复创建" + outerOrderId);
        }
        SysUser user = userService.getUserByName(userName);
        if (!BaseConstant.USER_MERCHANTS.equals(user.getMemberType())) {
            log.info("用户类型不是商户，无法提交订单，用户名为：{}", userName);
            throw new RRException("用户类型不是商户，无法提交订单");
        }
        //校验用户通道是否存在
        if (!channelIsOpen(payType, userName)) {
            log.info("通道未定义，或用户无此通道权限,用户为：{}", userName);
            throw new RRException("通道未定义，或用户无此通道权限,用户为：" + userName);
        }
        //校验在此通道下的该商户对应的代理是否有定义挂码账号
        List<UserBusinessEntity> useBusinesses =
                businessEntityService.queryBusinessCodeByUserName(user.getAgentUsername(), payType);
        if (CollectionUtils.isEmpty(useBusinesses)) {
            log.info("用户:{},无对应商户信息", userName);
            throw new RRException("通道：" + payType + ",未配置账号或账号未激活，请联系管理员");
        }
        UserBusinessEntity userBusinessEntity = null;
        if (useBusinesses.size() == 1) {
            userBusinessEntity = useBusinesses.get(0);
        } else {
            Collections.shuffle(useBusinesses);
            //如果配置的账号包含多个，则需要筛选一个
            for (UserBusinessEntity b : useBusinesses) {
                //如果充值金额为空，或收入金额+本单的金额>充值金额，则不能使用
                BigDecimal incomAmount = b.getIncomeAmount() == null ? new BigDecimal("0.00") : b.getIncomeAmount();
                Double amount =
                        incomAmount.add(new BigDecimal(submitAmount)).setScale(3, BigDecimal.ROUND_HALF_UP).doubleValue();
                if (b.getRechargeAmount() == null || b.getRechargeAmount().doubleValue() < amount) {
                    continue;
                }
                userBusinessEntity = b;
                break;
            }
        }

        if (userBusinessEntity == null) {
            throw new RRException("通道：" + payType + "下，所以账户额度已满，请联系管理员");
        }
        //校验金额的合法性
        checkAmountValidity(userName, submitAmount, payType);
        //校验用户费率是否有填写
        checkRate(user, payType);

        String orderId = generateOrderId();
        OrderInfoEntity order = new OrderInfoEntity();
        BigDecimal amount = new BigDecimal(submitAmount);
        String rate = rateEntityService.getUserRateByUserNameAndAngetCode(userName, agentName, payType);
        BigDecimal poundage = amount.multiply(new BigDecimal(rate)).setScale(2, BigDecimal.ROUND_HALF_UP);
        order.setPoundage(poundage);
        order.setActualAmount(amount.subtract(poundage).setScale(2, BigDecimal.ROUND_HALF_UP));
        order.setOrderId(orderId);
        order.setOuterOrderId(outerOrderId);
        order.setUserName(userName);
        order.setBusinessCode(userBusinessEntity.getBusinessCode());
        order.setSubmitAmount(amount);
        order.setStatus(BaseConstant.ORDER_STATUS_NOT_PAY);
        order.setPayType(payType);
        order.setSuccessCallbackUrl(callbackUrl);
        order.setCreateTime(new Date());
        order.setCreateBy("api");
        order.setParentUser(agentName);
        order.setIp(ip);
        order.setRemark(remark);
        //冗余字段
        order.setUserId(user.getId());
        order.setUserRealname(user.getRealname());
        order.setAgentId(user.getAgentId());
        order.setAgentUsername(user.getAgentUsername());
        order.setAgentRealname(user.getAgentRealname());
        order.setSalesmanId(user.getSalesmanId());
        order.setSalesmanUsername(user.getSalesmanUsername());
        order.setSalesmanRealname(user.getSalesmanRealname());
        //保存订单信息
        this.save(order);
        //请求挂马平台
        return requestPayUrl.requestPayUrl(order, userName, requestUrl, key, innerCallBackUrl, userBusinessEntity);
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
     * 农信易扫请求挂马平台
     *
     * @param order
     * @throws Exception
     */
    private void nxysCallBack(OrderInfoEntity order) throws Exception {
//        NxysCallBackParam nxys = new NxysCallBackParam();
//        nxys.setMerchantid(order.getBusinessCode());
//        nxys.setOrderid(order.getOrderId());
//        nxys.setAmount(order.getSubmitAmount().toString());
//        nxys.setPaytype(order.getPayType());
//        nxys.setClient_ip("");
//        nxys.setNotify_url(innerCallBackUrl);
//        nxys.setReturn_url(innerCallBackUrl);
//        nxys.setExt(order.getOuterOrderId());
//        nxys.setSign(sign(order));
//
//        String param = JSON.toJSONString(nxys);
//        log.info("农信易扫请求挂马后台，入参：{}", param);
//        String url = nxysAliPayUrl;
//        if (order.getPayType().equals(BaseConstant.REQUEST_NXYS_WX)) {
//            url = nxysWxPayUrl;
//        }
//        if (StringUtils.isBlank(url)) {
//            throw new RRException("农信易扫未配置回调挂马地址");
//        }
//        HttpResult result = HttpUtils.doPostJson(url, param);
//        if (result.getCode() == BaseConstant.SUCCESS) {
//            log.info("农信易扫请求挂马后台;四方回调挂马平台成功");
//            JSONObject r = JSONObject.parseObject(result.getBody());
//            if ((int) r.get("code") == 0) {
//                String qrcode = (String) r.get("data");
//                //展示农信易扫的二维码
//            }
//        } else {
//            throw new RRException("农信易扫请求挂马后台;四方回调挂马平台失败");
//        }
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
    private R checkParam(JSONObject reqobj, boolean createOrder, boolean fromInner, boolean isQuery) throws Exception {
        //时间戳
        Long timestamp = reqobj.getLong(BaseConstant.TIMESTAMP);
        //MD5值
        String sign = reqobj.getString(BaseConstant.SIGN);
        //RSA 加密data
        String data = reqobj.getString(BaseConstant.DATA);
        //四方系统用户
        String userName = reqobj.getString(BaseConstant.USER_NAME);
        //备注
        String remark = reqobj.getString(BaseConstant.REMARK);

        Assert.isBlank(sign, "签名不能为空");
        Assert.isBlank(data, "数据不能为空");
        Assert.isBlank(timestamp, "时间戳不能为空");
        Assert.isBlank(userName, "商户不能为空");

        SysUser user = userService.getUserByName(userName);
        if (user == null) {
            log.info("userName参数校验-->用户不存在，username:{}", userName);
            throw new RRException("用户不存在:" + userName);
        }
        if (user.getStatus() != 1) {
            log.info("userName参数校验-->该用户未激活，username:{}", userName);
            throw new RRException("该用户未激活，请联系管理员:" + userName);
        }
        String apiKey = null;
        if (fromInner) {
            apiKey = key;
        } else {
            apiKey = user.getApiKey();
        }
        R decryptData = decryptData(data, userName, timestamp, sign, apiKey);
        if (BaseConstant.CHECK_PARAM_SUCCESS.equals(decryptData.get(BaseConstant.CODE).toString())) {
            JSONObject dataObj = (JSONObject) decryptData.get(BaseConstant.DECRYPT_DATA);
            RequestPayUrl request = null;
            String requestUrl = null;
            if (!isQuery) {
                request = factory.getPay(dataObj.getString(BaseConstant.PAY_TYPE));
                requestUrl = factory.getRequestUrl(dataObj.getString(BaseConstant.PAY_TYPE));
            }
            if (!createOrder) {
                return R.ok().put(BaseConstant.ORDER_ID, dataObj.getString(BaseConstant.ORDER_ID))
                        .put(BaseConstant.USER_NAME, user.getUsername())
                        .put(BaseConstant.AGENT_NAME, user.getAgentUsername())
                        .put(BaseConstant.PAY_TYPE, dataObj.getString(BaseConstant.PAY_TYPE))
                        .put(BaseConstant.REQUEST, request)
                        .put(BaseConstant.REQUEST_URL, requestUrl);
            }
            return R.ok().put(BaseConstant.OUTER_ORDER_ID, dataObj.getString(BaseConstant.OUTER_ORDER_ID))
                    .put(BaseConstant.USER_NAME, dataObj.getString(BaseConstant.USER_NAME))
                    .put(BaseConstant.SUBMIT_AMOUNT, dataObj.getString(BaseConstant.SUBMIT_AMOUNT))
                    .put(BaseConstant.PAY_TYPE, dataObj.getString(BaseConstant.PAY_TYPE))
                    .put(BaseConstant.CALLBACK_URL, dataObj.getString(BaseConstant.CALLBACK_URL))
                    .put(BaseConstant.IP, dataObj.getString(BaseConstant.IP))
                    .put(BaseConstant.AGENT_NAME, user.getAgentUsername())
                    .put(BaseConstant.REQUEST, request)
                    .put(BaseConstant.REQUEST_URL, requestUrl)
                    .put(BaseConstant.REMARK, remark);
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
    private void checkRate(SysUser user, String payType) throws Exception {
        if (StringUtils.isBlank(user.getAgentUsername())) {
            throw new RRException("用户未配置高级代理");
        }
        //商户
        String rate = rateEntityService.getUserRateByUserNameAndAngetCode(user.getUsername(), user.getAgentUsername()
                , payType);
        if (StringUtils.isBlank(rate)) {
            throw new RRException("用户未配置费率，请联系管理员配置");
        }
        if (StringUtils.isNotBlank(user.getSalesmanUsername())) {
            //介绍人不为空
            //介绍人对商户设置的费率
            String introducerRate = rateEntityService.getBeIntroducerRate(user.getUsername(), user.getAgentUsername(),
                    user.getSalesmanUsername(), payType);
            if (StringUtils.isBlank(introducerRate)) {
                throw new RRException("介绍人未对商户设置费率，请联系管理员");
            }
            //代理对介绍人设置的费率
            SysUser sale = userService.getUserByName(user.getSalesmanUsername());
            String agentRate = rateEntityService.getUserRateByUserNameAndAngetCode(user.getSalesmanUsername(),
                    sale.getAgentUsername(), payType);
            if (StringUtils.isBlank(agentRate)) {
                throw new RRException("代理未对介绍人设置费率，请联系管理员");
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
        log.info("===>系统拼接的sign值为：{}", local.toString());
        log.info("===>商户传递的sign值为：{}", sign);
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
    @Override
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
        log.info("===回调商户的签名信息==:{}", sign.toString());
        callbackjson.put(BaseConstant.SIGN, DigestUtils.md5Hex(sign.toString()));
        callbackjson.put(BaseConstant.DATA, data);
        callbackjson.put(BaseConstant.TIMESTAMP, timestamp);
        callbackjson.put(BaseConstant.USER_NAME, order.getUserName());
        callbackjson.put(BaseConstant.REMARK, order.getRemark());
        log.info("====回调商户加密后数据====" + callbackjson);
        return callbackjson;
    }

    public static void main(String[] args) {
//        String a = DigestUtils.md5Hex("zy001" + "1566805086620" + "q39k6ykfJVUo/qDaabvhvNkKdBjrDrbUjqGgw/S" +
//                "/PjR4uspksIZJafy+Ne706Th0UevmQ4qChja6OCqhXZCzvLwe8xuP6P5YqqgsycHfzi8KuQ1UNqr/Zcm0lPevv4K5R" +
//                "/bhHjj0qEyH+VgDNO0C5jS3sAIXuaIyoShraI2eIXwYW8o+Mj9RyLLb/e1OyhxAXX8HPV19xarwMX06v
// /9aBBwLyOPM1dHfTFmyw" +
//                "/fasVc=" + "ec27798b41934764");

        String b = AES128Util.decryptBase64("ewEEEv1xmS+NYpg8DyqzwetQP3gaCCdtqHaA43HR6TpdUoT3J7iT92Umc3ijkzea3YaZLx" +
                "+XF2gU2QgLg1Zr3hSh45K4Y1w0wdr7Fxh932WBzp9ogXx/zyxCF+EOBesI", "a01d43d25c6c41f5");
//        OrderInfoEntity order = new OrderInfoEntity();
//        order.setOrderId("111");
//        order.setOuterOrderId("222");
//        order.setSubmitAmount(new BigDecimal(0.5));
//        order.setStatus(2);
//        JSONObject callobj = new JSONObject();
//        Long timestamp = System.currentTimeMillis();
//        callobj.put(BaseConstant.ORDER_ID, order.getOrderId());
//        callobj.put(BaseConstant.OUTER_ORDER_ID, order.getOuterOrderId());
//        callobj.put(BaseConstant.SUBMIT_AMOUNT, order.getSubmitAmount());
//        callobj.put(BaseConstant.STATUS, order.getStatus());
//
//        log.info("====回调商户加密前数据====" + callobj.toJSONString());
//        //加密数据
//        String data = AES128Util.encryptBase64(callobj.toJSONString(), "aaaa");
//        JSONObject callbackjson = new JSONObject();
//        StringBuilder sign = new StringBuilder();
//        //sign = orderID+outOrderId+submitAmount+timestamp
//        sign.append(order.getOrderId()).append(order.getOuterOrderId()).append(order.getSubmitAmount()).append
// (timestamp);
//        callbackjson.put(BaseConstant.SIGN, DigestUtils.md5Hex(sign.toString()));
//        callbackjson.put(BaseConstant.DATA, data);
//        callbackjson.put(BaseConstant.TIMESTAMP, timestamp);
//        log.info("====回调商户加密后数据====" + callbackjson);
//        Double b = 22985.020;
//        Double c = 22202.050;
//        BigDecimal remain = new BigDecimal(b).subtract(new BigDecimal(c)).setScale(2, BigDecimal.ROUND_HALF_UP);

        System.out.println(b);
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
