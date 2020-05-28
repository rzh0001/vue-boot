package org.jeecg.modules.v2.constant;

/**
 * @Author: wangjianbin
 * @Date: 2020/5/28 11:29
 */
public enum StatusEnum {
    CLOSE(0,"关闭"),OPEN(1,"开启");
    private Integer value;
    private String name;

     StatusEnum(Integer value,String name){
        this.value=value;
        this.name=name;
    }

    public Integer getValue() {
        return value;
    }

    public String getName() {
        return name;
    }
}
