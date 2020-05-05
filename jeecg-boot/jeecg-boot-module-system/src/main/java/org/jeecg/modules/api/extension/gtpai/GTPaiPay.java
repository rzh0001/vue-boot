package org.jeecg.modules.api.extension.gtpai;

import cn.hutool.http.HttpUtil;
import org.jeecg.common.system.vo.DictModel;
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
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;

@Component("gtpaiAlipay")
public class GTPaiPay implements PayChannelStrategy {
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
	public String callback(OrderInfoEntity orderId, HttpServletRequest req) {
		return null;
	}
}
