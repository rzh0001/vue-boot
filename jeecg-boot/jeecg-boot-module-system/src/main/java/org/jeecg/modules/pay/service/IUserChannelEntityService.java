package org.jeecg.modules.pay.service;

import org.jeecg.modules.pay.entity.ChannelEntity;
import org.jeecg.modules.pay.entity.UserChannelEntity;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.system.entity.SysUser;

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
    List<String> queryUserChannel(List<String> channelCodes,String userName);
    void updateUseTime(String channelCode,String userName);
    List<String> getChannelCodeByUserName(String userName);
    void deleteChannel(String userName,List<String> codes,String productCode);
    void save(ChannelEntity channels, SysUser sysUser);
    void deleteProductChannle(String product,List<String> channels);
}
