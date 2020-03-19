package org.jeecg.modules.pay.service.requestPayUrl.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.system.vo.DictModel;
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
import org.jeecg.modules.system.service.ISysDictService;
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
import java.util.List;
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
	private RedisUtil redisUtil;
	@Autowired
	private RequestUrlUtils utils;
	@Autowired
	public ISysDictService dictService;
	private static final String CALLBACK_URL="/callBack/tengfeiAlipay";

	@Override
	public R requestPayUrl(OrderInfoEntity order, String userName, String url, String key, String callbackUrl,
						   UserBusinessEntity userBusiness) throws Exception {
		TengFeiFuOrder o = new TengFeiFuOrder();
		o.setOpenid(userBusiness.getBusinessCode());
		o.setOrderNo(order.getOrderId());
		o.setOrderPrice(order.getSubmitAmount().toString());
		o.setOrderTimestamp(String.valueOf(System.currentTimeMillis() / 1000L));
		o.setPayType("ZFB");
		o.setMember_name(order.getUserId());
		o.setNotifyUrl(getDomain()+CALLBACK_URL);
		o.encode(userBusiness.getApiKey());
		log.info("==>腾飞支付 请求入参为：{}",o);
		String s = HttpUtil.get(url + o.buildStr());
		log.info("==>腾飞支付 返回结果：{}",s);
		cn.hutool.json.JSONObject json = JSONUtil.parseObj(s);
		TengFeiFuResponse response = json.toBean(TengFeiFuResponse.class);
		if (StrUtil.isNotBlank(response.getData())) {
			response.setPayurl((String) JSONUtil.parseObj(response.getData()).get("payurl"));
		}
		redisUtil.del(order.getOuterOrderId());
		return R.ok().put("url", response.getPayurl());
	}
	private String getDomain(){
		String domain = null;
		List<DictModel> apiKey = dictService.queryDictItemsByCode(BaseConstant.DOMAIN);
		for (DictModel k : apiKey) {
			if (BaseConstant.DOMAIN.equals(k.getText())) {
				domain = k.getValue();
			}
		}
		return domain;
	}

	@Override
	public boolean orderInfoOk(OrderInfoEntity order, String url, UserBusinessEntity userBusiness)
			throws Exception {
		return false;
	}

	@Override
	public boolean notifyOrderFinish(OrderInfoEntity order, String key, UserBusinessEntity userBusiness, String url)
			throws Exception {
		return false;
	}

	@Override
	public Object callBack(Object object) throws Exception {
		return null;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		PayServiceFactory.register(BaseConstant.REQUEST_TENGFEI_ALIPAY, this);
		PayServiceFactory.registerUrl(BaseConstant.REQUEST_TENGFEI_ALIPAY, utils.getRequestUrl(BaseConstant.REQUEST_TENGFEI_ALIPAY));
	}
}
