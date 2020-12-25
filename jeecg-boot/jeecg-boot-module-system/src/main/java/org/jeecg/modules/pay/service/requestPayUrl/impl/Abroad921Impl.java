package org.jeecg.modules.pay.service.requestPayUrl.impl;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.jeecg.common.system.vo.DictModel;
import org.jeecg.modules.pay.entity.OrderInfoEntity;
import org.jeecg.modules.pay.externalUtils.abroad.AbroadUtils;
import org.jeecg.modules.pay.service.factory.PayServiceFactory;
import org.jeecg.modules.pay.service.requestPayUrl.RequestPayUrl;
import org.jeecg.modules.system.service.ISysDictService;
import org.jeecg.modules.util.BaseConstant;
import org.jeecg.modules.util.HttpResult;
import org.jeecg.modules.util.HttpUtils;
import org.jeecg.modules.util.R;
import org.jeecg.modules.v2.entity.PayBusiness;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class Abroad921Impl implements RequestPayUrl<OrderInfoEntity, String, String, String, String, PayBusiness,
        Object, HttpServletResponse>, InitializingBean {
    @Autowired
    public ISysDictService dictService;

    @Override
    public R requestPayUrl(OrderInfoEntity order, String userName, String url, String key, String callbackUrl, PayBusiness userBusiness,HttpServletResponse response) throws Exception {
        Map<String, Object> data = AbroadUtils.buildParamData(getDomain(),BaseConstant.REQUEST_ABROAD_921,userName,order,userBusiness,"921");
        HttpResult body =  HttpUtils.doPost(url, data);
        String result = body.getBody();
        log.info("请求返还结果：{}",result);
        JSONObject json = JSON.parseObject(result);
        JSONObject resultJson = (JSONObject) json.get("result");
        String payUrl = (String)resultJson.get("payurl");
        return R.ok().put("url", payUrl);

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
    public boolean orderInfoOk(OrderInfoEntity order, String url, PayBusiness userBusiness) throws Exception {
        return false;
    }

    @Override
    public boolean notifyOrderFinish(OrderInfoEntity order, String key, PayBusiness userBusiness, String url) throws Exception {
        return false;
    }

    @Override
    public Object callBack(Object object) throws Exception {
        return null;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        PayServiceFactory.register(BaseConstant.REQUEST_ABROAD_921, this);
    }
}
