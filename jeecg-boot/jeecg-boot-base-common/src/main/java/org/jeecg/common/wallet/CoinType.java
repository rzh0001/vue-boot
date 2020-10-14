package org.jeecg.common.wallet;

public enum  CoinType {
    USDT("60","USDT")
   ;

    private String code;
    private String unit;
    CoinType(String code, String unit){
        this.code=code;
        this.unit=unit;
    }

    public String getCode(){
        return this.code;
    }

    public String getUnit(){
        return this.unit;
    }

    public static CoinType codeBy(String code){
        for(CoinType coinType:CoinType.values()){
            if(coinType.getCode().equals(code)){
                return coinType;
            }
        }
        return null;
    }


}
