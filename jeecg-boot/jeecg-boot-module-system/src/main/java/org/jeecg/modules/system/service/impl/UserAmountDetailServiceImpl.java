package org.jeecg.modules.system.service.impl;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.system.entity.SysUser;
import org.jeecg.modules.system.entity.UserAmountDetail;
import org.jeecg.modules.system.mapper.UserAmountDetailMapper;
import org.jeecg.modules.system.service.ISysUserService;
import org.jeecg.modules.system.service.IUserAmountDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * @Description: 用户收入流水详情
 * @Author: jeecg-boot
 * @Date: 2019-08-26
 * @Version: V1.0
 */
@Slf4j
@Service
public class UserAmountDetailServiceImpl extends ServiceImpl<UserAmountDetailMapper, UserAmountDetail> implements IUserAmountDetailService {
	@Autowired
	private ISysUserService userService;

	@Override
	public boolean addAmountDetail(BigDecimal amount, BigDecimal originalAmount, String type, String userId) {
		return addAmountDetail(amount, originalAmount, "", type, userId);
	}

	@Override
	public boolean addAmountDetail(BigDecimal amount, BigDecimal originalAmount, String remark, String type, String userId) {
		return addAmountDetail(amount, originalAmount, "", remark, type, userId);
	}

	@Override
	public boolean addAmountDetail(BigDecimal amount, BigDecimal originalAmount, String orderId, String remark, String type, String userId) {
		log.info("收入流水记录[userId:{},orderId:{},amount:{},remark:{}]", userId, orderId, amount, remark);
		SysUser opUser = userService.getById(userId);
		UserAmountDetail detail = new UserAmountDetail();
		detail.setUserId(opUser.getId());
		detail.setUserName(opUser.getUsername());
		detail.setType(type);
		detail.setAmount(amount);
		detail.setInitialAmount(originalAmount);
		detail.setUpdateAmount(amount.add(originalAmount));
		detail.setOrderId(orderId);
		detail.setRemark(remark);
		detail.setAgentId(opUser.getAgentId());
		detail.setAgentUsername(opUser.getAgentUsername());
		detail.setAgentRealname(opUser.getAgentRealname());
		detail.setSalesmanId(opUser.getSalesmanId());
		detail.setSalesmanUsername(opUser.getSalesmanUsername());
		detail.setSalesmanRealname(opUser.getSalesmanRealname());
		return save(detail);
	}

	@Override
	public BigDecimal getTotalIncome(String userId) {
		return baseMapper.getTotalIncome(userId);
	}

	@Override
	public BigDecimal getTodayIncome(String userId) {
		return baseMapper.getTodayIncome(userId);
	}

	@Override
	public UserAmountDetail getUserOriginalAmount(String userId, String dateStr) {
		return baseMapper.getUserOriginalAmount(userId, DateUtil.parseDate(dateStr));
	}
}
