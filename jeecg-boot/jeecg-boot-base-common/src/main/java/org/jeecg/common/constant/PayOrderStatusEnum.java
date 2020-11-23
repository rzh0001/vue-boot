package org.jeecg.common.constant;

import lombok.Getter;

/**
 * @author wangjianbin
 * @Description
 * @since 2020/11/23 11:43
 */
public enum PayOrderStatusEnum {

    DOING("0","待处理"),
    ACCEPT("1","已接单"),
    DONE("2","已打款"),
    REFUSE("3","拒绝");


    @Getter
    private String value;
    @Getter
    private String name;


    PayOrderStatusEnum(String value,String name){
        this.value = value;
        this.name = name;
    }

    public static PayOrderStatusEnum findByValue(String value){
        for (PayOrderStatusEnum type : PayOrderStatusEnum.values()) {
            if (type.getValue().equals(value)) {
                return type;
            }
        }
        return null;
    }

}
