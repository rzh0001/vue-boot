package org.jeecg.modules.system.controller;

import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author ruanzh
 * @since 2019-08-29
 */
@Slf4j
@RestController
@RequestMapping("/sys/homepage")
public class HomepageController {
    
    @GetMapping("/homepageSummary")
    public Result homepageSummary() {
        
        Result result = new Result();
        
        // 获取 今日订单数、成功率、成功金额
        
        // 获取会员余额、总收入、今日收入
        
        // 今日提现金额
        return result;
        
    }
}
