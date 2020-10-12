package org.jeecg.common.wallet;

public enum WalletStatus {
    FREE(0,"可用"),
    BUSY(1,"不可用");

    private Integer code;
    private String value;

    WalletStatus(Integer code, String value){
        this.code=code;
        this.value=value;
    }

    public Integer getCode() {
        return code;
    }

    public String getValue() {
        return value;
    }

    public static WalletStatus codeBy(Integer code){
        for(WalletStatus status:WalletStatus.values()){
            if(status.getCode().equals(code)){
                return status;
            }
        }
        return null;
    }
}
