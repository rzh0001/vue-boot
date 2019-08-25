package org.jeecg.modules.pay.service.requestPayUrl.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.modules.exception.RRException;
import org.jeecg.modules.pay.entity.AliPayCallBackParam;
import org.jeecg.modules.pay.service.requestPayUrl.RequestPayUrl;
import org.jeecg.modules.util.*;
import org.springframework.stereotype.Service;

@Service
@Slf4j
/**
 * 支付宝转卡请求挂码
 */
public class AliPayImpl implements RequestPayUrl<AliPayCallBackParam,String,String> {

    @Override
    public R requestPayUrl(AliPayCallBackParam param, String url,String key) throws Exception {
        if (StringUtils.isBlank(url)) {
            throw new RRException("未配置支付宝回调地址，请联系管理员配置回调地址");
        }
        log.info("四方回调挂马平台，加密前数据，url:{};param:{}", url, JSON.toJSONString(param));

        String data = AES128Util.encryptBase64(JSON.toJSONString(param), key);

        JSONObject p = new JSONObject();
        p.put("data", data);
        log.info("四方回调挂马平台，加密后数据，url:{};param:{}", url, p.toJSONString());
        HttpResult result = HttpUtils.doPostJson(url, p.toJSONString());
        String payUrl = null;
        if (result.getCode() == BaseConstant.SUCCESS) {
            if (StringUtils.isNotBlank(result.getBody())) {
                log.info("四方回调挂马平台成功，返回信息：{}", result.getBody());
                JSONObject r = JSON.parseObject(result.getBody());
                if (r != null) {
                    if ("200".equals(r.get("code").toString())) {
                        payUrl = (String) r.get("msg");
                        log.info("===请求挂码平台，返回支付链接为:{}", payUrl);
                    }else{
                        throw new RRException("四方回调挂马平台失败,订单创建失败：" + result.getBody());
                    }
                } else {
                    throw new RRException("四方回调挂马平台失败,订单创建失败：" + result.getBody());
                }
            } else {
                throw new RRException("四方回调挂马平台失败,订单创建失败：" + result.getBody());
            }
        } else {
            throw new RRException("四方回调挂马平台失败,订单创建失败：" + result.getBody());
        }
        return R.ok().put("url", payUrl);
    }
}
