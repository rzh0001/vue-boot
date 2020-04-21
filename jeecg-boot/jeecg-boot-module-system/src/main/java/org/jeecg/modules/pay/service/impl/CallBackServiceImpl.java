package org.jeecg.modules.pay.service.impl;

import cn.hutool.crypto.SecureUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.jeecg.modules.pay.entity.OrderInfoEntity;
import org.jeecg.modules.pay.entity.UserBusinessEntity;
import org.jeecg.modules.pay.externalUtils.antUtil.AntUtil;
import org.jeecg.modules.pay.externalUtils.antUtil.GtpaiUtil;
import org.jeecg.modules.pay.service.ICallBackService;
import org.jeecg.modules.pay.service.IOrderInfoEntityService;
import org.jeecg.modules.pay.service.IUserBusinessEntityService;
import org.jeecg.modules.system.entity.SysUser;
import org.jeecg.modules.system.service.ISysUserService;
import org.jeecg.modules.util.BaseConstant;
import org.jeecg.modules.util.R;
import org.jeecg.modules.util.RequestHandleUtil;
import org.jeecg.modules.util.SignatureUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.util.*;

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
	@Autowired
	private IUserBusinessEntityService businessService;

	@Override
	public String callBack4niuNanAlipay() throws Exception {
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
		Object param = RequestHandleUtil.getReqParam(request);
		Map<String, Object> map = (Map<String, Object>) param;
		log.info("==>牛腩支付 回调，回调参数为：{}", map);
		String merCode = (String) map.get("merCode");
		String orderNo = (String) map.get("orderNo");
		String orderAmount = (String) map.get("orderAmount");
		String payDate = (String) map.get("payDate");
		String payCompletionDate = (String) map.get("payCompletionDate");
		String resultCode = (String) map.get("resultCode");
		String resultStatus = (String) map.get("resultStatus");
		String resultMsg = (String) map.get("resultMsg");
		String resultTime = (String) map.get("resultTime");
		String sign = (String) map.get("sign");
		StringBuilder md5buffer = new StringBuilder();
		md5buffer.append("merCode=").append(merCode)
				.append("&orderAmount=").append(orderAmount)
				.append("&orderNo=").append(orderNo)
				.append("&payCompletionDate=").append(payCompletionDate)
				.append("&payDate=").append(payDate)
				.append("&resultCode=").append(resultCode)
				.append("&resultStatus=").append(resultStatus)
				.append("&resultTime=").append(resultTime);
		if (StringUtils.isNotBlank(resultMsg)) {
			md5buffer.append("&resultMsg=").append(resultMsg);
		}
		String apiKey = this.getApikey(orderNo,BaseConstant.REQUEST_NIUNAN_ALIPAY);
		log.info("==>牛腩支付 回调，getApiKey：{}", apiKey);
		md5buffer.append(apiKey);

		log.info("==>牛腩支付 回调，签名：{}", md5buffer.toString());
		String localSign = this.md5Hash(md5buffer.toString());
		log.info("==>牛腩支付 回调，MD5 :{}", localSign);
		if (!localSign.equals(sign)) {
			log.error("==>签名验证不通过，入参sign:{},本地：{}", sign, localSign);
			return "fail";
		}
		return this.notify(orderNo, BaseConstant.REQUEST_NIUNAN_ALIPAY);
	}
	private String getApikey(String orderNo,String type){
		OrderInfoEntity order = orderInfoEntityService.queryOrderInfoByOrderId(orderNo);
		List<UserBusinessEntity> useBusinesses =
			businessService.queryBusinessCodeByUserName(order.getAgentUsername(), type);
		if(CollectionUtils.isEmpty(useBusinesses)){
			log.info("==>查询秘钥异常");
			return "fail";
		}
		UserBusinessEntity business = useBusinesses.get(0);
		return business.getApiKey();
	}
	@Override
	public String callBackTengFeiAlipay() throws Exception {
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
		Object param = RequestHandleUtil.getReqParam(request);
		Map<String, Object> map = (Map<String, Object>) param;
		log.info("==>腾飞支付 回调，回调参数为：{}", map);
		String openid = (String) map.get("openid");
		String orderNo = (String) map.get("orderNo");
		String tradeNo = (String) map.get("tradeNo");
		String orderPrice = (String) map.get("orderPrice");
		String sign = (String) map.get("sign");
		StringBuilder md5buffer = new StringBuilder();
		md5buffer.append("openid=").append(openid)
				.append("&orderNo=").append(orderNo)
				.append("&orderPrice=").append(orderPrice)
				.append("&tradeNo=").append(tradeNo).append("&key=b5b71fc279531ce2d7ffc57993fa3a5e");
		log.info("==>腾飞支付 签名：{}", md5buffer.toString());
		String localSign = SecureUtil.md5(md5buffer.toString()).toUpperCase();
		if (!sign.equals(localSign)) {
			log.info("==>签名不匹配，localSign：{}；sign：{}", localSign, sign);
			return "fail";
		}
		notify1(orderNo);
		log.info("回复success");
		return "success";
	}

	public static final String LETIAN_KEY = "08966fd914e9c23b8c3c99c039466835cf";

	@Override
	public String callBackLeTianAlipay() throws Exception {
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
		Object param = RequestHandleUtil.getReqParam(request);
		Map<String, String> map = (Map<String, String>) param;
		log.info("==>乐天支付 回调，回调参数为：{}", map);
		String sign = map.get("Signature");
		String outTradeNo = map.get("outTradeNo");
		map.remove("Signature");
		String localSign = SignatureUtils.signature(LETIAN_KEY, map);
		if (!localSign.equals(sign)) {
			log.info("=>乐天支付，签名校验失败,入参签名：{}，本地签名：{}", sign, localSign);
			return "fail";
		}
		return this.notify(outTradeNo, BaseConstant.REQUEST_LETIAN_ALIPAY);
	}

	@Override
	public String callBackAntAlipay() throws Exception {
		Map<String, String> map = getParam();
		log.info("==>蚁支付，回调参数为：{}",map);
		String sign = map.get("sign");
		String orderNo = map.get("out_trade_no");
		String apiKey = this.getApikey(orderNo,BaseConstant.REQUEST_ANT_ALIPAY);
		map.remove("sign");
		map.remove("code");
		map.remove("msg");
		map.remove("sub_code");
		map.remove("sub_msg");
		String localSign = AntUtil.generateSignature(map,apiKey);
		if(!localSign.equals(sign)){
			log.info("==>蚁支付，回调签名为：{}，本地签名为：{}",sign,localSign);
			return "签名验证不通过";
		}
		return this.notify(orderNo, BaseConstant.REQUEST_ANT_ALIPAY);
	}

	@Override
	public String callBackGtpaiAlipay() throws Exception {
		Map<String, String> map = getParam();
		log.info("==>GT派支付，回调参数为：{}",map);
		String sign = map.get("sign");
		String orderNo = map.get("out_trade_no");
		String apiKey = this.getApikey(orderNo,BaseConstant.REQUEST_GTPAI_ALIPAY);
		String retCode = map.get("ret_code");
		map.remove("sign");

		String localSign = GtpaiUtil.generateSignature(map,apiKey);
		if(!localSign.equals(sign)){
			log.info("==>GT派支付，回调签名为：{}，本地签名为：{}",sign,localSign);
			return "签名验证不通过";
		}

		if(!retCode.equals("00")){
			log.info("==>GT派支付，返回码：{}",retCode);
			return "sucess";
		}

		return this.notify(orderNo, BaseConstant.REQUEST_GTPAI_ALIPAY);
	}

	private Map<String, String> getParam(){
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
		Object param = RequestHandleUtil.getReqParam(request);
		Map<String, String> map = (Map<String, String>) param;
		return map;
	}
	public static void main(String[] args) {
		Map<String, String> map = new HashMap<>();
		map.put("a", "a");
		map.put("b", "b");
		map.remove("a");
		System.out.println(map);
	}

	@Async
	void notify1(String orderNo) throws Exception {
		log.info("异步通知notify1开始");
		OrderInfoEntity order = orderInfoEntityService.queryOrderInfoByOrderId(orderNo);
		order.setStatus(BaseConstant.ORDER_STATUS_SUCCESS_NOT_RETURN);
		SysUser user = userService.getUserByName(order.getUserName());
		R r = orderInfoEntityService.notifyCustomer(order, user, BaseConstant.REQUEST_TENGFEI_ALIPAY);
		log.info("异步通知notify1结束");
	}

	private String notify(String orderNo, String payType) throws Exception {
		OrderInfoEntity order = orderInfoEntityService.queryOrderInfoByOrderId(orderNo);
		if (order == null || order.getStatus() == 2) {
			return "非法访问";
		}
		order.setStatus(BaseConstant.ORDER_STATUS_SUCCESS_NOT_RETURN);
		SysUser user = userService.getUserByName(order.getUserName());
		orderInfoEntityService.notifyCustomer(order, user, payType);
		return "success";
	}

	/**
	 * MD5
	 *
	 * @param dataStr
	 * @return
	 */
	public String md5Hash(String dataStr) {
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
