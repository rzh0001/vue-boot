package org.jeecg.modules.pay.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.jeecg.modules.pay.entity.OrderInfoEntity;
import org.jeecg.modules.pay.entity.UserBusinessEntity;

import java.math.BigDecimal;
import java.util.List;

/**
 * @Description: 用户关联商户
 * @Author: jeecg-boot
 * @Date: 2019-07-26
 * @Version: V1.0
 */
public interface UserBusinessEntityMapper extends BaseMapper<UserBusinessEntity> {
	@Select("select * from sys_user_business  where user_name=#{userName} and channel_code=#{channelCode}")
	List<UserBusinessEntity> queryBusiness(@Param("userName") String userName,
										   @Param("channelCode") String channelCode);

	List<UserBusinessEntity> queryBusinessCodeByUserName(@Param("userName") String userName,
														 @Param("channelCode") String channelCode);

	@Select("select * from sys_user_business where user_name=#{userName} and business_code=#{businessCode} and " +
			"channel_code=#{channelCode}")
	List<UserBusinessEntity> queryBusiness2(@Param("userName") String userName,
											@Param("channelCode") String channelCode,
											@Param("businessCode") String businessCode);

	@Select("select * from sys_user_business where user_name=#{username}")
	List<UserBusinessEntity> queryUserBusiness(@Param("username") String username);

	@Select("select * from sys_user_business where user_id=#{userId} and business_code=#{businessCode} and channel_code=#{channelCode} limit 1")
	UserBusinessEntity getUserBusiness(@Param("userId") String userId, @Param("businessCode") String businessCode, @Param("channelCode") String channelCode);

	@Delete("delete from sys_user_business where user_name=#{userName} and business_code=#{businessCode} and " +
			"channel_code=#{channelCode}")
	void deleteUserBusiness(UserBusinessEntity userBusinessEntity);

	@Select("select business_code as businessCode,active,recharge_amount as rechargeAmount, income_amount as incomeAmount from sys_user_business where user_name=#{userName} and " +
			"channel_code = #{channelCode}")
	List<UserBusinessEntity> queryAllBusiness(UserBusinessEntity userBusinessEntity);

	@Select("select business_code from sys_user_business where user_name=#{userName} and channel_code = #{channelCode}")
	List<String> getBusinessCodesByAgentName(@Param("userName") String userName,
											 @Param("channelCode") String channelCode);

	void activeBusiness(@Param("userName") String userName, @Param("channelCode") String channelCode,
						@Param("codes") String[] codes);

	void disableBusiness(@Param("userName") String userName, @Param("channelCode") String channelCode,
						 @Param("codes") String[] codes);

	@Update("update sys_user_business set active='0' where user_name=#{userName} and channel_code=#{channelCode}")
	void disableAllBusiness(@Param("userName") String userName, @Param("channelCode") String channelCode);

	@Update("update sys_user_business set recharge_amount=recharge_amount+#{amount} where user_name=#{userName} and " +
			"channel_code=#{channelCode} and business_code=#{businesses}")
	void rechargeAmount(@Param("userName") String userName, @Param("channelCode") String channelCode, @Param(
			"businesses") String businesses, @Param("amount") BigDecimal amount);

	@Select("select recharge_amount from sys_user_business where user_name=#{userName} and " +
			"channel_code=#{channelCode} and business_code=#{businesses}")
	BigDecimal getRechargeAmount(String userName, String channelCode, String businesses);

	@Update("update sys_user_business set income_amount=income_amount+#{submitAmount} where user_name=#{parentUser} " +
			"and business_code=#{businessCode} and channel_code=#{payType} and active='1'")
	void updateBusinessIncomeAmount(OrderInfoEntity order);

	@Update("update sys_user_business set today_amount=0.000")
	void updateBusinessTodayAmount();

	@Select("select * from sys_user_business where user_id=#{userId}  and channel_code=#{channelCode} limit 1 ")
	UserBusinessEntity getUserChannelConfig(String userId, String channelCode);
}
