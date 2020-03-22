package org.jeecg.modules.df.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.modules.df.entity.UserBankcard;
import org.jeecg.modules.df.entity.UserBankcardConfig;
import org.jeecg.modules.df.entity.UserBankcardConfigDO;
import org.jeecg.modules.df.entity.UserBankcardVo;
import org.jeecg.modules.df.mapper.UserBankcardMapper;
import org.jeecg.modules.df.service.IUserBankcardConfigService;
import org.jeecg.modules.df.service.IUserBankcardService;
import org.jeecg.modules.exception.RRException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;


/**
 * @Description: 代付平台代理银行卡
 * @Author: jeecg-boot
 * @Date: 2019-10-25
 * @Version: V1.0
 */
@Slf4j
@Service
public class UserBankcardServiceImpl extends ServiceImpl<UserBankcardMapper, UserBankcard> implements IUserBankcardService {


	@Autowired
	private IUserBankcardConfigService configService;

	@Override
	public IPage<UserBankcardVo> selectUserBankcardPage(Page<UserBankcard> page, Wrapper wrapper) {
		return baseMapper.selectPageVo(page, wrapper);
	}

	@Override
	public UserBankcardConfigDO getRandomBankcard(String agentId, String userId) {
		// 获取配置银行卡，若无配置，获取代理所有可用银行卡
		Map map = new HashMap();
		map.put("agent_id", agentId);
		map.put("user_id", userId);
		UserBankcardConfigDO randomBankcard = baseMapper.getRandomBankcard(map);
		if (randomBankcard == null) {
			map.remove("user_id");
			randomBankcard = baseMapper.getRandomBankcard(map);
			if (randomBankcard == null) {
				throw new RRException("获取银行卡失败，代理未配置收款银行卡，请联系代理配置！");
			}
		}

		UserBankcardConfig config = new UserBankcardConfig();
		config.setId(randomBankcard.getId());
		config.setLastTime(new Date());
		configService.updateById(config);
		return randomBankcard;
	}

	@Override
	public void delete(String id) {
		removeById(id);
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("card_id", id);
		configService.removeByMap(map);
		;
	}

	@Override
	public void add(UserBankcard userBankcard) {
		LoginUser opUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
		userBankcard.setUserId(opUser.getId());
		userBankcard.setUsername(opUser.getUsername());
		userBankcard.setDelFlag("0");
		save(userBankcard);

		// 插入 df_user_bankcard_config
		UserBankcardConfig config = new UserBankcardConfig();
		config.setCardId(userBankcard.getId());
		config.setAgentId(userBankcard.getUserId());
		configService.save(config);
	}
}
