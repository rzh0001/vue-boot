package org.jeecg.modules.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;
import java.util.Map;

public interface DashboardMapper extends BaseMapper {


	@Select("select sum(amount) from sys_user_amount")
	BigDecimal summaryTotalUserAmount();

	@Select("select sum(amount) from sys_user_amount where agent_id = #{agentId}")
	BigDecimal summaryUserAmount(@Param("agentId") String agentId);

	@Select("select sum(amount) from df_pay_order where status = '2'")
	BigDecimal summaryTotalDfPayAmount();

	@Select("select sum(amount) from df_pay_order where agent_id = #{agentId} and status = '2'")
	BigDecimal summaryDfPayAmount(@Param("agentId") String agentId);

	@Select("select sum(order_fee) from df_pay_order where status = '2'")
	BigDecimal summaryTotalDfOrderFee();

	@Select("select sum(order_fee) from df_pay_order where agent_id = #{agentId} and status = '2'")
	BigDecimal summaryDfOrderFee(@Param("agentId") String agentId);

	@Select("select sum(amount) from df_recharge_order where status = '2' and to_days(create_time) = to_days(now())")
	BigDecimal summaryTodayTotalRechargeAmount();

	@Select("select sum(amount) from df_recharge_order where agent_id = #{agentId} "
			+ "and status = '2' and to_days(create_time) = to_days(now())")
	BigDecimal summaryTodayRechargeAmount(@Param("agentId") String agentId);

	@Select("select sum(amount) from df_pay_order where status = '2' and to_days(create_time) = to_days(now())")
	BigDecimal summaryTodayTotalDfPayAmount();

	@Select("select sum(amount) from df_pay_order where agent_id = #{agentId} "
			+ "and status = '2' and to_days(create_time) = to_days(now())")
	BigDecimal summaryTodayDfPayAmount(@Param("agentId") String agentId);

	@Select("select sum(order_fee) from df_pay_order where status = '2' and to_days(create_time) = to_days(now())")
	BigDecimal summaryTodayTotalDfOrderFee();

	@Select("select sum(order_fee) from df_pay_order where agent_id = #{agentId} "
			+ "and status = '2' and to_days(create_time) = to_days(now())")
	BigDecimal summaryTodayDfOrderFee(@Param("agentId") String agentId);

	@Select("select amount from sys_user_amount where user_id = #{userId}")
	BigDecimal getUserAmount(@Param("userId") String userId);

	@Select("select sum(amount) from df_pay_order where user_id = #{userId} and status = '2'")
	BigDecimal summaryUserDfPayAmount(@Param("userId") String userId);

	@Select("select sum(order_fee) from df_pay_order where user_id = #{userId} and status = '2'")
	BigDecimal summaryUserDfOrderFee(@Param("userId") String userId);

	@Select(
			"select sum(amount) from df_recharge_order where user_id = #{userId} "
					+ "and status = '2' and to_days(create_time) = to_days(now())")
	BigDecimal summaryUserTodayRechargeAmount(@Param("userId") String userId);

	@Select(
			"select sum(amount) from df_pay_order where user_id = #{userId} "
					+ "and status = '2' and to_days(create_time) = to_days(now())")
	BigDecimal summaryUserTodayDfPayAmount(@Param("userId") String userId);

	@Select(
			"select sum(order_fee) from df_pay_order where user_id = #{userId} "
					+ "and status = '2' and to_days(create_time) = to_days(now())")
	BigDecimal summaryUserTodayDfOrderFee(@Param("userId") String userId);

	@Select("select count(*) from df_recharge_order where agent_id = #{agentId} and status = '0'")
	Integer countCzOrder(@Param("agentId") String agentId);

	@Select("select count(*) from df_pay_order where agent_id = #{agentId} and status = '0'")
	Integer countDfOrder(@Param("agentId") String agentId);
}
