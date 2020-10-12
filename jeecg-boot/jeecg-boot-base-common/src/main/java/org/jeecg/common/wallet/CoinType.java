package org.jeecg.common.wallet;

public enum  CoinType {
    Bitcoin("0","BTC"),
    Litecoin("2","LTC"),
    Dogecoin("3","DOGE"),
    Ethereum("60","ETH"),
    EthereumClassic("61","ETC"),
    Ripple("144","XRP"),
    Bitcoincash("145","BCH"),
    EOS("194","EOS"),
    TRX("195","TRX"),
    NEO("888","NEO"),
    XNE("208","XNE"),
    TEC("206","TEC"),
    GCA("500","GCA"),
    GCB("501","GCB"),
    GalaxyChain("502","GCC"),
    DASH("5","DASH"),
    ZEC("133","ZEC"),
    QTUM("2301","QTUM"),
    TECO("506","TECO"),
    CNYT("509","CNYT"),
    STO("99","STO"),
    CNT("520","CNT");

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
