package org.jeecg.common.wallet;

import io.swagger.models.auth.In;
import lombok.Getter;

public enum  WalletCallbackType {
    CHARGE(1,"充币回调"),
    PAYOUT(2,"提币回调");
    @Getter
    private Integer code;
    @Getter
    private String name;

    WalletCallbackType(Integer code,String name){
        this.code = code;
        this.name = name;
    }

    public static WalletCallbackType codeBy(Integer code){
        for(WalletCallbackType type:WalletCallbackType.values()){
            if(type.getCode().equals(code)){
                return type;
            }
        }
        return null;
    }
}
