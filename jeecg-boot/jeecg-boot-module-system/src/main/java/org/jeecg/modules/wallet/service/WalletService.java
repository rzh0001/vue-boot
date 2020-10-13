package org.jeecg.modules.wallet.service;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.jeecg.common.wallet.WalletCallbackType;
import org.jeecg.modules.util.WalletHttpRequestUtils;
import org.jeecg.modules.wallet.dto.WalletHttpCallbackBody;
import org.jeecg.modules.wallet.dto.WalletHttpCallbackParam;
import org.jeecg.modules.wallet.entity.PayWalletUrl;
import org.jeecg.modules.wallet.service.impl.PayWalletUrlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class WalletService {
    @Autowired
    private PayWalletUrlService walletUrlService;
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
    public String createWalletUrl(String coinType,String callBackUrl){
        try {
            String url =  WalletHttpRequestUtils.getWalletUrl(coinType,callBackUrl);
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
    public Double transformCoinByAmount(String coinType,Long amount){
        return null;
    }

    /**
     * 获取币种对应的利率
     * @param coinType
     * @return
     */
    public Double findCoinRate(String coinType){
        return null;
    }

    /**
     * 释放地址
     * @param address
     */
    public void freeWalletUrl(String address){
        walletUrlService.freeWallterUrl(address);
    }

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
