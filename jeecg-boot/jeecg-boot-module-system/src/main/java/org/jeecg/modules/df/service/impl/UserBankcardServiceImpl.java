package org.jeecg.modules.df.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.jeecg.modules.df.entity.UserBankcard;
import org.jeecg.modules.df.entity.UserBankcardVo;
import org.jeecg.modules.df.mapper.UserBankcardMapper;
import org.jeecg.modules.df.service.IUserBankcardService;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;


/**
 * @Description: 代付平台用户银行卡
 * @Author: jeecg-boot
 * @Date: 2019-10-25
 * @Version: V1.0
 */
@Service
public class UserBankcardServiceImpl extends ServiceImpl<UserBankcardMapper, UserBankcard> implements IUserBankcardService {

	@Override
	public IPage<UserBankcardVo> selectUserBankcardPage(Page<UserBankcard> page, Wrapper wrapper) {
		return baseMapper.selectPageVo(page, wrapper);
	}
}
