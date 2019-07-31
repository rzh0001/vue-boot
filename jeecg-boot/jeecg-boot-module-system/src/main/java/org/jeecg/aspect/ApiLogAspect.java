package org.jeecg.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.jeecg.modules.system.util.IPUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;

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

    @Pointcut("execution(public * org.jeecg.modules.pay.controller..ApiController.*(..))")
    public void apiLog(){

    }
    @Before("apiLog()")
    public void doBefore(JoinPoint joinPoint) throws Exception{
        // 接收到请求，记录请求内容
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();

        // 记录下请求内容
        log.info("====================接收api请求，请求参数如下：========================");
        log.info("URL : " + request.getRequestURL().toString());
        log.info("HTTP_METHOD : " + request.getMethod());
        log.info("IP : " + IPUtils.getIpAddr(request));
        log.info("CLASS_METHOD : " + joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName());
        log.info("ARGS : " + Arrays.toString(joinPoint.getArgs()));
    }
    @AfterReturning(returning = "ret", pointcut = "apiLog()")
    public void doAfterReturning(Object ret) throws Throwable {
        // 处理完请求，返回内容
        log.info("====================api请求返回，返回结果如下：========================");
        log.info("result : " + ret);
        log.info("====================api请求结束：========================");
    }
}
