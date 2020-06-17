package org.jeecg.modules.df.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.df.entity.UserBankcardConfig;
import org.jeecg.modules.df.entity.UserBankcardConfigVo;
import org.jeecg.modules.df.mapper.UserBankcardConfigMapper;
import org.jeecg.modules.df.service.IUserBankcardConfigService;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @Description: 1
 * @Author: jeecg-boot
 * @Date: 2020-06-17
 * @Version: V1.0
 */
@Slf4j
@Service
public class UserBankcardConfigServiceImpl extends ServiceImpl<UserBankcardConfigMapper, UserBankcardConfig> implements IUserBankcardConfigService {

	@Override
	public IPage<UserBankcardConfigVo> selectPageVo(Page<UserBankcardConfigVo> page, Map map) {
		return baseMapper.selectPageVo(page, map);
	}
}
