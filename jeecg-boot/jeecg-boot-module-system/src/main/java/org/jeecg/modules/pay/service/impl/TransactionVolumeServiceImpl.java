package org.jeecg.modules.pay.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.jeecg.modules.pay.entity.OrderInfoEntity;
import org.jeecg.modules.pay.entity.TransactionVolumeVO;
import org.jeecg.modules.pay.mapper.TransactionVolumeMapper;
import org.jeecg.modules.pay.service.ITransactionVolumeService;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

/**
 * @Description: d
 * @Author: jeecg-boot
 * @Date: 2020-04-30
 * @Version: V1.0
 */
@Service
public class TransactionVolumeServiceImpl extends ServiceImpl<TransactionVolumeMapper, TransactionVolumeVO> implements ITransactionVolumeService {

	@Override
	public IPage<TransactionVolumeVO> selectPage(Page<OrderInfoEntity> page, Wrapper wrapper) {
		return baseMapper.selectPageVo(page, wrapper);
	}
}
