package org.jeecg.modules.system.service.impl;

import org.jeecg.common.api.vo.Result;
import org.jeecg.modules.pay.service.IUserAmountDetailService;
import org.jeecg.modules.pay.service.IUserAmountEntityService;
import org.jeecg.modules.system.service.IBaseService;
import org.jeecg.modules.system.service.IHomepageService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

/**
 * @author ruanzh
 * @since 2019-09-01
 */
public class IHomepageServiceImpl extends IBaseService implements IHomepageService {
    
    @Autowired
    private IUserAmountEntityService amountService;
    
    @Autowired
    private IUserAmountDetailService amountDetailService;
    
    @Override
    public Result<Map<String, Object>> homepageSummary() {
        
        // 获取 今日订单数、成功率、成功金额
        
        // 获取会员余额、总收入、今日收入
        
        // 今日提现金额
        return null;
    }
}
