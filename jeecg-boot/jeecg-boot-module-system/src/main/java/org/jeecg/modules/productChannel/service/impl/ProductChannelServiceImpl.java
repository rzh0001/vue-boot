package org.jeecg.modules.productChannel.service.impl;

import org.jeecg.modules.productChannel.entity.ProductChannel;
import org.jeecg.modules.productChannel.mapper.ProductChannelMapper;
import org.jeecg.modules.productChannel.service.IProductChannelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.util.List;

/**
 * @Description: 产品关联通道
 * @Author: jeecg-boot
 * @Date:   2020-03-05
 * @Version: V1.0
 */
@Service
public class ProductChannelServiceImpl extends ServiceImpl<ProductChannelMapper, ProductChannel> implements IProductChannelService {
   @Autowired
    private ProductChannelMapper productChannelMapper;
    @Override
    public List<String> getChannelByProductCode(String productCode) {
        return productChannelMapper.getProductChannelByProductCode(productCode);
    }

    @Override
    public void remove(String productCode) {
        productChannelMapper.remove(productCode);
    }

    @Override
    public Integer batchSave(List<String> channelCodes, String productCode) {
        return productChannelMapper.batchSave(channelCodes,productCode);
    }
}
