package org.jeecg.aspect;

import com.alibaba.fastjson.JSONObject;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.jeecg.common.system.vo.DictModel;
import org.jeecg.modules.system.entity.SysUser;
import org.jeecg.modules.system.service.ISysDictService;
import org.jeecg.modules.system.service.ISysUserService;
import org.jeecg.modules.system.util.IPUtils;
import org.jeecg.common.util.encryption.AES128Util;
import org.jeecg.modules.util.BaseConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;

/**
 * @title:
 * @Description: 拦截请求api的请求，记录请求入参和出参
 * @author: wangjb
 * @create: 2019-06-11 15:28
 */
@Aspect
@Component
public class ApiLogAspect {
    private static final Logger log = LoggerFactory.getLogger(ApiLogAspect.class);
    @Autowired
    private ISysUserService userService;
    @Autowired
    public ISysDictService dictService;
    private static String key = null;
    @PostConstruct
    public void init() {
        List<DictModel> apiKey = dictService.queryDictItemsByCode(BaseConstant.API_KEY);
        for (DictModel k : apiKey) {
            if (BaseConstant.API_KEY.equals(k.getText())) {
                key = k.getValue();
            }
        }
    }
    @Pointcut("execution(public * org.jeecg.modules.pay.controller..ApiController.*(..))")
    public void apiLog(){

    }
    @Before("apiLog()")
    public void doBefore(JoinPoint joinPoint) throws Exception{
        try{
            // 接收到请求，记录请求内容
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            HttpServletRequest request = attributes.getRequest();

            // 记录下请求内容
            log.info("====================接收api请求，请求参数如下：========================");
            log.info("URL : " + request.getRequestURL().toString());
            log.info("HTTP_METHOD : " + request.getMethod());
            log.info("IP : " + IPUtils.getIpAddr(request));
            log.info("CLASS_METHOD : " + joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName());
            JSONObject object = (JSONObject)joinPoint.getArgs()[0];
            String data = (String) object.get("data");
            String userName = (String)  object.get("username");
            SysUser user = userService.getUserByName(userName);
            String apiKey = null;
            //回调是挂马回调四方，或四方本地自己补单，属于内部加密机制，使用的密钥从数据字典中获取
            if(joinPoint.getSignature().getName().equals("callback")){
                apiKey = key;
                log.info("系统密钥：{}",apiKey);
            }else {
                apiKey = user.getApiKey();
                log.info("用户密钥：{}",apiKey);
            }
            String dataStr = AES128Util.decryptBase64(data, apiKey);
            log.info("ARGS : " + Arrays.toString(joinPoint.getArgs()));
            log.info("解密ARGS : " + dataStr);
        }catch (Exception e){
            log.info("====================接收api请求，异常信息为{}：========================",e);
            log.info("api日志记录异常");
        }

    }
    @AfterReturning(returning = "ret", pointcut = "apiLog()")
    public void doAfterReturning(Object ret) throws Throwable {
        // 处理完请求，返回内容
        log.info("====================api请求返回，返回结果如下：========================");
        log.info("result : " + ret);
        log.info("====================api请求结束：========================");
    }
}
