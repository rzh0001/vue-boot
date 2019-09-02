package org.jeecg.modules.pay.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.pay.mapper.DailyIncomeSummaryMapper;
import org.jeecg.modules.pay.service.IDailyIncomeSummaryService;
import org.jeecg.modules.pay.vo.DailyIncomeSummaryVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @Description: 今日交易统计
 * @Author: jeecg-boot
 * @Date:   2019-09-02
 * @Version: V1.0
 */
@Slf4j
@Service
public class DailyIncomeSummaryServiceImpl extends ServiceImpl<DailyIncomeSummaryMapper, DailyIncomeSummaryVO> implements IDailyIncomeSummaryService {
    
    @Autowired
    private DailyIncomeSummaryMapper mapper;
    
    @Override
    public IPage<DailyIncomeSummaryVO> pageSummary(Page page, Map<String, Object> map) {
        return mapper.pageSummary(page, map);
    }
}
