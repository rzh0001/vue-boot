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
    private String userid;
    private String orderNo;
    private String MsgUrl;
    private String return_url;
    private String mch_id;
    private String sign;

    private String channel;
    private String Amount;

}
