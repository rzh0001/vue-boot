package org.jeecg.modules.wallet.service;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.jeecg.common.system.vo.DictModel;
import org.jeecg.common.wallet.WalletCallbackType;
import org.jeecg.modules.system.service.ISysDictService;
import org.jeecg.modules.util.BaseConstant;
import org.jeecg.modules.util.WalletHttpRequestUtils;
import org.jeecg.modules.wallet.dto.WalletHttpCallbackBody;
import org.jeecg.modules.wallet.dto.WalletHttpCallbackParam;
import org.jeecg.modules.wallet.entity.PayWalletOrderInfo;
import org.jeecg.modules.wallet.entity.PayWalletUrl;
import org.jeecg.modules.wallet.service.impl.PayWalletOrderInfoServiceImpl;
import org.jeecg.modules.wallet.service.impl.PayWalletUrlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@Slf4j
public class WalletService {
    @Autowired
    private PayWalletUrlService walletUrlService;
    @Autowired
    private PayWalletOrderInfoServiceImpl orderInfoService;
    @Autowired
    public ISysDictService dictService;
    /**
     *获取可用的钱包地址
     * @param coinType 币种类型
     * @return
     */
    public String findFreeWalletUrl(String coinType){
        return walletUrlService.findFreeWalletUrl(coinType);
    }

    /**
     * 创建地址
     * @param coinType 币种类型
     * @param callBackUrl 四方回调地址
     */
    public String createWalletUrl(String requestUrl,String key,String merId,String coinType,String callBackUrl){
        try {
            String url =  WalletHttpRequestUtils.getWalletUrl(requestUrl,key,merId,coinType,callBackUrl);
            walletUrlService.save(coinType,url);
            return url;
        }catch (Exception e){
           log.info("获取钱包地址失败，失败原因：{}",e);
        }
        return null;
    }

    /**
     * 将人民币转换为币种对应的数量
     * @param coinType 币种类型
     * @param amount 人民币金额，单位为分
     * @return
     */
    public String transformCoinByAmount(String coinType,String  amount){
        String rate = findCoinRate(coinType);
        return new BigDecimal(amount).divide(new BigDecimal(rate)).toString();
    }

    /**
     * 获取币种对应的利率
     * @param coinType
     * @return
     */
    public String findCoinRate(String coinType){
        String rate = null;
        List<DictModel> apiKey = dictService.queryDictItemsByCode(BaseConstant.WALLET_COIN_RATE);
        for (DictModel k : apiKey) {
            if (BaseConstant.WALLET_COIN_USDT.equals(k.getText())) {
                rate = k.getValue();
            }
        }
        return rate;
    }

    /**
     * 释放地址
     * @param address
     */
    public void freeWalletUrl(String address){
        walletUrlService.freeWallterUrl(address);
    }

    @Transactional(rollbackFor = Exception.class)
    public void callback(WalletHttpCallbackParam param){
        log.info("接收钱包回调请求，请求入参:{}", JSONObject.toJSONString(param));
        if(!checkSignSuccess(param)){
            return;
        }
        String body = param.getBody();
        WalletHttpCallbackBody callbackBody = JSONObject.parseObject(body,WalletHttpCallbackBody.class);
        WalletCallbackType callbackType = WalletCallbackType.codeBy(callbackBody.getTradeType());
        if(WalletCallbackType.PAYOUT.equals(callbackType)){
            log.info("接收钱包回调请求，请求类型为提币请求，忽略该请求...");
            return;
        }
        if(!walletUrlService.checkCallbackWalletUrlIsOk(callbackBody.getAddress())){
            log.info("接收钱包回调请求，数据库地址校验不通过，地址：[{}]",callbackBody.getAddress());
            return;
        }
        //todo 更新四方订单信息；释放链接；统计；异步通知下游
        PayWalletOrderInfo orderInfo = orderInfoService.findCurrentCallbackWalletOrder(callbackBody.getAddress());
        if(orderInfo == null){
            log.info("接收钱包回调请求，四方未发现匹配的订单信息，钱包地址为：[{}]，忽略请求...",callbackBody.getAddress());
            return;
        }
        //矿工费
        orderInfo.setCoinFee(callbackBody.getActualFee());
        //实际支付的币种数量
        orderInfo.setCoinQuantity(callbackBody.getActualAmount());
        orderInfoService.updateById(orderInfo);
        freeWalletUrl(callbackBody.getAddress());
    }

    private boolean checkSignSuccess(WalletHttpCallbackParam param){
        //sign=md5(body + key + nonce + timestamp)
        StringBuilder signStr =new StringBuilder();
        signStr.append(param.getBody()).append(WalletHttpRequestUtils.KEY).append(param.getNonce()).append(param.getTimestamp());
        String sign = DigestUtils.md5Hex(signStr.toString());
        if(sign.equals(param.getSign())){
            return true;
        }
        log.info("钱包回调，签名异常，本地签名串为：[{}],本地签名值为：[{}],入参签名值为:[{}]",signStr.toString(),sign,param.getSign());
        return false;
    }
}
