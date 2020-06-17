package org.jeecg.modules.df.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.df.entity.UserBankcardConfig;
import org.jeecg.modules.df.entity.UserBankcardConfigVo;

import java.util.Map;

/**
 * @Description: 1
 * @Author: jeecg-boot
 * @Date: 2020-06-17
 * @Version: V1.0
 */
public interface IUserBankcardConfigService extends IService<UserBankcardConfig> {
	IPage<UserBankcardConfigVo> selectPageVo(Page<UserBankcardConfigVo> page, Map map);

}
