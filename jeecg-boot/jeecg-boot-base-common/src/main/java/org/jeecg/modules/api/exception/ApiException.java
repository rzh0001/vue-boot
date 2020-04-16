package org.jeecg.modules.api.exception;

/**
 * API异常基类
 * @author ruanzh
 * @since 2019/11/17
 */
public class ApiException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    
    private String msg;
    private int code = 500;
    
    public ApiException(String msg) {
        super(msg);
        this.msg = msg;
    }
    
    public ApiException(String msg, Throwable e) {
        super(msg, e);
        this.msg = msg;
    }
    
    public ApiException(int code, String msg) {
        super(msg);
        this.msg = msg;
        this.code = code;
    }
    
    public ApiException(String msg, int code, Throwable e) {
        super(msg, e);
        this.msg = msg;
        this.code = code;
    }
    
    public String getMsg() {
        return msg;
    }
    
    public void setMsg(String msg) {
        this.msg = msg;
    }
    
    public int getCode() {
        return code;
    }
    
    public void setCode(int code) {
        this.code = code;
    }
    
    
}
