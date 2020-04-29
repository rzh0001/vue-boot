package org.jeecg.modules.api.extension.alibank;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONObject;
import org.jeecg.common.system.vo.DictModel;
import org.jeecg.common.util.encryption.AES128Util;
import org.jeecg.modules.api.constant.PayTypeEnum;
import org.jeecg.modules.api.extension.PayChannelStrategy;
import org.jeecg.modules.pay.entity.OrderInfoEntity;
import org.jeecg.modules.system.service.ISysDictService;
import org.jeecg.modules.util.BaseConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component("ali_bank")
public class AliBankPay implements PayChannelStrategy {
	@Autowired
	public ISysDictService dictService;

	/**
	 * 请求挂码平台的秘钥
	 */
	private String apiKey = null;

	private String serverUrl = null;
	/**
	 * 挂码平台回调四方的地址
	 */
	private static String innerCallBackUrl = null;

	@PostConstruct
	public void init() {
		List<DictModel> apiKeyML = dictService.queryDictItemsByCode(BaseConstant.API_KEY);
		Optional<DictModel> apiKeyModel = apiKeyML.stream().filter(model -> BaseConstant.API_KEY.equals(model.getText())).findFirst();
		apiKeyModel.ifPresent(dictModel -> apiKey = dictModel.getValue());

		List<DictModel> innerUrlML = dictService.queryDictItemsByCode(BaseConstant.INNER_CALL_BACK_URL);
		Optional<DictModel> innerUrlModel = innerUrlML.stream().filter(model -> BaseConstant.INNER_CALL_BACK_URL.equals(model.getText())).findFirst();
		innerUrlModel.ifPresent(dictModel -> innerCallBackUrl = dictModel.getValue());

		List<DictModel> payUrl = dictService.queryDictItemsByCode(BaseConstant.REQUEST_URL);
		Optional<DictModel> urlModel = payUrl.stream().filter(model -> PayTypeEnum.ALI_BANK.getValue().equals(model.getText())).findFirst();
		urlModel.ifPresent(dictModel -> serverUrl = dictModel.getValue());
	}

	@Override
	public String pay(OrderInfoEntity orderInfo) {
		AliBankRequestBody body = generate(orderInfo);
		String data = AES128Util.encryptBase64(body.toJsonString(), apiKey);
		JSONObject p = new JSONObject();
		p.put("data", data);
		String response = HttpUtil.post(serverUrl, p);
		return response;
	}

	@Override
	public String callback(HttpServletRequest req) {
		// 1.从HttpServletRequest获取数据
		Map<String, String[]> parameterMap = req.getParameterMap(); //获取以form提交的数据
		JSONObject json = getJson(req); // 获取以json提交的数据

		return null;
	}

	private AliBankRequestBody generate(OrderInfoEntity orderInfo) {
		AliBankRequestBody body = new AliBankRequestBody();
		body.setAccount_id(orderInfo.getBusinessCode());
		body.setContent_type("text");
		if (orderInfo.getPayType().equals(BaseConstant.REQUEST_ALI_ZZ)) {
			body.setThoroughfare("alipay_auto");
			body.setPayType(BaseConstant.REQUEST_ALI_ZZ);
		} else if (orderInfo.getPayType().equals(BaseConstant.REQUEST_ALI_BANK)) {
			body.setThoroughfare("jdpay_auto");
			body.setPayType(BaseConstant.REQUEST_ALI_BANK);
		} else if (orderInfo.getPayType().equals(BaseConstant.REQUEST_WECHAT_BANK)) {
			body.setThoroughfare("wechat_auto");
			body.setPayType(BaseConstant.REQUEST_WECHAT_BANK);
		} else if (orderInfo.getPayType().equals(BaseConstant.REQUEST_ALI_TT)) {
			body.setThoroughfare("alipay_auto");
			body.setPayType(BaseConstant.REQUEST_ALI_TT);
		}

		body.setOut_trade_no(orderInfo.getOrderId());
		body.setType("3");
		body.setRobin("2");
		body.setKeyId("");
		body.setAmount(orderInfo.getSubmitAmount().toString());
		body.setCallback_url(innerCallBackUrl);
		body.setUserName(orderInfo.getUserName());
		body.setSign(body.sign(orderInfo, apiKey));
		return body;
	}

	public JSONObject getJson(HttpServletRequest req) {
		String s = null;
		try {
			BufferedReader reader = req.getReader();
			Stream<String> lines = reader.lines();
			s = lines.collect(Collectors.joining());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return JSONObject.parseObject(s);
	}
}
