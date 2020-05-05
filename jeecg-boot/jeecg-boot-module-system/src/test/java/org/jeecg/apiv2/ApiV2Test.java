package org.jeecg.apiv2;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONObject;
import org.jeecg.common.util.encryption.AES128Util;
import org.jeecg.modules.api.entity.ApiRequestBody;
import org.jeecg.modules.api.entity.PayOrderRequestData;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;

@SpringBootTest
public class ApiV2Test {
	private String username = "阿贵002";       // 对接测试帐号
	private String apiKey = "77f8a1ef73bb4b84";  // 对接测试帐号 密钥

	private String ip = "http://127.0.0.1:8080";
	private String createUrl = ip + "/pay/api/v2/order/create";
	private String queryUrl = ip + "/pay/api/v2/order/query/";

	private String orderId = "df2020e012246546rw827"; // 您系统的订单号，测试时可以更换

	@Test
	public void create() {
		PayOrderRequestData data = new PayOrderRequestData();
		data.setOuterOrderId(orderId);
		data.setProductCode("AlipayScan");
		data.setAmount(new BigDecimal(400));
		data.setClientId("test");
		data.setClientIp("1.1.1.1");
		data.setCallbackUrl("127.0.0.1");


		ApiRequestBody req = new ApiRequestBody();
		req.setUsername(username);
		req.setTimestamp(System.currentTimeMillis());
		req.setRemark("remark");
		req.setData(data.encodeData(apiKey));
		req.setSign(req.sign(apiKey));

		System.out.println("请求信息：" + req.toJsonString());
		String result = HttpUtil.post(createUrl, req.toJsonString());
		System.out.println("返回信息：" + result);
	}

	@Test
	public void query() {
		JSONObject json = new JSONObject();
		json.put("outerOrderId", orderId);
		ApiRequestBody req = new ApiRequestBody();
		req.setUsername(username);
		req.setTimestamp(System.currentTimeMillis());
		req.setData(AES128Util.encryptBase64(json.toJSONString(), apiKey));
		req.setSign(req.sign(apiKey));

		System.out.println("请求信息：" + req.toJsonString());
		String result = HttpUtil.post(queryUrl + orderId, req.toJsonString());
		System.out.println("返回信息：" + result);
	}
}
