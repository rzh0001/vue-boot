package org.jeecg.modules.df.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.util.RedisUtil;
import org.jeecg.common.util.SUtil;
import org.jeecg.modules.df.constant.DfConstant;
import org.jeecg.modules.df.entity.PayOrder;
import org.jeecg.modules.system.entity.SysUser;
import org.jeecg.modules.system.service.ISysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;

@Slf4j
@Service
public class OperatorService {
	@Autowired
	private RedisUtil redisUtil;

	@Autowired
	private ISysUserService userService;

	@Autowired
	private IPayOrderService orderService;

	@PostConstruct
	private void init() {
		initialCache();
	}

	/**
	 * 服务启动时，首次实例化
	 */
	public void initialCache() {
		//获取代理列表
		QueryWrapper<SysUser> qw = new QueryWrapper<>();
		qw.lambda().eq(SysUser::getMemberType, "1").eq(SysUser::getDelFlag, 0).eq(SysUser::getStatus, 1);
		List<SysUser> list = userService.list(qw);
		if (list.isEmpty()) {
			log.error("初始化缓存失败，获取代理信息失败");
			return;
		}
		list.forEach(agent -> {

			initialAgentOperator(agent.getId());
			initialAgentOrder(agent.getId());
		});

	}

	/**
	 * 缓存代理的上线操作员
	 *
	 * @param agentId
	 */
	private void initialAgentOperator(String agentId) {
		QueryWrapper<SysUser> qw = new QueryWrapper<>();
		qw.lambda().eq(SysUser::getMemberType, "4").eq(SysUser::getDelFlag, 0).eq(SysUser::getStatus, 1).eq(SysUser::getAgentId, agentId);
		List<SysUser> list = userService.list(qw);
		if (list.isEmpty()) {
			log.error("初始化操作员缓存失败，获取代理{}的操作员信息失败", agentId);
			return;
		}
		log.info("初始化缓存-代理{}的上线操作员", agentId);
		String key = genOperatorKey(agentId);
		if (redisUtil.hasKey(key)) {
			redisUtil.del(key);
		}
		list.forEach(user -> redisUtil.lSet(key, user.getId()));

	}

	private void initialAgentOrder(String agentId) {
		QueryWrapper<PayOrder> qw = new QueryWrapper<>();
		qw.lambda().eq(PayOrder::getAgentId, agentId).eq(PayOrder::getStatus, DfConstant.STATUS_SAVE);
		List<PayOrder> list = orderService.list(qw);

		log.info("初始化缓存-代理{}的代付订单", agentId);
		String key = genPayOrderKey(agentId);
		if (redisUtil.hasKey(key)) {
			redisUtil.del(key);
		}
		list.forEach(order -> redisUtil.lSet(key, order.getOrderId()));

	}

	/**
	 * 生成缓存key
	 *
	 * @param agentId
	 * @return
	 */
	private String genOperatorKey(String agentId) {
		return SUtil.concat("operator-agent-{}", agentId);
	}

	private String genPayOrderKey(String agentId) {
		return SUtil.concat("pay-order-agent-{}", agentId);
	}

	public void distribute() {
		//获取代理列表
		QueryWrapper<SysUser> qw = new QueryWrapper<>();
		qw.lambda().eq(SysUser::getMemberType, "1").eq(SysUser::getDelFlag, 0).eq(SysUser::getStatus, 1);
		List<SysUser> list = userService.list(qw);

		list.forEach(agent -> {
			String key1 = genOperatorKey(agent.getId());
			long l = redisUtil.lGetListSize(key1);
			if (l == 0) {
				log.info("代理{}没有在线的操作员，不进行派单", agent.getId());
				return;
			}

			String key2 = genPayOrderKey(agent.getId());
			l = redisUtil.lGetListSize(key2);
			if (l == 0) {
				log.info("代理{}没有未派发的订单", agent.getId());
				return;
			}

			PayOrder o = (PayOrder) redisUtil.lGetIndex(key2, 0);

		});
	}
}
