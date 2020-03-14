package org.jeecg.modules.pay.entity;

/**
 * @Author: wangjianbin
 * @Date: 2020/3/14 18:01
 */
public class ChiChengAlipayQueryResult {

    /**
     * memberid : 10120
     * orderid : 1234567891211
     * amount : 300.0000
     * time_end : 1970-01-01 08:00:00
     * transaction_id : 20200314135727555651
     * returncode : 00
     * trade_state : NOTPAY
     * sign : 90D2E58CE7BFD1055EF8D8680C201947
     */

    private String memberid;
    private String orderid;
    private String amount;
    private String time_end;
    private String transaction_id;
    private String returncode;
    private String trade_state;
    private String sign;

    public String getMemberid() {
        return memberid;
    }

    public void setMemberid(String memberid) {
        this.memberid = memberid;
    }

    public String getOrderid() {
        return orderid;
    }

    public void setOrderid(String orderid) {
        this.orderid = orderid;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getTime_end() {
        return time_end;
    }

    public void setTime_end(String time_end) {
        this.time_end = time_end;
    }

    public String getTransaction_id() {
        return transaction_id;
    }

    public void setTransaction_id(String transaction_id) {
        this.transaction_id = transaction_id;
    }

    public String getReturncode() {
        return returncode;
    }

    public void setReturncode(String returncode) {
        this.returncode = returncode;
    }

    public String getTrade_state() {
        return trade_state;
    }

    public void setTrade_state(String trade_state) {
        this.trade_state = trade_state;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }
}
