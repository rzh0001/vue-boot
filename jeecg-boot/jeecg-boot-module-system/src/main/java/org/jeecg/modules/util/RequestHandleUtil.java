package org.jeecg.modules.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @ Description：请求内容处理工具类
 */
@Slf4j
public class RequestHandleUtil {
    public static final String METHOD_POST = "POST";
    public static final String METHOD_GET = "GET";
    public static final String CONTENT_TYPE_JSON = "application/json";
    /**
     * 获取请求参数
     * @param req
     * @return
     */
    public static Object getReqParam(HttpServletRequest req){
        String method = req.getMethod();
        log.info("=====>回调的请求的方式为:{}",method);
        if(METHOD_GET.equals(method)){
            return doGet(req);
        }else if(METHOD_POST.equals(method)){
            return doPost(req);
        }else{
            //其他请求方式暂不处理
            return null;
        }
    }

    private static Map<String, Object> doGet(HttpServletRequest req) {
        String param = req.getQueryString();
        log.info("====>回调的入参为：{}",param);
        return (Map<String, Object>)paramsToMap(param,METHOD_GET);
    }

    private static JSONObject doPost(HttpServletRequest req){
        String contentType = req.getContentType();
        log.info("====>post请求的contentType为：{}",contentType);
        StringBuilder sb = new StringBuilder();
        try {
            if (contentType.contains(CONTENT_TYPE_JSON)) {
                BufferedReader br = new BufferedReader(new InputStreamReader(req.getInputStream(), "UTF-8"));
                String line = null;
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }
                log.info("====>回调的入参为：{}",sb.toString());
                return (JSONObject)paramsToMap(sb.toString(),METHOD_POST);
            } else {
                //其他内容格式的请求暂不处理
                log.info("====>post请求的contentType为：{}",contentType);
            }
        } catch (IOException e) {
            log.info("获取post请求中的参数异常，异常信息为：{}",e);
        }
        return null;
    }

    /**
     * post:{"data":"GYu8TUt8u/GUJA7irzFyKiwMb8Hj7a8L59ZFY+SV6A95Dk3TCBGSRn6AvrAGhz2m8JhFPGaM9+OaGWCYQEC3mg==","sign":"b8a76e35ec18df01ee9936b9fe27bdd9","timestamp":1572167429525,"username":"www"}
     * get:orderId=123456&&status=1
     * @param params
     * @return
     */
    public static Object paramsToMap(String params,String methodType) {
        //如果是post请求，转换为json
        if(methodType.equals(METHOD_POST)){
            JSONObject jsonObject = JSON.parseObject(params);
            log.info("====>回调的入参为：{}",jsonObject.toJSONString());
            return  (Map<String,Object>)jsonObject;
        }else{
            //get请求转换为Map<String,Object>
            Map<String, String> map = new LinkedHashMap<>();
            if (StringUtils.isNotBlank(params)) {
                String[] array = params.split("&");
                for (String pair : array) {
                    if ("=".equals(pair.trim())) {
                        continue;
                    }
                    String[] entity = pair.split("=");
                    if (entity.length == 1) {
                        map.put(decode(entity[0]), null);
                    } else {
                        map.put(decode(entity[0]), decode(entity[1]));
                    }
                }
            }
            return map;
        }
    }

    /**
     * 编码格式转换
     * @param content
     * @return
     */
    public static String decode(String content) {
        try {
            return URLDecoder.decode(content, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "";
    }
}
