package org.jeecg.modules.v2.constant;

/**
 * @Author: wangjianbin
 * @Date: 2020/5/28 11:34
 */
public enum DeleteFlagEnum {
    DELETE(1,"删除"),NOT_DELETE(0,"未删除");
    private Integer value;
    private String name;

    DeleteFlagEnum(Integer value,String name){
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
