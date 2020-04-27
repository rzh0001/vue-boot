package org.jeecg.modules.api.constant;

import lombok.Data;

/**
 * @Author:
 * @Date: 2020/4/27 9:18
 */
public enum PayTypeEnum {
    ALI_BANK("ali_bank","支付宝转银行卡");
    private String value;
    private String name;
    PayTypeEnum(String value, String name) {
        this.value = value;
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
