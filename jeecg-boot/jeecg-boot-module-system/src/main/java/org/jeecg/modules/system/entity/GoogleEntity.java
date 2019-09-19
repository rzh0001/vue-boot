package org.jeecg.modules.system.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class GoogleEntity implements Serializable {
    private String googleUrl;
    private String googleKey;
}
