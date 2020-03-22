package org.jeecg.modules.df.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.modules.df.constant.DfConstant;
import org.jeecg.modules.df.entity.PayOrder;
import org.jeecg.modules.df.entity.RechargeOrder;
import org.jeecg.modules.df.entity.UserBankcard;
import org.jeecg.modules.df.entity.UserBankcardConfigDO;
import org.jeecg.modules.df.mapper.RechargeOrderMapper;
import org.jeecg.modules.df.service.IRechargeOrderService;
import org.jeecg.modules.df.service.IUserBankcardService;
import org.jeecg.modules.df.util.IDUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.util.Map;

/**
 * @Description: 代付充值订单
 * @Author: jeecg-boot
 * @Date: 2019-10-26
 * @Version: V1.0
 */
@Slf4j
@Service
public class RechargeOrderServiceImpl extends ServiceImpl<RechargeOrderMapper, RechargeOrder> implements IRechargeOrderService {

	@Autowired
	private IUserBankcardService cardService;

	@Override
	public Map<String, Object> summary(QueryWrapper<RechargeOrder> queryWrapper) {
		return baseMapper.summary(queryWrapper);
	}

	@Override
	public void add(RechargeOrder order) {
		LoginUser ou = (LoginUser) SecurityUtils.getSubject().getPrincipal();
		order.setUserId(ou.getId());
		order.setUserName(ou.getUsername());
		order.setUserRealname(ou.getRealname());
		order.setAgentId(ou.getAgentId());
		order.setAgentUsername(ou.getAgentUsername());
		order.setAgentRealname(ou.getAgentRealname());
		order.setOrderId(IDUtil.genRechargeOrderId());
		order.setStatus(DfConstant.STATUS_PAID);

		UserBankcardConfigDO randomBankcard = cardService.getRandomBankcard(ou.getAgentId(), ou.getId());
		order.setBankcardId(randomBankcard.getId());
		order.setBankName(randomBankcard.getBankName());
		order.setBranchName(randomBankcard.getBranchName());
		order.setAccountType(randomBankcard.getAccountType());
		order.setAccountName(randomBankcard.getAccountName());
		order.setCardNumber(randomBankcard.getCardNumber());

		save(order);
	}
}
