package org.jeecg.modules.pay.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.pay.entity.UserAmountReport;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * @Description: 用户余额报表-期初余额 每天0点更新
 * @Author: jeecg-boot
 * @Date:   2019-09-11
 * @Version: V1.0
 */
public interface UserAmountReportMapper extends BaseMapper<UserAmountReport> {

}
