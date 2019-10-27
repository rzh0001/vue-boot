package org.jeecg.modules.pay.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.constant.PayConstant;
import org.jeecg.modules.exception.RRException;
import org.jeecg.modules.pay.entity.UserAmountReport;
import org.jeecg.modules.pay.service.ICashOutApplyService;
import org.jeecg.modules.pay.service.IOrderInfoEntityService;
import org.jeecg.modules.pay.service.IReportService;
import org.jeecg.modules.pay.service.IUserAmountReportService;
import org.jeecg.modules.system.entity.SysUser;
import org.jeecg.modules.system.entity.UserAmountDetail;
import org.jeecg.modules.system.service.ISysUserService;
import org.jeecg.modules.system.service.IUserAmountDetailService;
import org.jeecg.modules.system.service.IUserAmountEntityService;
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
    public boolean generateFinancialStatement(String dateStr) {
    
        log.info(">>>>>>> 获取用户列表......");
        QueryWrapper<SysUser> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(SysUser::getMemberType, PayConstant.MEMBER_TYPE_MEMBER);
        List<SysUser> users = userService.list(wrapper);
        
        if (users.isEmpty()) {
            log.error(">>>>>>> 获取用户列表失败");
            throw new RRException("获取用户列表失败");
        }
        log.info(">>>>>>> 获取用户列表成功");
        
        for (SysUser user : users) {
            log.info(">>>>>>> 用户：{} 开始处理......", user.getUsername());
            QueryWrapper<UserAmountReport> rw = new QueryWrapper<>();
            rw.lambda()
                    .eq(UserAmountReport::getReportDate, dateStr)
                    .eq(UserAmountReport::getUserId, user.getId());
            UserAmountReport report = reportService.getOne(rw);
    
            if (BeanUtil.isEmpty(report)) {
                log.info(">>>>>>> 用户：{} 未生成元数据，开始生成......", user.getUsername());
                initUserAmountReport(user, dateStr);
                report = reportService.getOne(rw);
            }
    
            log.info(">>>>>>> 用户：{} 开始统计信息......", user.getUsername());
            Map<String, Object> orderAmount = orderService.summaryUserTodayOrderAmount(user.getId(), DateUtil.parseDate(dateStr));
            
            report.setPaidAmount((BigDecimal) orderAmount.get("paidAmount"));
            report.setPayFee((BigDecimal) orderAmount.get("payFee"));
    
    
            Map<String, Object> cashOutAmount = cashOutApplyService.summaryUserTodayCashOutAmount(user.getId(), DateUtil.parseDate(dateStr));
            report.setCashOutAmount((BigDecimal) cashOutAmount.get("cashOutAmount"));
            
            
            BigDecimal userAmount = amountService.getUserAmount(user.getId());
            report.setAvailableAmount(userAmount);
            
            reportService.saveOrUpdate(report);
            log.info(">>>>>>> 用户：{} 处理完毕......", user.getUsername());
        }
        
        
        return true;
    }
    
    @Override
    public boolean generateUserOriginalAmount(String dateStr) {
        QueryWrapper<SysUser> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(SysUser::getMemberType, PayConstant.MEMBER_TYPE_MEMBER);
        List<SysUser> users = userService.list(wrapper);
        
        for (SysUser user : users) {
            initUserAmountReport(user, dateStr);
    
        }
    
        return true;
    }
    
    private void initUserAmountReport(SysUser user, String dateStr) {
        UserAmountDetail amountDetail = amountDetailService.getUserOriginalAmount(user.getId(), dateStr);
        BigDecimal originalAmount;
        if (amountDetail != null && amountDetail.getInitialAmount() != null) {
            originalAmount = amountDetail.getInitialAmount();
        } else {
            originalAmount = amountService.getUserAmount(user.getId());
        }
        
        
        UserAmountReport report = generateReport(user, originalAmount);
        report.setReportDate(dateStr);
        reportService.save(report);
    }
    
    private UserAmountReport generateReport(SysUser user, BigDecimal originalAmount) {
        UserAmountReport report = new UserAmountReport();
        report.setUserId(user.getId());
        report.setUserName(user.getUsername());
        report.setOriginalamount(originalAmount);
        report.setAgentId(user.getAgentId());
        report.setAgentUsername(user.getAgentUsername());
        report.setAgentRealname(user.getAgentRealname());
        report.setSalesmanId(user.getSalesmanId());
        report.setSalesmanUsername(user.getSalesmanUsername());
        report.setSalesmanRealname(user.getSalesmanRealname());
        return report;
    }
    
}
