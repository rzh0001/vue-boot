package org.jeecg.modules.system.mapper;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Map;

public interface DashboardMapper {
  @Select("select sum(amount) from sys_user_amount where agent_id = #{agentId}")
  public Map<String, Object> summaryUserAmount(@Param("agentId") String agentId);

  public Map<String, Object> userSumary();
}
