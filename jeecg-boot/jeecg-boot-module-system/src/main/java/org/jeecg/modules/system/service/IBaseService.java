package org.jeecg.modules.system.service;

import org.apache.shiro.SecurityUtils;
import org.jeecg.common.system.vo.LoginUser;
import org.springframework.stereotype.Service;

/**
 * @author ruanzh
 * @since 2019-09-01
 */
@Service
public class IBaseService {
    
    public LoginUser getLoginUser() {
        return (LoginUser) SecurityUtils.getSubject().getPrincipal();
    }
    
}
