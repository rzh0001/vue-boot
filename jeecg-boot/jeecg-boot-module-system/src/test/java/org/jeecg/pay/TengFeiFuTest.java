package org.jeecg.pay;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import org.jeecg.modules.plugin.entity.TengFeiFuOrder;
import org.jeecg.modules.plugin.entity.TengFeiFuQuery;
import org.jeecg.modules.plugin.entity.TengFeiFuResponse;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.*;

@SpringBootTest
public class TengFeiFuTest {
	private String openId = "5cffa1a9012f78c781ffe3d41bbd1e33";
	private String apiKey = "b5b71fc279531ce2d7ffc57993fa3a5e";

	private String url = "http://pop099.com/initPayOrder?";
	private String queryUrl = "http://pop099.com/queryOrder?";

	private String orderId = "sf1a343562789";

	@Test
	public void createOrder() {

		TengFeiFuOrder order = new TengFeiFuOrder();

		order.setMember_name("abcd");
		order.setOpenid(openId);
		order.setNotifyUrl("mmm");
		order.setOrderNo(orderId);
		order.setOrderPrice("600.2");
		order.setOrderTimestamp(String.valueOf(System.currentTimeMillis() / 1000L));
		order.setPayType("WX");
		order.setRemark("test");

		order.encode(apiKey);
		String s = HttpUtil.get(url + order.buildStr());
		JSONObject json = JSONUtil.parseObj(s);
		TengFeiFuResponse response = json.toBean(TengFeiFuResponse.class);
		if (StrUtil.isNotBlank(response.getData())) {
			response.setPayurl((String) JSONUtil.parseObj(response.getData()).get("payurl"));
		}

		System.out.println(s);
		System.out.println(url + order.buildStr());

	}

	@Test
	public void queryOrder() {

		TengFeiFuQuery query = new TengFeiFuQuery();
		query.setOpenid(openId);
		query.setOrderNo(orderId);
		query.encode(apiKey);
		String s = HttpUtil.get(queryUrl + query.buildStr());
		System.out.println(s);

	}

}
