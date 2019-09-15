package org.jeecg.modules.pay.service.impl;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.constant.PayConstant;
import org.jeecg.modules.exception.RRException;
import org.jeecg.modules.pay.entity.UserAmountDetail;
import org.jeecg.modules.pay.entity.UserAmountReport;
import org.jeecg.modules.pay.service.*;
import org.jeecg.modules.system.entity.SysUser;
import org.jeecg.modules.system.service.ISysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @author ruanzh
 * @since 2019-09-14
 */
@Slf4j
@Service
public class IReportServiceImpl implements IReportService {
    
    @Autowired
    private ISysUserService userService;
    
    @Autowired
    private IUserAmountEntityService amountService;
    
    @Autowired
    private IUserAmountDetailService amountDetailService;
    
    @Autowired
    private IOrderInfoEntityService orderService;
    
    @Autowired
    private ICashOutApplyService cashOutApplyService;
    
    @Autowired
    private IUserAmountReportService reportService;
    
    @Override
    public boolean generateFinancialStatement() {
        DateTime yesterday = DateUtil.yesterday();
        
        QueryWrapper<SysUser> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(SysUser::getMemberType, PayConstant.MEMBER_TYPE_MEMBER);
        List<SysUser> users = userService.list(wrapper);
        
        if (users.isEmpty()) {
            throw new RRException("获取用户列表失败");
        }
        
        for (SysUser user : users) {
            
            QueryWrapper<UserAmountReport> rw = new QueryWrapper<>();
            rw.lambda().eq(UserAmountReport::getReportDate, DateUtil.formatDate(yesterday)).eq(UserAmountReport::getUserId, user.getId());
            UserAmountReport report = reportService.getOne(rw);
            
            Map<String, Object> orderAmount = orderService.summaryUserTodayOrderAmount(user.getId(), yesterday);
            
            report.setPaidAmount((BigDecimal) orderAmount.get("paidAmount"));
            report.setPayFee((BigDecimal) orderAmount.get("payFee"));
            
            
            Map<String, Object> cashOutAmount = cashOutApplyService.summaryUserTodayCashOutAmount(user.getId(), yesterday);
            report.setCashOutAmount((BigDecimal) cashOutAmount.get("cashOutAmount"));
            
            
            BigDecimal userAmount = amountService.getUserAmount(user.getId());
            report.setAvailableAmount(userAmount);
            
            reportService.saveOrUpdate(report);
        }
        
        
        return true;
    }
    
    @Override
    public boolean generateUserOriginalAmount(String dateStr) {
        QueryWrapper<SysUser> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(SysUser::getMemberType, PayConstant.MEMBER_TYPE_MEMBER);
        List<SysUser> users = userService.list(wrapper);
        
        for (SysUser user : users) {
            
            UserAmountDetail amountDetail = amountDetailService.getUserOriginalAmount(user.getId(), dateStr);
            BigDecimal originalAmount;
            if (amountDetail == null) {
                originalAmount = amountService.getUserAmount(user.getId());
            } else {
                originalAmount = amountDetail.getInitialAmount();
            }
            
            
            UserAmountReport report = generateReport(user, originalAmount);
            report.setReportDate(dateStr);
            reportService.save(report);
            
        }
        
        return true;
    }
    
    private UserAmountReport generateReport(SysUser user, BigDecimal originalAmount) {
        UserAmountReport report = new UserAmountReport();
        report.setUserId(user.getId());
        report.setUserName(user.getUsername());
        report.setOriginalamount(originalAmount);
        report.setAgentId(user.getAgentId());
        return report;
    }
    
}
