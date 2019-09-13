package org.jeecg.modules.pay.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.jeecg.modules.pay.entity.UserRateEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * @Description: 用户在指定通道下的费率
 * @Author: jeecg-boot
 * @Date:   2019-07-26
 * @Version: V1.0
 */
public interface UserRateEntityMapper extends BaseMapper<UserRateEntity> {
    /**
     * 根据用户名和高级代理名称查询
     * @param userName
     * @param agentUsername
     * @return
     */
    String getUserRateByUserNameAndAngetCode(@Param("userName")String userName,@Param("agentUsername")String agentUsername,@Param("payType")String payType);


    String getBeIntroducerRate(@Param("userName")String userName, @Param("agentUsername")String agentUsername, @Param("introducerName")String beIntroducerName,@Param("payType") String payType);

    @Select("select * from sys_user_rate where user_name=#{userName}")
    List<UserRateEntity> queryUserRate(@Param("userName") String username);

    @Delete("delete from sys_user_rate where user_name=#{userName} and channel_code=#{channelCode} and user_rate=#{userRate}")
    void deleteUserRate(UserRateEntity dto);

    @Select("select username from sys_user where salesman_username=#{userName}")
    List<String> getBeIntroducerName(@Param("userName")String userName);
}
