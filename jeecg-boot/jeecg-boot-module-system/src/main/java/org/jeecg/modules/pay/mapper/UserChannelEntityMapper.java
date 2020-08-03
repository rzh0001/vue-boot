package org.jeecg.modules.pay.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.*;
import org.jeecg.modules.pay.entity.ChannelEntity;
import org.jeecg.modules.pay.entity.UserChannelEntity;
import org.jeecg.modules.system.entity.SysUser;

import java.math.BigDecimal;
import java.util.List;

/**
 * @Description: 用户关联通道
 * @Author: jeecg-boot
 * @Date: 2019-07-26
 * @Version: V1.0
 */
public interface UserChannelEntityMapper extends BaseMapper<UserChannelEntity> {
	List<UserChannelEntity> queryChannelAndUser(@Param("channelCode") String channelCode, @Param("userName") String userName);

	@Select("select c.user_name as userName,c.channel_code as channelCode, b.business_code as businessCode,b.api_key as apiKey,c.upper_limit as upperLimit,c.lower_limit as lowerLimit from sys_user_channel c left join sys_user_business b on c.channel_code = b.channel_code AND c.user_name=b.user_name where c.user_name=#{username}")
	List<UserChannelEntity> queryChannelByUserName(@Param("username") String username);

	@Delete("delete from sys_user_channel where user_name=#{userName} and channel_code=#{channelCode}")
	void deleteUserChannel(@Param("userName") String userName, @Param("channelCode") String channelCode);

	List<String> queryUserChannel(@Param("channelCodes") List<String> channelCodes, @Param("userName") String userName);

	@Update("update pay_v2_user_channel set update_time=now() where user_name=#{userName} and channel_code=#{channelCode}")
	void updateUseTime(@Param("channelCode") String channelCode, @Param("userName") String userName);

	@Select("select channel_code from sys_user_channel where user_name=#{userName}")
	List<String> getChannelCodeByUserName(@Param("userName") String userName);

	void deleteChannel(@Param("userName") String userName, @Param("codes") List<String> codes, @Param("productCode") String productCode);

	@Insert("insert into sys_user_channel (id,user_id,user_name,channel_code,member_type) values (SELECT REPLACE(UUID(), '-', '') AS id),#{sysUser.id},#{sysUser.username},#{channel.channelCode},#{sysUser.memberType}")
	void save(@Param("channel") ChannelEntity channel, @Param("sysUser") SysUser sysUser);

	void deleteProductChannle(@Param("productCode") String product, @Param("channels") List<String> channels);

	@Select("select DISTINCT product_code from pay_v2_user_product where user_name=#{agentName}")
	List<String> getRelationProducts(@Param("agentName") String agentName);

	@Select("select * from sys_user_channel where user_name=#{loginName} and product_code=#{productCode}")
	List<UserChannelEntity> getChannelByLoginNameAndProduceCode(@Param("loginName") String loginName, @Param("productCode") String productCode);

	@Update("update sys_user_channel set upper_limit=#{upper},lower_limit=#{lower} where user_name=#{name} and channel_code=#{channel} and product_code=#{productCode}")
	void updateRate(@Param("name") String name, @Param("channel") String channel, @Param("productCode") String productCode, @Param("lower") BigDecimal lower, @Param("upper") BigDecimal upper);

	@Select("select * from sys_user_channel where user_name=#{name} and channel_code=#{channel} and product_code=#{product}")
	List<UserChannelEntity> getChannleByUserNameAndChannelAndProduct(@Param("name") String name, @Param("channel") String channel,
																	 @Param("product") String product);

	List<UserChannelEntity> getUserChannel(@Param("channelCodes") List<String> channelCodes, @Param("userName") String userName);

}
