package org.jeecg.modules.pay.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.pay.vo.DailyIncomeSummaryVO;

import java.util.Map;

/**
 * @Description: 今日交易统计
 * @Author: ruanzh
 * @Date:   2019-09-02
 * @Version: V1.0
 */
public interface DailyIncomeSummaryMapper extends BaseMapper<DailyIncomeSummaryVO> {
    
    IPage<DailyIncomeSummaryVO> pageSummary(Page page, @Param("map") Map<String, Object> map);
    
}
