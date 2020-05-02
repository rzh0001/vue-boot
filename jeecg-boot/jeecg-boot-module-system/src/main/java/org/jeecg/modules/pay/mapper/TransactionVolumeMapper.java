package org.jeecg.modules.pay.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.pay.entity.TransactionVolumeVO;

/**
 * @Description: d
 * @Author: jeecg-boot
 * @Date: 2020-04-30
 * @Version: V1.0
 */
public interface TransactionVolumeMapper extends BaseMapper<TransactionVolumeVO> {

	IPage<TransactionVolumeVO> selectPageVo(Page<?> page, @Param(Constants.WRAPPER) Wrapper wrapper);
}
