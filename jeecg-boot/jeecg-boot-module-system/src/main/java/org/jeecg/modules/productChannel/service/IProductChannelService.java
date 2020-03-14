package org.jeecg.modules.productChannel.service;

import org.jeecg.modules.productChannel.entity.ProductChannel;
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

    Integer batchSave(List<String> channelCodes,String productCode);
}
