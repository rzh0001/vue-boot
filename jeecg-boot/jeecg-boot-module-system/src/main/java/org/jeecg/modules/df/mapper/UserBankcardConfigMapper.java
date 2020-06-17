package org.jeecg.modules.df.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.df.entity.UserBankcardConfig;
import org.jeecg.modules.df.entity.UserBankcardConfigVo;

import java.util.Map;

/**
 * @Description: 1
 * @Author: jeecg-boot
 * @Date: 2020-06-17
 * @Version: V1.0
 */
public interface UserBankcardConfigMapper extends BaseMapper<UserBankcardConfig> {

	IPage<UserBankcardConfigVo> selectPageVo(Page<UserBankcardConfigVo> page, @Param("map") Map map);
}
