package org.jeecg.modules.util;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.RandomUtil;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.jeecg.modules.wallet.dto.WalletHttpRequestBody;
import org.jeecg.modules.wallet.dto.WalletHttpRequestParam;
import org.jeecg.modules.wallet.dto.WalletHttpResponse;

import java.util.List;

/**
 * @Author: wangjianbin
 * @Date: 2020/10/13 16:57
 */
@Slf4j
public class WalletHttpRequestUtils {
    public static final String MERCHID = "301555";
    public static final String KEY = "daf33649515f92c2b7d8f76d53055d08";
    public static final String URL = "https://hk01-node.uduncloud.com/mch/address/create";
    public static String getWalletUrl(String requestUrl,String key,String merId,String coinType, String callBackUrl) throws Exception {
        WalletHttpRequestParam param = new WalletHttpRequestParam();
        param.setTimestamp(System.currentTimeMillis());
        param.setNonce(RandomUtil.randomLong(6));
        List<WalletHttpRequestBody> bodys = Lists.newArrayList();
        WalletHttpRequestBody body =
            WalletHttpRequestBody.builder().callUrl(callBackUrl).coinType(Integer.parseInt(coinType)).merchantId(merId).build();
        bodys.add(body);
        param.setBody(JSONObject.toJSONString(bodys));
        //sign=md5(body + key + nonce + timestamp)
        String bodyJson = JSONObject.toJSONString(bodys);
        StringBuilder signStr = new StringBuilder();
        signStr.append(bodyJson).append(key).append(param.getNonce()).append(param.getTimestamp());
        String sign = DigestUtils.md5Hex(signStr.toString());
        log.info("签名字符串：{}，签名值：{}",signStr.toString(),sign);
        param.setSign(sign);
        String jsonParam = JSONObject.toJSONString(param);
        HttpResult result = HttpUtils.doPostJson(requestUrl,jsonParam);
        String resultJson = result.getBody();
        log.info("请求钱包地址，返回code：{},body:{}",result.getCode(),resultJson);
        WalletHttpResponse  response = JSONObject.parseObject(resultJson,WalletHttpResponse.class);
        if(response.getCode() == 200){
            return response.getData().getAddress();
        }
        return null;
    }

}
