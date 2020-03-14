package org.jeecg.modules.df.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.df.entity.UserBankcard;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.jeecg.modules.df.entity.UserBankcardVo;

/**
 * @Description: 代付平台用户银行卡
 * @Author: jeecg-boot
 * @Date: 2019-10-25
 * @Version: V1.0
 */
public interface UserBankcardMapper extends BaseMapper<UserBankcard> {

	IPage<UserBankcardVo> selectPageVo(Page<?> page, @Param(Constants.WRAPPER) Wrapper wrapper);
}
