package org.jeecg.modules.system.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.constant.PayConstant;
import org.jeecg.modules.exception.RRException;
import org.jeecg.modules.system.entity.SysUser;
import org.jeecg.modules.system.entity.UserAmountEntity;
import org.jeecg.modules.system.mapper.UserAmountEntityMapper;
import org.jeecg.modules.system.service.IUserAmountDetailService;
import org.jeecg.modules.system.service.IUserAmountEntityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

/**
 * @Description: 商户收入额度
 * @Author: jeecg-boot
 * @Date: 2019-07-31
 * @Version: V1.0
 */
@Slf4j
@Service
public class UserAmountEntityServiceImpl extends ServiceImpl<UserAmountEntityMapper, UserAmountEntity> implements IUserAmountEntityService {

	@Autowired
	@Lazy
	private IUserAmountDetailService amountDetailService;


	@Override
	public UserAmountEntity getUserAmountByUserName(String userName) {
		return baseMapper.getUserAmountByUserName(userName);
	}

	@Override
	public void initialUserAmount(SysUser user) {

		UserAmountEntity amount = new UserAmountEntity();
		amount.setAmount(BigDecimal.ZERO);
		amount.setUserId(user.getId());
		amount.setUserName(user.getUsername());
		amount.setAgentId(user.getMemberType().equals(PayConstant.MEMBER_TYPE_AGENT) ? null : user.getAgentId());
		this.save(amount);
	}

	@Override
	public boolean changeAmount(String userId, BigDecimal amount) {
		log.info("更新用户余额[userId:{},amount:{}]", userId, amount);
		boolean ok = baseMapper.changeAmount(userId, amount);
		if (!ok) {
			throw new RRException("更新余额失败");
		}
		return true;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public boolean changeAmount(String userId, BigDecimal amount, String orderId, String remark, String type) {
		UserAmountEntity userAmount = baseMapper.getByUserId(userId);
		amountDetailService.addAmountDetail(amount, userAmount.getAmount(), orderId, remark, type, userId);
		changeAmount(userId, amount);
		return true;
	}

	@Override
	public BigDecimal getUserAmount(String userId) {
		UserAmountEntity amount = getUserAmountEntity(userId);
		return amount.getAmount();
	}

	@Override
	public UserAmountEntity getUserAmountEntity(String userId) {
		UserAmountEntity amount = baseMapper.getByUserId(userId);
		if (amount == null) {
			throw new RRException("获取余额失败");
		}
		return amount;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public boolean adjustAmount(String userId, BigDecimal adjustAmount, String remark, SysUser user) {

		UserAmountEntity userAmountEntity = getUserAmountEntity(userId);
		if (BeanUtil.isEmpty(userAmountEntity)) {
			throw new RRException("内部错误!请联系管理员！");
		}

		if (adjustAmount.compareTo(userAmountEntity.getAmount()) > 0) {
			throw new RRException("余额不足！");
		}

		changeAmount(userId, adjustAmount, "", remark, "4");

		return true;
	}

	@Override
	public void changeAmountByUserName(String userName, BigDecimal amount) {
		baseMapper.changeAmountByUserName(userName, amount);
	}
}
