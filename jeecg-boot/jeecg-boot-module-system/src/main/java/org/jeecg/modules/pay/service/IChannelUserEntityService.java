package org.jeecg.modules.pay.service;

import org.jeecg.modules.pay.entity.ChannelUserEntity;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.data.repository.query.Param;

/**
 * @Description: 用户关联通道信息
 * @Author: jeecg-boot
 * @Date:   2019-07-25
 * @Version: V1.0
 */
public interface IChannelUserEntityService extends IService<ChannelUserEntity> {
    ChannelUserEntity queryChannelAndUser(String channelCode,  String userId);
}
