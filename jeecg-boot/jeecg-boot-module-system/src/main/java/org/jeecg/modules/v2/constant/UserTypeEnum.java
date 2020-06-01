package org.jeecg.modules.v2.constant;

/**
 * @Author: wangjianbin
 * @Date: 2020/6/1 10:09
 */
public enum UserTypeEnum {
    AGENT("1","代理"),INTRODUCER("2","介绍人"),MERCHANT("3","商户");
    private String value;
    private String name;

    UserTypeEnum(String value,String name){
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
