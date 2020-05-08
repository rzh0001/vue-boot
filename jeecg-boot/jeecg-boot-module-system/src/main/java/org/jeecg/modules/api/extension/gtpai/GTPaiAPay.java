package org.jeecg.modules.api.extension.gtpai;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.jeecg.common.system.vo.DictModel;
import org.jeecg.modules.api.extension.APayChannelStrategy;
import org.jeecg.modules.pay.entity.OrderInfoEntity;
import org.jeecg.modules.pay.entity.UserBusinessEntity;
import org.jeecg.modules.pay.externalUtils.antUtil.GtpaiUtil;
import org.jeecg.modules.pay.service.IOrderInfoEntityService;
import org.jeecg.modules.pay.service.IOrderToolsService;
import org.jeecg.modules.pay.service.IUserBusinessEntityService;
import org.jeecg.modules.pay.service.impl.AsyncNotifyServiceImpl;
import org.jeecg.modules.system.service.ISysDictService;
import org.jeecg.modules.util.BaseConstant;
import org.jeecg.modules.util.HttpResult;
import org.jeecg.modules.util.HttpUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

@Component("gtpaiAlipay")
public class GTPaiAPay extends APayChannelStrategy {
	@Autowired
	private IUserBusinessEntityService businessService;

	@Autowired
	private IOrderToolsService orderTools;

	@Autowired
	private IOrderInfoEntityService orderService;
	@Autowired
	private AsyncNotifyServiceImpl asyncNotify;

	@Autowired
	private ISysDictService dictService;

	private String serverUrl = null;

	@PostConstruct
	public void init() {
		List<DictModel> payUrl = dictService.queryDictItemsByCode(BaseConstant.REQUEST_URL);
		Optional<DictModel> urlModel = payUrl.stream().filter(model -> "gtpaiAlipay".equals(model.getText())).findFirst();
		urlModel.ifPresent(dictModel -> serverUrl = dictModel.getValue());
	}

	@Override
	public String pay(OrderInfoEntity orderInfo) {
		UserBusinessEntity userChannelConfig = orderTools.getUserChannelConfig(orderInfo);

		GTPaiRequestBody body = new GTPaiRequestBody();
		body.setMch_id(orderInfo.getBusinessCode());
		body.setTrans_amt(orderInfo.getSubmitAmount().toString());
		body.setOut_trade_no(orderInfo.getOrderId());
		body.setNotify_url(orderInfo.getSuccessCallbackUrl());
		body.setSign(body.sign(userChannelConfig.getApiKey()));

		String resp = HttpUtil.post(serverUrl, body.toJsonString());
		String[] list = resp.split("'");
		String payUrl = list[1].split("'")[0];
		return payUrl;
	}

	@Override
	public Object reply() throws Exception {
		return "success";
	}

	@Override
	public boolean checkSign(Map<String, Object> param, String apiKey) throws Exception {
		log.info("==>GT派支付，回调参数为：{}", param);
		String sign = (String)param.get("sign");
		param.remove("sign");
		Map<String, Object> sortedMap = new TreeMap<String, Object>(param);
		String localSign = GtpaiUtil.generateSignature(sortedMap, apiKey);
		if (!localSign.equals(sign)) {
			log.info("==>GT派支付，回调签名为：{}，本地签名为：{}", sign, localSign);
			return false;
		}
		return true;
	}

	@Override
	public Map<String, Object> getCallBackParam(Map<String, Object> map) {
		String json = (String)map.get("reqData");
		Map<String, Object> param = JSON.parseObject(json);
		return param;
	}

	@Override
	public boolean checkOrderStatusIsOK(Map<String, Object> param, String apiKey) throws Exception {
		// 查询订单状态
		TreeMap<String, Object> queryMap = new TreeMap<String, Object>();
		queryMap.put("mch_id", (String)param.get("mch_id"));
		String orderNo = (String)param.get("out_trade_no");
		queryMap.put("out_trade_no", orderNo);
		queryMap.put("store_id", (String)param.get("store_id"));
		String querySign = GtpaiUtil.generateSignature(queryMap, apiKey);
		queryMap.put("sign", querySign);
		String paramString = JSON.toJSONString(queryMap);
		TreeMap<String, Object> tmpMap = new TreeMap<String, Object>();
		tmpMap.put("reqData", paramString);
		log.info("==>GT派支付支付宝，查询签名为：{} 查询参数为：{}", querySign, tmpMap);
		HttpResult result = HttpUtils.doPost("http://gttffp.com:8089/zhifpops/shOrderQuery", tmpMap);
		String body = result.getBody();
		log.info("==>GT派支付支付宝，查询返回结果为：{}", body);
		JSONObject queryRet = JSON.parseObject(body);
		if (!queryRet.getString("ret_code").equals("00")) {
			log.info("==>GT派支付，查询订单状态失败");
			return false;
		}
		return true;
	}

}
