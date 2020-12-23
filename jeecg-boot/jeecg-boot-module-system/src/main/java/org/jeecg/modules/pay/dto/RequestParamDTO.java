package org.jeecg.modules.pay.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RequestParamDTO implements Serializable {
    private String mch_id;
    private String order_sn;
    private String money;
    private String goods_desc;
    private String client_ip;
    private String format;
    private String notify_url;
    private String time;
    private String sign;
}
