package org.jeecg.modules.pay.service.impl;

import org.jeecg.modules.pay.entity.UserAmountReport;
import org.jeecg.modules.pay.mapper.UserAmountReportMapper;
import org.jeecg.modules.pay.service.IUserAmountReportService;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

/**
 * @Description: 用户余额报表-期初余额 每天0点更新
 * @Author: jeecg-boot
 * @Date:   2019-09-11
 * @Version: V1.0
 */
@Service
public class UserAmountReportServiceImpl extends ServiceImpl<UserAmountReportMapper, UserAmountReport> implements IUserAmountReportService {

}
