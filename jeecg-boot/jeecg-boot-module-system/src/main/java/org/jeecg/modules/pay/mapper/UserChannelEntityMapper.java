package org.jeecg.modules.pay.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.jeecg.modules.pay.entity.ChannelEntity;
import org.jeecg.modules.pay.entity.UserChannelEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.jeecg.modules.system.entity.SysUser;

/**
 * @Description: 用户关联通道
 * @Author: jeecg-boot
 * @Date:   2019-07-26
 * @Version: V1.0
 */
public interface UserChannelEntityMapper extends BaseMapper<UserChannelEntity> {
    UserChannelEntity queryChannelAndUser(@Param("channelCode") String channelCode,@Param("userName") String userName);

    @Select("select c.user_name as userName,c.channel_code as channelCode, b.business_code as businessCode,b.api_key as apiKey,c.upper_limit as upperLimit,c.lower_limit as lowerLimit from sys_user_channel c left join sys_user_business b on c.channel_code = b.channel_code AND c.user_name=b.user_name where c.user_name=#{username}")
    List<UserChannelEntity> queryChannelByUserName(@Param("username")String username);

    @Delete("delete from sys_user_channel where user_name=#{userName} and channel_code=#{channelCode}")
    void deleteUserChannel(@Param("userName")String userName, @Param("channelCode")String channelCode);

    List<String> queryUserChannel(@Param("channelCodes") List<String> channelCodes, @Param("userName") String userName);

    @Update("update sys_user_channel set update_time=now() where user_name=#{userName} and channel_code=#{channelCode}")
    void updateUseTime(@Param("channelCode") String channelCode, @Param("userName") String userName);

    @Select("select channel_code from sys_user_channel where user_name=#{userName}")
    List<String> getChannelCodeByUserName(@Param("userName")String userName);

    void deleteChannel(@Param("userName") String userName,@Param("codes") List<String> codes);

    void batchSave(@Param("channels") List<ChannelEntity> channels, @Param("sysUser") SysUser sysUser);
}
