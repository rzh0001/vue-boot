package org.jeecg.modules.api.extension.gm;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.common.system.vo.DictModel;
import org.jeecg.common.util.encryption.AES128Util;
import org.jeecg.modules.api.constant.PayTypeEnum;
import org.jeecg.modules.api.exception.AccountAbnormalException;
import org.jeecg.modules.api.exception.BusinessException;
import org.jeecg.modules.api.extension.PayChannelStrategy;
import org.jeecg.modules.pay.entity.OrderInfoEntity;
import org.jeecg.modules.pay.entity.UserBusinessEntity;
import org.jeecg.modules.pay.service.IOrderInfoEntityService;
import org.jeecg.modules.pay.service.IOrderToolsService;
import org.jeecg.modules.pay.service.IUserBusinessEntityService;
import org.jeecg.modules.pay.service.impl.AsyncNotifyServiceImpl;
import org.jeecg.modules.system.service.ISysDictService;
import org.jeecg.modules.util.BaseConstant;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * GM支付接口
 */
@Slf4j
public class GMPay implements PayChannelStrategy {
	@Autowired
	private ISysDictService dictService;

	@Autowired
	private IUserBusinessEntityService businessService;

	@Autowired
	private IOrderToolsService orderTools;

	@Autowired
	private IOrderInfoEntityService orderService;
	@Autowired
	private AsyncNotifyServiceImpl asyncNotify;

	private String gmUrl = null;

	private String payType;
	/**
	 * 挂码平台回调四方的地址
	 */
	private static String innerCallBackUrl = null;

	@PostConstruct
	public void init() {

		List<DictModel> innerUrlML = dictService.queryDictItemsByCode(BaseConstant.INNER_CALL_BACK_URL);
		Optional<DictModel> innerUrlModel = innerUrlML.stream().filter(model -> BaseConstant.INNER_CALL_BACK_URL.equals(model.getText())).findFirst();
		innerUrlModel.ifPresent(dictModel -> innerCallBackUrl = dictModel.getValue());

		List<DictModel> payUrl = dictService.queryDictItemsByCode(BaseConstant.REQUEST_URL);
		Optional<DictModel> urlModel = payUrl.stream().filter(model -> PayTypeEnum.ALI_BANK.getValue().equals(model.getText())).findFirst();
		urlModel.ifPresent(dictModel -> gmUrl = dictModel.getValue());
	}

	@Override
	public String pay(OrderInfoEntity orderInfo) {
		UserBusinessEntity userChannelConfig = businessService.getUserChannelConfig(orderInfo.getAgentUsername(), orderInfo.getPayType());
		String gmApiKey = userChannelConfig.getApiKey();
		if (StringUtils.isBlank(gmApiKey)) {
			log.error("通道未配置apikey");
			throw new AccountAbnormalException("通道未配置apikey");
		}
		String[] keys = gmApiKey.split("=");
		if (keys.length != 2) {
			log.error("通道未配置apikey");
			throw new AccountAbnormalException("通道未配置apikey");
		}
		String md5Key = keys[0];
		String aesKey = keys[1];

		GMRequestData data = new GMRequestData();
		data.setAgentorderid(orderInfo.getOrderId());
		data.setApplyamount(String.valueOf(orderInfo.getSubmitAmount()));
		data.setOrderchannel(6);
		data.setWeb_username(orderInfo.getUserName());
		data.setCallbackurl(orderTools.generateCallbackUrl(orderInfo));

		GMRequestBody body = new GMRequestBody();
		body.setAgentcode(orderInfo.getBusinessCode());
		body.setData(data.encrypt(aesKey));
		body.setSign(body.sign(md5Key));

		log.info("订单[{}]向GM[{}]发送请求[{}]", orderInfo.getOrderId(), gmUrl, body.toJsonString());
		String response = HttpUtil.post(gmUrl, body.toJsonString());
		log.info("订单[{}]接受响应[{}]", orderInfo.getOrderId(), response);

		// 处理响应
		GMResponse gmResponse = JSONObject.parseObject(response, GMResponse.class);
		if (gmResponse.getCode() != 0) {
			throw BusinessException.Fuck("支付连接生成失败，请联系管理员。失败原因：{}", gmResponse.getMsg());
		}
		return gmResponse.getPayurl();
	}

	@Override
	public String callback(String orderId, HttpServletRequest req) {
		// 1.从HttpServletRequest获取数据
//		Map<String, String[]> parameterMap = req.getParameterMap(); //获取以form提交的数据
		String jsonStr = getJsonString(req); // 获取以json提交的数据
		GMCallbackBody body = JSONObject.parseObject(jsonStr, GMCallbackBody.class);
		// 验签
		UserBusinessEntity userChannelConfig = businessService.getUserChannelConfig(body.getUsername(), payType);
		String gmApiKey = userChannelConfig.getApiKey();
		if (StringUtils.isBlank(gmApiKey)) {
			log.error("通道未配置apikey");
			throw new AccountAbnormalException("通道未配置apikey");
		}
		String[] keys = gmApiKey.split("=");
		if (keys.length != 2) {
			log.error("通道未配置apikey");
			throw new AccountAbnormalException("通道未配置apikey");
		}
		String md5Key = keys[0];
		String aesKey = keys[1];

		body.checkSign(md5Key);

		String s = AES128Util.decryptBase64(body.getData(), aesKey);
		GMCallbackData data = JSONObject.parseObject(s, GMCallbackData.class);
		if (data == null) {
			throw BusinessException.Fuck("data数据解密失败");
		}
		if (orderId.compareTo(data.getOrderId()) != 0) {
			throw BusinessException.Fuck("orderId 校验失败");
		}
		OrderInfoEntity orderInfo = orderService.queryOrderInfoByOrderId(orderId);
		if (orderInfo == null) {
			throw BusinessException.Fuck("订单不存在");
		}
		if (orderInfo.getStatus() == 2) {
			throw BusinessException.Fuck("订单已完成");
		}

		//异步通知客户
		try {
			asyncNotify.asyncNotify(orderId, payType);
		} catch (Exception e) {
			log.error("订单[{}]异步通知客户失败", orderId);
			e.printStackTrace();
		}

		JSONObject json = new JSONObject();
		json.put("code", "200");

		return json.toJSONString();
	}


	public String getJsonString(HttpServletRequest req) {
		String s = null;
		try {
			BufferedReader reader = req.getReader();
			Stream<String> lines = reader.lines();
			s = lines.collect(Collectors.joining());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return s;
	}
}
