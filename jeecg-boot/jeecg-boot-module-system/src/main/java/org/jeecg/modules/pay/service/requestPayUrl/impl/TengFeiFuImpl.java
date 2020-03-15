package org.jeecg.modules.pay.service.requestPayUrl.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.util.DateUtils;
import org.jeecg.common.util.RedisUtil;
import org.jeecg.modules.pay.entity.ChiChengAlipayParam;
import org.jeecg.modules.pay.entity.ChiChengAlipayQueryResult;
import org.jeecg.modules.pay.entity.OrderInfoEntity;
import org.jeecg.modules.pay.entity.UserBusinessEntity;
import org.jeecg.modules.pay.service.IOrderInfoEntityService;
import org.jeecg.modules.pay.service.factory.PayServiceFactory;
import org.jeecg.modules.pay.service.requestPayUrl.RequestPayUrl;
import org.jeecg.modules.plugin.entity.TengFeiFuOrder;
import org.jeecg.modules.plugin.entity.TengFeiFuResponse;
import org.jeecg.modules.system.entity.SysUser;
import org.jeecg.modules.system.service.ISysUserService;
import org.jeecg.modules.util.BaseConstant;
import org.jeecg.modules.util.HttpResult;
import org.jeecg.modules.util.HttpUtils;
import org.jeecg.modules.util.R;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author: wangjianbin
 * @Date: 2020/3/13 20:05
 */
@Service
@Slf4j
public class TengFeiFuImpl implements RequestPayUrl<OrderInfoEntity, String, String, String, String, UserBusinessEntity,
		Object>, InitializingBean {
	@Autowired
	private IOrderInfoEntityService orderInfoEntityService;
	@Autowired
	private RedisUtil redisUtil;
	@Autowired
	private ISysUserService userService;
	@Autowired
	private RequestUrlUtils utils;

	private String apiKey = "b5b71fc279531ce2d7ffc57993fa3a5e";
	private String url = "http://pop099.com/initPayOrder?";
	private String queryUrl = "http://pop099.com/queryOrder?";

	@Override
	public R requestPayUrl(OrderInfoEntity order, String userName, String url, String key, String callbackUrl,
						   UserBusinessEntity userBusiness) throws Exception {
		TengFeiFuOrder o = new TengFeiFuOrder();
		o.setOpenid("5cffa1a9012f78c781ffe3d41bbd1e33");
		o.setOrderNo(order.getOrderId());
		o.setOrderPrice(order.getSubmitAmount().toString());
		o.setOrderTimestamp(String.valueOf(System.currentTimeMillis() / 1000L));
		o.setPayType("ZFB");
		o.setMember_name(order.getUserId());

		o.encode(apiKey);
		String s = HttpUtil.get(url + o.buildStr());
		cn.hutool.json.JSONObject json = JSONUtil.parseObj(s);
		TengFeiFuResponse response = json.toBean(TengFeiFuResponse.class);
		if (StrUtil.isNotBlank(response.getData())) {
			response.setPayurl((String) JSONUtil.parseObj(response.getData()).get("payurl"));
		}

		redisUtil.del(order.getOuterOrderId());
		return R.ok().put("url", response.getPayurl());
	}

	public String md5(String str) throws NoSuchAlgorithmException {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(str.getBytes());
			byte[] byteDigest = md.digest();
			int i;
			//字符数组转换成字符串
			StringBuffer buf = new StringBuffer("");
			for (int offset = 0; offset < byteDigest.length; offset++) {
				i = byteDigest[offset];
				if (i < 0)
					i += 256;
				if (i < 16)
					buf.append("0");
				buf.append(Integer.toHexString(i));
			}
			// 32位加密
			return buf.toString().toUpperCase();
			// 16位的加密
			//return buf.toString().substring(8, 24).toUpperCase();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public boolean orderInfoOk(OrderInfoEntity order, String url, UserBusinessEntity userBusiness)
			throws Exception {
		Map<String, Object> param = new HashMap<>();
		StringBuilder md5buffer = new StringBuilder();
		md5buffer.append("pay_memberid=").append(userBusiness.getBusinessCode())
				.append("&pay_orderid=").append(order.getOrderId())
				.append("&key=").append(userBusiness.getApiKey());
		String md5 = md5(md5buffer.toString()).toUpperCase();
		param.put("pay_memberid", userBusiness.getApiKey());
		param.put("pay_orderid", order.getOrderId());
		param.put("pay_md5sign", md5);
		HttpResult result = HttpUtils.doPost(url, param);
		log.info("==>赤诚支付 查询订单状态，订单号：{}，返回结果：{}", order.getOrderId(), result.getBody());
		ChiChengAlipayQueryResult queryResult = JSON.parseObject(result.getBody(), ChiChengAlipayQueryResult.class);
		if ("SUCCESS".equals(queryResult.getTrade_state())) {
			return true;
		}
		return false;
	}

	@Override
	public boolean notifyOrderFinish(OrderInfoEntity order, String key, UserBusinessEntity userBusiness, String url)
			throws Exception {
		return false;
	}

	private static final String key = "3e9n9v27lrp7pl98t477pwbrjvvpn4lc";

	@Override
	public Object callBack(Object object) throws Exception {
		Map<String, Object> map = (Map<String, Object>) object;
		log.info("==>赤诚支付 回调，回调参数为：{}", map);
		String memberid = (String) map.get("memberid");
		String orderId = (String) map.get("orderid");
		String amount = (String) map.get("amount");
		String transaction_id = (String) map.get("transaction_id");
		String datetime = (String) map.get("datetime");
		String returncode = (String) map.get("returncode");
		String payType = (String) map.get("attach");
		String sign = (String) map.get("sign");
		StringBuilder md5buffer = new StringBuilder();
		md5buffer.append("amount=").append(amount)
				.append("&datetime=").append(datetime)
				.append("&memberid=").append(memberid)
				.append("&orderid=").append(orderId)
				.append("&returncode=").append(returncode)
				.append("&transaction_id=").append(transaction_id)
				.append("&key=").append(key);
		log.info("==>赤诚支付 签名值：{}", md5buffer.toString());
		String md5 = this.md5(md5buffer.toString()).toUpperCase();
		if (!md5.equals(sign)) {
			log.info("==>赤诚支付 签名不匹配，sign:{},md5:{}", sign, md5);
			return "非法访问";
		}
		if (!redisUtil.setIfAbsent("callBack" + orderId, orderId, 30)) {
			return "不能重复回调";
		}
		OrderInfoEntity order = orderInfoEntityService.queryOrderInfoByOrderId(orderId);
		if (order == null || order.getStatus() == 2) {
			return "非法访问";
		}
		order.setStatus(BaseConstant.ORDER_STATUS_SUCCESS_NOT_RETURN);
		SysUser user = userService.getUserByName(order.getUserName());

		R r = orderInfoEntityService.notifyCustomer(order, user, payType);
		if ("0".equals(r.get("code"))) {
			return "OK";
		} else {
			return "fail";
		}
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		PayServiceFactory.register("XTXF", this);
//		PayServiceFactory.registerUrl(BaseConstant.REQUEST_CHICHENG_ALIPAY, utils.getRequestUrl(BaseConstant.REQUEST_CHICHENG_ALIPAY));
	}
}
