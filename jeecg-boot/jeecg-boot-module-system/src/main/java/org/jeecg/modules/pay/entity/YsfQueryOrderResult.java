package org.jeecg.modules.pay.entity;

import java.io.Serializable;
import java.util.List;

/**
 * @title:
 * @Description:
 * @author: wangjb
 * @create: 2019-08-19 12:49
 */
@lombok.Data
public class YsfQueryOrderResult  implements Serializable {
    private Integer code;
    private String msg;
    private List<YsfData> data;
}
