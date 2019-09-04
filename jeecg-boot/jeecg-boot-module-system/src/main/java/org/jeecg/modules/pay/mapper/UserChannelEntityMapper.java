package org.jeecg.modules.pay.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.jeecg.modules.pay.entity.UserChannelEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * @Description: 用户关联通道
 * @Author: jeecg-boot
 * @Date:   2019-07-26
 * @Version: V1.0
 */
public interface UserChannelEntityMapper extends BaseMapper<UserChannelEntity> {
    UserChannelEntity queryChannelAndUser(@Param("channelCode") String channelCode,@Param("userName") String userName);

    @Select("select * from sys_user_channel where user_name=#{username}")
    List<UserChannelEntity> queryChannelByUserName(@Param("username")String username);
}
