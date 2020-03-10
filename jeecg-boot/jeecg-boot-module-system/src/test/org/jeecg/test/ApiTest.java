package org.jeecg.test;

import cn.hutool.http.HttpUtil;
import org.jeecg.modules.df.entity.ApiRequestBody;
import org.jeecg.modules.df.entity.PayOrderData;
import org.jeecg.modules.df.entity.QueryOrderData;
import org.junit.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Set;

/**
 * @author ruanzh
 * @since 2019/11/17
 */
public class ApiTest {

	private String username = "user_test";       // 对接测试帐号
	private String apiKey = "1cd31af85fd94f76";  // 对接测试帐号 密钥

	//	private String ip = "http://47.244.49.200:8080";
	private String ip = "http://127.0.0.1:8080";
	private String createUrl = ip + "/dfxt/api/order/create";
	private String queryUrl = ip + "/dfxt/api/order/query";

	private String orderId = "df20200120101012127827"; // 您系统的订单号，测试时可以更换

	/**
	 * 创建订单接口
	 */
	@Test
	void createOrder() {
		PayOrderData data = new PayOrderData();
		data.setAccountType("1222");
		data.setAccountName("王二");
		data.setAccountNo("1231241241");
		data.setProductCode("alipay");
		data.setBankName("快乐银行");
		data.setBranchName("天上人间分行");
		data.setBizOrderNo(orderId);
		data.setAmount(BigDecimal.TEN);
		data.setCallbackUrl("q3"); //回调地址，若您的系统无需回调，请勿赋值
		data.setIp("1.1.1.1");
		data.setRemark("sssssss");
		data.setBankCode("31211212211");


		ApiRequestBody req = new ApiRequestBody();
		req.setUsername(username);
		req.setTimestamp(System.currentTimeMillis());
		req.setData(data.encodeData(apiKey));
		req.setSign(req.sign(apiKey));

		System.out.println("请求信息：" + req.toJsonString());
		String result = HttpUtil.post(createUrl, req.toJsonString());
		System.out.println("返回信息：" + result);
	}

	/**
	 * 订单查询接口
	 */
	@Test
	void queryOrder() {
		QueryOrderData query = new QueryOrderData();
		query.setBizOrderNo(orderId);

		ApiRequestBody req = new ApiRequestBody();
		req.setUsername(username);
		req.setTimestamp(System.currentTimeMillis());
		req.setData(query.encodeData(apiKey));
		req.setSign(req.sign(apiKey));

		System.out.println("请求信息：" + req.toJsonString());
		String result = HttpUtil.post(queryUrl, req.toJsonString());
		System.out.println("返回信息：" + result);
	}

}
