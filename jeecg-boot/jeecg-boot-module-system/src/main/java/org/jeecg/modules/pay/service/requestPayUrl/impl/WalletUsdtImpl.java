package org.jeecg.modules.pay.service.requestPayUrl.impl;

import org.apache.commons.lang3.StringUtils;
import org.jeecg.common.system.vo.DictModel;
import org.jeecg.common.util.RedisUtil;
import org.jeecg.common.wallet.CoinType;
import org.jeecg.modules.pay.entity.OrderInfoEntity;
import org.jeecg.modules.pay.service.factory.PayServiceFactory;
import org.jeecg.modules.pay.service.requestPayUrl.RequestPayUrl;
import org.jeecg.modules.system.service.ISysDictService;
import org.jeecg.modules.util.BaseConstant;
import org.jeecg.modules.util.R;
import org.jeecg.modules.util.WalletHttpRequestUtils;
import org.jeecg.modules.v2.entity.PayBusiness;
import org.jeecg.modules.wallet.service.WalletService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @Author: wangjianbin
 * @Date: 2020/10/14 15:05
 */
public class WalletUsdtImpl
    implements RequestPayUrl<OrderInfoEntity, String, String, String, String, PayBusiness, Object>, InitializingBean {
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private RequestUrlUtils utils;
    @Autowired
    public ISysDictService dictService;

    @Autowired
    public WalletService walletService;

    private static final String CALLBACK_URL="/wallet/callback";

    private static final String PAY_URL="/api/wallte/";
    @Override
    public R requestPayUrl(OrderInfoEntity order, String userName, String url, String key, String callbackUrl,
        PayBusiness userBusiness) throws Exception {
        //钱包地址
       String walletUrl = walletService.findFreeWalletUrl(CoinType.USDT.getCode());
       if(StringUtils.isBlank(walletUrl)){
           walletUrl = walletService.createWalletUrl(url,key,userBusiness.getBusinessCode(),CoinType.USDT.getCode(),getDomain()+CALLBACK_URL);
       }
       //币种数量
        String coinAmount = walletService.transformCoinByAmount(CoinType.USDT.getCode(),order.getSubmitAmount().toString());
        redisUtil.del(order.getOuterOrderId());
        return R.ok().put("url", getGuamaDomain()+PAY_URL+walletUrl+"/"+coinAmount);
    }
    private String getGuamaDomain(){
        String domain = null;
        List<DictModel> apiKey = dictService.queryDictItemsByCode(BaseConstant.GUAMA_DOMAIN);
        for (DictModel k : apiKey) {
            if (BaseConstant.GUAMA_DOMAIN.equals(k.getText())) {
                domain = k.getValue();
            }
        }
        return domain;
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
    public boolean notifyOrderFinish(OrderInfoEntity order, String key, PayBusiness userBusiness, String url)
        throws Exception {
        return false;
    }

    @Override
    public Object callBack(Object object) throws Exception {
        return null;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        PayServiceFactory.register(BaseConstant.REQUEST_WALLET_USDT, this);
        PayServiceFactory.registerUrl(BaseConstant.REQUEST_WALLET_USDT, utils.getRequestUrl(BaseConstant.REQUEST_ANT_ALIPAY));
    }
}
