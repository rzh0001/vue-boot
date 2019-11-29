package org.jeecg.modules.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;
import java.util.Map;

public interface DashboardMapper extends BaseMapper {

  /**
   * 统计代理下用户的总可用额度
   *
   * @param agentId
   * @return
   */
  @Select("select sum(amount) from sys_user_amount where agent_id = #{agentId}")
  BigDecimal summaryUserAmount(@Param("agentId") String agentId);

  BigDecimal userSumary();
}
