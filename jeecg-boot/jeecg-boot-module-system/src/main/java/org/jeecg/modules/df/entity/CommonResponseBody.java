package org.jeecg.modules.df.entity;

import lombok.Data;

/**
 * @author ruanzh
 * @since 2019/11/16
 */
@Data
public class CommonResponseBody {
    private int code;
    private String msg;
    private String data;
    
    public CommonResponseBody() {
        code = 0;
        msg = "success";
    }
    
    public static CommonResponseBody ok() {
        return new CommonResponseBody();
    }
    
    public static CommonResponseBody ok(String data) {
        CommonResponseBody body = new CommonResponseBody();
        body.setData(data);
        return body;
    }
    
    public static CommonResponseBody error(int code, String msg) {
        CommonResponseBody body = new CommonResponseBody();
        body.setCode(code);
        body.setMsg(msg);
        return body;
    }
}
