package org.jeecg.modules.pay.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.pay.entity.OrderInfoEntity;
import org.jeecg.modules.pay.entity.TransactionVolumeVO;

/**
 * @Description: d
 * @Author: jeecg-boot
 * @Date: 2020-04-30
 * @Version: V1.0
 */
public interface ITransactionVolumeService extends IService<TransactionVolumeVO> {
	IPage<TransactionVolumeVO> selectPage(Page<OrderInfoEntity> page, Wrapper wrapper);

}
