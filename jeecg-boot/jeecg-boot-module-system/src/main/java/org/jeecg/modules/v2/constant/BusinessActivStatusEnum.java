package org.jeecg.modules.v2.constant;

/**
 * @Author: wangjianbin
 * @Date: 2020/5/28 11:41
 */
public enum  BusinessActivStatusEnum {
    ACTIVE("1","激活"),NOT_ACTIVE("0","未激活");
    private String value;
    private String name;
    BusinessActivStatusEnum (String value,String name){
        this.value=value;
        this.name=name;
    }

    public String getValue() {
        return value;
    }

    public String getName() {
        return name;
    }
}
