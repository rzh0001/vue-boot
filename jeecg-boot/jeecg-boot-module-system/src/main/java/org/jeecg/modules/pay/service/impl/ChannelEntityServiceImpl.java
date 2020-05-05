package org.jeecg.modules.pay.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.common.constant.PayConstant;
import org.jeecg.common.util.RedisUtil;
import org.jeecg.modules.pay.entity.ChannelEntity;
import org.jeecg.modules.pay.mapper.ChannelEntityMapper;
import org.jeecg.modules.pay.service.IChannelEntityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * @Description: 通道设置
 * @Author: jeecg-boot
 * @Date: 2019-07-26
 * @Version: V1.0
 */
@Service
public class ChannelEntityServiceImpl extends ServiceImpl<ChannelEntityMapper, ChannelEntity> implements IChannelEntityService {

	@Autowired
	private RedisUtil redisUtill;

	@PostConstruct
	public void init() {
		cacheChannelGatewayConfig();
	}

	@Override
	public void cacheChannelGatewayConfig() {
		redisUtill.del(PayConstant.REDIS_CHANNEL_CONFIG);
		List<ChannelEntity> channelEntities = queryAllChannelCode();
		channelEntities.forEach((channel) -> redisUtill.hset(PayConstant.REDIS_CHANNEL_CONFIG, channel.getChannelCode(), channel.getChannelGateway()));
	}

	@Override
	public ChannelEntity queryChannelByCode(String channelCode) {
		return baseMapper.queryChannelByCode(channelCode);
	}

	@Override
	public List<ChannelEntity> queryAllChannelCode() {
		return baseMapper.selectList(new QueryWrapper<>());
	}

	@Override
	public List<ChannelEntity> queryAgentChannelCodeByAgentName(String userName) {
		return baseMapper.queryAgentChannelCodeByAgentName(userName);
	}

	@Override
	public List<ChannelEntity> queryChannelByCodes(List<String> codes) {
		return baseMapper.queryChannelByCodes(codes);
	}
}
