package org.jeecg.modules.pay.service;

import org.jeecg.modules.pay.entity.ChannelBusinessEntity;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @Description: 通道关联商户
 * @Author: jeecg-boot
 * @Date:   2019-07-26
 * @Version: V1.0
 */
public interface IChannelBusinessEntityService extends IService<ChannelBusinessEntity> {
    ChannelBusinessEntity queryChannelBusiness(String businessCode,String channelCode);
}
