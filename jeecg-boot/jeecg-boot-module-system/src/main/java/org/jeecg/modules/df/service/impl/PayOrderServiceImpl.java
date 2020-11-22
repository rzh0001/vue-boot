package org.jeecg.modules.df.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.modules.df.constant.DfConstant;
import org.jeecg.modules.df.entity.DeviceUserEntity;
import org.jeecg.modules.df.entity.PayOrder;
import org.jeecg.modules.api.entity.PayOrderResult;
import org.jeecg.modules.df.mapper.PayOrderMapper;
import org.jeecg.modules.api.service.IDfApiService;
import org.jeecg.modules.df.service.IPayOrderService;
import org.jeecg.modules.df.util.IDUtil;
import org.jeecg.modules.api.exception.ApiException;
import org.jeecg.modules.exception.RRException;
import org.jeecg.modules.system.entity.SysUser;
import org.jeecg.modules.system.service.ISysUserService;
import org.jeecg.modules.system.service.IUserAmountEntityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class PayOrderServiceImpl extends ServiceImpl<PayOrderMapper, PayOrder>
		implements IPayOrderService {

	@Autowired
	private ISysUserService userService;

	@Autowired
	private IUserAmountEntityService userAmountService;

	@Autowired
	@Lazy
	private IDfApiService apiService;

	@Override
	@Transactional(rollbackFor = Exception.class)
	public boolean create(PayOrder order) {
		LoginUser ou = (LoginUser) SecurityUtils.getSubject().getPrincipal();

		QueryWrapper<PayOrder> qw = new QueryWrapper<>();
		qw.lambda().eq(PayOrder::getUserId, ou.getId()).eq(PayOrder::getOrderId, order.getOrderId());
		Integer count = baseMapper.selectCount(qw);
		if (count > 0) {
			throw new RRException("订单号重复，订单可能已创建成功，请确认后再试！");
		}

		// 获取费率配置
		SysUser user = userService.getById(ou.getId());
		if (BeanUtil.isEmpty(user)
				|| null == user.getTransactionFeeRate()
				|| null == user.getOrderFixedFee()) {
			throw new RRException("获取费率配置失败！");
		}

		order.setUserId(user.getId());
		order.setUserName(user.getUsername());
		order.setUserRealname(user.getRealname());
		order.setAgentId(user.getAgentId());
		order.setAgentUsername(user.getAgentUsername());
		order.setAgentRealname(user.getAgentRealname());
		order.setSalesmanId(user.getSalesmanId());
		order.setSalesmanUsername(user.getSalesmanUsername());
		order.setSalesmanRealname(user.getSalesmanRealname());

		// 订单号由前端生成 2020-09-21 22:15:30
		// 生成订单号
//		order.setOrderId(IDUtil.genPayOrderId());
		order.setStatus(DfConstant.STATUS_SAVE);

		// 计算手续费
		order.setTransactionFee(order.getAmount().multiply(user.getTransactionFeeRate()).setScale(2, BigDecimal.ROUND_HALF_UP));
		order.setFixedFee(user.getOrderFixedFee());
		order.setOrderFee(order.getTransactionFee().add(order.getFixedFee()));

		// 检查代付额度
		BigDecimal userAmount = userAmountService.getUserAmount(ou.getId());
		if (userAmount.compareTo(order.getAmount().add(order.getOrderFee())) < 0) {
			throw new RRException("余额不足，请及时充值！本订单需扣减[" + order.getAmount().add(order.getOrderFee()) + "]元");
		}
		// 扣减商户余额
		String remark = "单笔固定手续费：" + order.getFixedFee() + "，交易手续费：" + order.getTransactionFee();
		userAmountService.changeAmount(user.getId(), order.getAmount().negate(), order.getOrderId(), "", "5");
		userAmountService.changeAmount(user.getId(), order.getOrderFee().negate(), order.getOrderId(), remark, "6");

		return save(order);
	}


	@Autowired
	private DeviceUserEntityServiceImpl deviceUserEntityService;

	@Override
	public PayOrder findOrderByDevice(String deviceCode,String balance){
		List<DeviceUserEntity>  deviceUsers = deviceUserEntityService.findByCode(deviceCode);
		if(CollectionUtils.isEmpty(deviceUsers)){
			return null;
		}
		//分配订单
		List<String> userNames = deviceUsers.stream().map(key->key.getUserName()).collect(Collectors.toList());
		return this.assignOrderByCreateTime(userNames,balance);
	}



	@Override
	@Transactional(rollbackFor = Exception.class)
	public boolean checked(PayOrder order) {

		// 修改订单状态
		updateById(order);

		/**
		 * 手续费分润
		 */
		// 判断是否有介绍人
		if (StrUtil.isNotBlank(order.getSalesmanId())) {
			SysUser salesman = userService.getUserById(order.getSalesmanId());
			if (BeanUtil.isEmpty(salesman)
					|| null == salesman.getTransactionFeeRate()
					|| null == salesman.getOrderFixedFee()) {
				log.info("\n=======>订单[{}]：获取介绍人费率配置失败", order.getOuterOrderId());
				throw new ApiException(1006, "获取介绍人费率配置失败！");
			}
			// 计算代理收入
			BigDecimal bigDecimal = order.getAmount().multiply(salesman.getTransactionFeeRate()).setScale(2, BigDecimal.ROUND_HALF_UP);
			BigDecimal orderFee = bigDecimal.add(salesman.getOrderFixedFee());

			// 增加代理收入
			String remark = "单笔固定手续费：" + salesman.getOrderFixedFee() + "，交易手续费：" + bigDecimal;
			userAmountService.changeAmount(order.getAgentId(), orderFee, order.getOrderId(), remark, "1");

			// 增加介绍人收入
			remark = "单笔固定手续费：" + order.getFixedFee().subtract(salesman.getOrderFixedFee()) + "，交易手续费：" + order.getTransactionFee().subtract(bigDecimal);
			userAmountService.changeAmount(order.getSalesmanId(), order.getOrderFee().subtract(orderFee), order.getOrderId(), remark, "1");

		} else {
			// 增加代理收入
			String remark = "单笔固定手续费：" + order.getFixedFee() + "，交易手续费：" + order.getTransactionFee();
			userAmountService.changeAmount(order.getAgentId(), order.getOrderFee(), order.getOrderId(), remark, "1");
		}

		if (StrUtil.isNotBlank(order.getCallbackUrl())) {
			try {
				apiService.callback(order.getId());
			} catch (Exception e) {
				log.info("\n=======>订单[{}]：回调失败", order.getOuterOrderId());
			}
		}
		return true;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public boolean rejected(PayOrder order) {

		// 修改订单状态
		updateById(order);

		// 返还商户额度
		userAmountService.changeAmount(order.getUserId(), order.getAmount(), order.getOrderId(), order.getRemark(), "3");
		userAmountService.changeAmount(order.getUserId(), order.getOrderFee(), order.getOrderId(), order.getRemark(), "3");
		if (StrUtil.isNotBlank(order.getCallbackUrl())) {
			//若关闭回调通知，这里不回调
			SysUser user = userService.getUserById(order.getUserId());
			if (user.getCallbackSwitch() == 0) {
				apiService.callback(order.getId());
			}
		}
		return true;
	}

	@Override
	public PayOrderResult apiOrder(PayOrder order) {

		int count = baseMapper.count(order.getUserId(), order.getOuterOrderId());
		if (count > 0) {
			log.info("\n=======>订单[{}]：订单号重复", order.getOuterOrderId());
			throw new ApiException(1005, "订单号重复");
		}

		// 获取费率配置
		SysUser user = userService.getById(order.getUserId());
		if (BeanUtil.isEmpty(user)
				|| null == user.getTransactionFeeRate()
				|| null == user.getOrderFixedFee()) {
			log.info("\n=======>订单[{}]：获取费率配置失败", order.getOuterOrderId());
			throw new ApiException(1006, "获取费率配置失败！");
		}

		// 生成订单号
		order.setOrderId(IDUtil.genPayOrderId());
		order.setStatus(DfConstant.STATUS_SAVE);
		order.setCallbackStatus("0");

		// 计算手续费
		order.setTransactionFee(order.getAmount().multiply(user.getTransactionFeeRate()).setScale(2, BigDecimal.ROUND_HALF_UP));
		order.setFixedFee(user.getOrderFixedFee());
		order.setOrderFee(order.getTransactionFee().add(order.getFixedFee()));

		// 检查代付额度
		BigDecimal userAmount = userAmountService.getUserAmount(order.getUserId());
		if (userAmount.compareTo(order.getAmount().add(order.getOrderFee())) < 0) {
			log.info("\n=======>订单[{}]：余额不足，请及时充值！", order.getOuterOrderId());
			throw new ApiException(1004, "余额不足，请及时充值！当前余额[" + userAmount + "]元，本订单需[" + order.getAmount().add(order.getOrderFee()) + "]元");
		}
		// 扣减商户余额
		userAmountService.changeAmount(user.getId(), order.getAmount().negate(), order.getOrderId(), null, "5");
		String remark = "单笔固定手续费：" + order.getFixedFee() + "，交易手续费：" + order.getTransactionFee();
		userAmountService.changeAmount(user.getId(), order.getOrderFee().negate(), order.getOrderId(), remark, "6");
		log.info("\n=======>订单[{}]：扣除手续费成功", order.getOuterOrderId());

		save(order);
		PayOrderResult result = PayOrderResult.fromPayOrder(order);
		return result;
	}

	@Override
	public int count(String userId, String outerOrderId) {

		return baseMapper.count(userId, outerOrderId);
	}

	@Override
	public PayOrder getByOuterOrderId(String outerOrderId) {
		return baseMapper.getByOuterOrderId(outerOrderId);
	}

	@Override
	public Map<String, Object> summary(QueryWrapper<PayOrder> queryWrapper) {
		return baseMapper.summary(queryWrapper);
	}

	@Override
	public PayOrder assignOrderByCreateTime(List<String> userNames,String balance) {
		String limit = "limit 0,1";
		List<PayOrder> list = getBaseMapper().selectList(new LambdaQueryWrapper<PayOrder>()
		.in(PayOrder::getUserName,userNames).le(PayOrder::getAmount,new BigDecimal(balance))
				.eq(PayOrder::getStatus,"1")
				.orderByDesc(PayOrder::getCreateTime).last(limit));
		if(CollectionUtils.isEmpty(list)){
			return null;
		}
		return list.get(0);
	}
}
