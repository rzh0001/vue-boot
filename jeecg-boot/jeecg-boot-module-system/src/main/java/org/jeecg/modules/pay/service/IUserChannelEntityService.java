package org.jeecg.modules.pay.service;

import org.jeecg.modules.pay.entity.UserChannelEntity;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @Description: 用户关联通道
 * @Author: jeecg-boot
 * @Date:   2019-07-26
 * @Version: V1.0
 */
public interface IUserChannelEntityService extends IService<UserChannelEntity> {
    UserChannelEntity queryChannelAndUserName(String channelCode, String userName);
    List<UserChannelEntity> queryChannelByUserName(String username);
    void deleteUserChannel(String userName,String channelCode);
}
