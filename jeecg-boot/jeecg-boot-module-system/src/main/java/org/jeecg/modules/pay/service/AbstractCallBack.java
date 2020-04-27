package org.jeecg.modules.pay.service;

import org.jeecg.common.system.vo.DictModel;
import org.jeecg.modules.pay.entity.OrderInfoEntity;
import org.jeecg.modules.system.entity.SysUser;
import org.jeecg.modules.system.service.ISysDictService;
import org.jeecg.modules.system.service.ISysUserService;
import org.jeecg.modules.util.BaseConstant;
import org.jeecg.modules.util.RequestHandleUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.util.CollectionUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @Author: wangjianbin
 * @Date: 2020/4/24 21:30
 */
public abstract class AbstractCallBack implements CallBackService{
    protected Logger log = LoggerFactory.getLogger(getClass());
    @Autowired
    public ISysDictService dictService;
    @Autowired
    public ICallBackService callBackService;
    @Autowired
    private IOrderInfoEntityService orderInfoEntityService;
    @Autowired
    private ISysUserService userService;
    @Override
    public Object callBack(String orderNoField,String payType) throws Exception{
        return executeCallBack(orderNoField,payType);
    }

    protected final Object executeCallBack(String orderNoField,String payType) throws Exception{
        if(!this.checkCallBackIpIsOk(payType)){
            log.info("==>回调IP异常，IP未在白名单范围内");
            return "fail";
        }
        Map<String, Object> param =  this.getParam();
        Map<String,Object> callBackParam = this.getCallBackParam(param);
        String orderNo = (String)callBackParam.get(orderNoField);
        OrderInfoEntity order = orderInfoEntityService.queryOrderInfoByOrderId(orderNo);
        if (order == null || order.getStatus() == 2) {
            log.info("==>通道：{}，回调查询订单信息异常，订单号为：{}",payType,orderNo);
            return "fail";
        }
        String apiKey = callBackService.getApikey(orderNo,payType);
        if(!this.checkOrderStatusIsOK(callBackParam,apiKey)){
            log.info("==>订单状态查询失败");
            return "fail";
        }
        //通知客户
        this.notify(orderNo,payType);
        //回复三方
        return reply(callBackParam,apiKey);
    }

    /**
     * 获取请求参数
     * @return
     */
    public Map<String, Object> getParam(){
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        Object param = RequestHandleUtil.getReqParam(request);
        Map<String, Object> map = (Map<String, Object>) param;
        return map;
    }
    @Async
    public void notify(String orderNo, String payType) throws Exception {
        log.info("==>异步通知商户信息，订单号：{}，通道类型：{}",orderNo,payType);
        OrderInfoEntity order = orderInfoEntityService.queryOrderInfoByOrderId(orderNo);
        if (order == null || order.getStatus() == 2) {
            log.info("==>异步通知商户信息,无订单信息，订单号为：{}",orderNo);
            return;
        }
        order.setStatus(BaseConstant.ORDER_STATUS_SUCCESS_NOT_RETURN);
        SysUser user = userService.getUserByName(order.getUserName());
        orderInfoEntityService.notifyCustomer(order, user, payType);
        log.info("==>异步通知商户信息成功");
    }
    /**
     * 回复三方
     * @param param 三方请求入参
     * @param apiKey 三方秘钥
     * @return
     */
    public abstract Object reply(Map<String, Object> param,String apiKey) throws Exception;

    /**
     * 如果不需要对参数进行转换，直接返回入参的map
     * @param map
     * @return
     */
    public abstract Map<String,Object> getCallBackParam(Map<String,Object> map);

    /**
     * 查询三方订单状态是否成功
     * @param map 三方请求入参
     * @return
     */
    public abstract boolean checkOrderStatusIsOK(Map<String,Object> map,String apiKey) throws Exception;
    /**
     * 校验回调IP
     * @param payType
     * @return
     */
    boolean checkCallBackIpIsOk(String payType){
        String callBackIp = org.jeecg.common.util.IPUtils.getIpAddr(((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest());
        //IP白名单
        List<DictModel> notifyUrls = dictService.queryDictItemsByCode(BaseConstant.CALLBACK_IP_WHITE);
        List<String> ips = null;
        for (DictModel model : notifyUrls) {
            if (payType.equals(model.getText()) && org.apache.commons.lang.StringUtils.isNotBlank(model.getValue())) {
                ips = Arrays.asList(model.getValue().split(","));
                break;
            }
        }
        //如果未配置ip白名单则认为都是白名单
        if(!CollectionUtils.isEmpty(ips) && !ips.contains(callBackIp)){
            log.info("==>回调IP白名单校验不通过，IP白名单为：{},回调IP为：{}",ips,callBackIp);
            return false;
        }
        return true;
    }
}
