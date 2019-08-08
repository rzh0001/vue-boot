package org.jeecg.modules.pay.service;

import org.jeecg.modules.pay.entity.ChannelEntity;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @Description: 通道设置
 * @Author: jeecg-boot
 * @Date:   2019-07-26
 * @Version: V1.0
 */
public interface IChannelEntityService extends IService<ChannelEntity> {
    ChannelEntity queryChannelByCode(String channelCode);
    List<String> queryAllChannelCode();
}
