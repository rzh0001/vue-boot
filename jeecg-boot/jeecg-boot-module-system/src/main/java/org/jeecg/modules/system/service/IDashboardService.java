package org.jeecg.modules.system.service;

import org.jeecg.common.api.vo.Result;

import java.util.Map;

/**
 * 首页服务类
 *
 * @author ruanzh
 * @since 2019-09-01
 */
public interface IDashboardService {
	/**
	 * 汇总用户数据
	 *
	 * @return
	 */
	Result<Map<String, Object>> homepageSummary();
}
