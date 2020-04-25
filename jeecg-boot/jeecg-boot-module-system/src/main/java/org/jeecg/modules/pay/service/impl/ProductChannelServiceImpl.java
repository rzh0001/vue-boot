package org.jeecg.modules.pay.service.impl;

import org.jeecg.modules.pay.entity.ChannelEntity;
import org.jeecg.modules.pay.entity.ProductChannel;
import org.jeecg.modules.pay.mapper.ProductChannelMapper;
import org.jeecg.modules.pay.service.IProductChannelService;
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
    public Integer batchSave(List<ChannelEntity> channelCodes, String productCode) {
        return productChannelMapper.batchSave(channelCodes,productCode);
    }

    @Override
    public List<ProductChannel> getChannelProduct(String productCode) {
        return productChannelMapper.getChannelProduct(productCode);
    }

    @Override
    public List<String> getProductCodeByChannelCodes(List<String> channelCodes) {
        return productChannelMapper.getProductCodeByChannelCodes(channelCodes);
    }

    @Override
    public List<String> getChannelCodeNotInProductCode(String productCode) {
        return productChannelMapper.getChannelCodeNotInProductCode(productCode);
    }

    @Override
    public List<String> alreadyRelationChannelCodes(String productCode) {
        return productChannelMapper.alreadyRelationChannelCodes(productCode);
    }
}
