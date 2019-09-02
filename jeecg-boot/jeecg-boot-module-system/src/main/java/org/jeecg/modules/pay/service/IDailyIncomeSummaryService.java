package org.jeecg.modules.pay.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.pay.vo.DailyIncomeSummaryVO;

import java.util.Map;

/**
 * @Description: 今日交易统计
 * @Author: jeecg-boot
 * @Date:   2019-09-02
 * @Version: V1.0
 */
public interface IDailyIncomeSummaryService extends IService<DailyIncomeSummaryVO> {
    
    IPage<DailyIncomeSummaryVO> pageSummary(Page page, Map<String, Object> map);
}
