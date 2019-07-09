package org.jeecg.interceptor;

import org.jeecg.common.system.util.JwtUtil;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;

/**
 * @author ruanzh
 * @since 2019-06-22
 */
@Component
public class LogInterceptor implements HandlerInterceptor {
    
    private final static String USERNAME = "username";
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String accessToken = request.getHeader("X-Access-Token");
        if (Objects.isNull(accessToken)){
            MDC.put(USERNAME, "anyone");
        } else {
            MDC.put(USERNAME, JwtUtil.getUserNameByToken(request));
        }
        return true;
    }
    
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
    }
    
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        MDC.remove(USERNAME);
    }
}
