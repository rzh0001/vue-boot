package org.jeecg.modules.api.vo;

import java.util.HashMap;
import java.util.Map;

/**
 * @title:
 * @Description: api返回结果
 * @author: wangjb
 * @create: 2019-06-10 11:37
 */
public class ResultApiVo extends HashMap<String, Object> {

    public ResultApiVo() {
        put("code", 0);
        put("msg", "success");
    }
    public static ResultApiVo error() {
        return error(500, "未知异常，请联系管理员");
    }

    public static ResultApiVo error(String msg) {
        return error(500, msg);
    }

    public static ResultApiVo error(int code, String msg) {
        ResultApiVo r = new ResultApiVo();
        r.put("code", code);
        r.put("msg", msg);
        return r;
    }

    public static ResultApiVo ok(String msg) {
        ResultApiVo r = new ResultApiVo();
        r.put("msg", msg);
        return r;
    }

    public static ResultApiVo ok(Map<String, Object> map) {
        ResultApiVo r = new ResultApiVo();
        r.putAll(map);
        return r;
    }

    public static ResultApiVo ok() {
        return new ResultApiVo();
    }

    @Override
    public ResultApiVo put(String key, Object value) {
        super.put(key, value);
        return this;
    }
}
