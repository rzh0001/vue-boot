package org.jeecg.modules.pay.entity;

/**
 * @Author: wangjianbin
 * @Date: 2020/3/15 20:29
 */

public class NiuNanAlipayParam {

    /**
     * merCode : 20180719120117265
     * orderNo : CZ201812111503426df09b
     * orderAmount : 1
     * callbackUrl : http://xiaoyuapidemo.niunan.net/callback.aspx
     * payType : 2
     * productDesc : 用户充值
     * sign : 0693ab847406928288b581f922587124
     */

    private String merCode;
    private String orderNo;
    private String orderAmount;
    private String callbackUrl;
    private String payType;
    private String productDesc;
    private String sign;

    public String getMerCode() {
        return merCode;
    }

    public void setMerCode(String merCode) {
        this.merCode = merCode;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getOrderAmount() {
        return orderAmount;
    }

    public void setOrderAmount(String orderAmount) {
        this.orderAmount = orderAmount;
    }

    public String getCallbackUrl() {
        return callbackUrl;
    }

    public void setCallbackUrl(String callbackUrl) {
        this.callbackUrl = callbackUrl;
    }

    public String getPayType() {
        return payType;
    }

    public void setPayType(String payType) {
        this.payType = payType;
    }

    public String getProductDesc() {
        return productDesc;
    }

    public void setProductDesc(String productDesc) {
        this.productDesc = productDesc;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }
}
