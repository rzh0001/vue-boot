package org.jeecg.modules.df.entity;

import com.alibaba.fastjson.JSON;
import lombok.Data;
import org.jeecg.modules.system.entity.SysUser;
import org.jeecg.modules.util.AES128Util;

import java.math.BigDecimal;

/**
 * @author ruanzh
 * @since 2019/11/16
 */
@Data
public class PayOrderData {
    private String productCode;
    private String bizOrderNo;
    private BigDecimal amount;
    
    private String accountType;
    private String accountName;
    private String accountNo;
    private String bankCode;
    private String ip;
    private String callbackUrl;
    private String remark;
    
    public String toJsonString() {
        return JSON.toJSONString(this);
    }
    
    public String encodeData(String apiKey) {
        return AES128Util.encryptBase64(toJsonString(), apiKey);
    }
    
    public PayOrder toPayOrder(SysUser u) {
        PayOrder o = new PayOrder();
        o.setChannel(productCode);
        o.setOuterOrderId(bizOrderNo);
        o.setAmount(amount);
        o.setAccountType(accountType);
        o.setAccountName(accountName);
        o.setCardNumber(accountNo);
        o.setBankCode(bankCode);
        o.setIp(ip);
        o.setCallbackUrl(callbackUrl);
        o.setRemark(remark);
        
        o.setUserId(u.getId());
        o.setUserName(u.getUsername());
        o.setUserRealname(u.getRealname());
        o.setAgentId(u.getAgentId());
        o.setAgentUsername(u.getAgentUsername());
        o.setAgentRealname(u.getAgentRealname());
        o.setSalesmanId(u.getSalesmanId());
        o.setSalesmanUsername(u.getSalesmanUsername());
        o.setSalesmanRealname(u.getSalesmanRealname());
        return o;
    }
}
