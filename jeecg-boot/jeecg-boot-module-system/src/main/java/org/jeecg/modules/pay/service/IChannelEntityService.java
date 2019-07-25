package org.jeecg.modules.pay.service;

import org.jeecg.modules.pay.entity.ChannelEntity;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @Description: 通道信息
 * @Author: jeecg-boot
 * @Date:   2019-07-25
 * @Version: V1.0
 */
public interface IChannelEntityService extends IService<ChannelEntity> {
    ChannelEntity queryChannelByCode(String channelCode);
}
