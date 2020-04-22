package org.jeecg.modules.pay.service;

import org.jeecg.modules.pay.entity.ChannelEntity;
import org.jeecg.modules.pay.entity.ProductChannel;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @Description: 产品关联通道
 * @Author: jeecg-boot
 * @Date:   2020-03-05
 * @Version: V1.0
 */
public interface IProductChannelService extends IService<ProductChannel> {

    List<String> getChannelByProductCode(String productCode);

    void remove(String productCode);

    Integer batchSave(List<ChannelEntity> channelCodes,String productCode);

    List<ProductChannel> getChannelProduct(String productCode);

    List<String> getProductCodeByChannelCodes(List<String> channelCodes);

    List<String> getChannelCodeNotInProductCode(String productCode);

    List<String> alreadyRelationChannelCodes(String productCode);
}
