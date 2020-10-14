package org.jeecg.common.wallet;

import lombok.Getter;

/**
 * @Author: wangjianbin
 * @Date: 2020/10/14 14:12
 */
public enum OrderStatusEnum {
    INVALID(-1,"无效"),
    NO_PAY(0,"未支付"),
    NO_BACK(1,"成功未返回"),
    SUCCESS(2,"成功已返回");
    @Getter
    private Integer code;
    @Getter
    private String name;
    OrderStatusEnum(Integer code,String name){
        this.code=code;
        this.name = name;
    }
    public static OrderStatusEnum codeBy(Integer code){
        for(OrderStatusEnum status:OrderStatusEnum.values()){
            if(status.getCode().equals(code)){
                return status;
            }
        }
        return null;
    }
}
