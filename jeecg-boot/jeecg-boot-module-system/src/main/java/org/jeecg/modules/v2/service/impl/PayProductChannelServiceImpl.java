package org.jeecg.modules.v2.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.v2.constant.DeleteFlagEnum;
import org.jeecg.modules.v2.entity.PayProductChannel;
import org.jeecg.modules.v2.mapper.PayProductChannelMapper;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.util.List;

/**
 * @Description: 产品通道
 * @Author: jeecg-boot
 * @Date: 2020-05-28
 * @Version: V1.0
 */
@Service
public class PayProductChannelServiceImpl extends ServiceImpl<PayProductChannelMapper, PayProductChannel>
    implements IService<PayProductChannel> {

    public PayProductChannel getproductChannel(String productCode, String channelCode) {
        QueryWrapper<PayProductChannel> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("product_code", productCode).eq("channel_code", channelCode).eq("del_flag", DeleteFlagEnum.NOT_DELETE.getValue());
        return getBaseMapper().selectOne(queryWrapper);
    }

    public boolean exist(String productCode, String channelCode){
        PayProductChannel productChannel = getproductChannel(productCode,channelCode);
        if(productChannel != null){
            return true;
        }
        return false;
    }

    public void delete(String productCode,String channelCode){
        getBaseMapper().delete(productCode,channelCode);
    }

    public void cleanRelated(String productCode){
        getBaseMapper().cleanRelated(productCode);
    }

    public void saveProductChannelCodes(String productCode, List<String> channelCodes){
        getBaseMapper().saveProductChannelCodes(productCode,channelCodes);
    }

    public List<String> getProductRelateChannels(String productCode){
        return getBaseMapper().getProductRelateChannels(productCode);
    }
}
