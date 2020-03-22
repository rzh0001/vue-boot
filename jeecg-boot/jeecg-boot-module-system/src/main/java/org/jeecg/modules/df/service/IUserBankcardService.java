package org.jeecg.modules.df.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.jeecg.modules.df.entity.UserBankcard;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.df.entity.UserBankcardConfigDO;
import org.jeecg.modules.df.entity.UserBankcardVo;


/**
 * @Description: 代付平台用户银行卡
 * @Author: jeecg-boot
 * @Date: 2019-10-25
 * @Version: V1.0
 */
public interface IUserBankcardService extends IService<UserBankcard> {
	IPage<UserBankcardVo> selectUserBankcardPage(Page<UserBankcard> page, Wrapper wrapper);

	/**
	 * 获取商户本次匹配打款银行卡
	 *
	 * @param agentId
	 * @return
	 */
	UserBankcardConfigDO getRandomBankcard(String agentId, String userId);

	/**
	 * 逻辑删除银行卡
	 *
	 * @param id
	 */
	void delete(String id);

	void add(UserBankcard userBankcard);
}
