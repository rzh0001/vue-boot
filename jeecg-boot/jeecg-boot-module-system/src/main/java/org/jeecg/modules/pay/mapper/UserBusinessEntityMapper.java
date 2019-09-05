package org.jeecg.modules.pay.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.jeecg.modules.pay.entity.UserBusinessEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * @Description: 用户关联商户
 * @Author: jeecg-boot
 * @Date:   2019-07-26
 * @Version: V1.0
 */
public interface UserBusinessEntityMapper extends BaseMapper<UserBusinessEntity> {

    List<UserBusinessEntity> queryBusinessCodeByUserName(@Param("userName")String userName,@Param("channelCode")String channelCode);

    @Select("select * from sys_user_business where user_name=#{username}")
    List<UserBusinessEntity> queryUserBusiness(@Param("username")String username);

    @Delete("delete from sys_user_business where user_name=#{userName} and business_code=#{businessCode} and channel_code=#{channelCode}")
    void deleteUserBusiness(UserBusinessEntity userBusinessEntity);
}
