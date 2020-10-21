package org.jeecg.modules.wallet.dto;

import io.swagger.models.auth.In;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class WalletHttpCallbackBody implements Serializable {
    /**
     *钱包地址
     */
    private String address;
    /**
     *交易数量，根据币种精度获取实际金额，实际金额=amount/pow(10,decimals)，即实际金额等于amount除以10的decimals次方
     */
    private String amount;
    /**
     *矿工费，根据币种精度获取实际金额，实际金额获取同上
     */
    private String fee;
    /**
     *币种精度
     */
    private String decimals;
    /**
     *子币种编号
     */
    private String coinType;
    /**
     *主币种编号
     */
    private String mainCoinType;
    /**
     *业务编号，提币回调时为提币请求时传入的，充币回调无值
     */
    private String businessId;
    /**
     *区块高度
     */
    private String blockHigh;
    /**
     *状态
     */
    private Integer status;
    /**
     *交易流水号
     */
    private String tradeId;
    /**
     *交易类型
     */
    private Integer tradeType;
    /**
     *区块链交易哈希
     */
    private String txid;
    /**
     *备注
     */
    private String memo;


    /**
     * 实际金额
     * @return
     */
    public BigDecimal getActualAmount(){
        double pow = Math.pow(10,Integer.parseInt(this.decimals));
        return new BigDecimal(this.amount).divide(new BigDecimal(Double.toString(pow)),2,BigDecimal.ROUND_HALF_UP);
    }

    public BigDecimal getActualFee(){
        double pow = Math.pow(10,Integer.parseInt(this.decimals));
        return new BigDecimal(this.fee).divide(new BigDecimal(Double.toString(pow)),2,BigDecimal.ROUND_HALF_UP);
    }

}
