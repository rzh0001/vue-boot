package org.jeecg.modules.pay.controller;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.pay.entity.OrderInfoEntity;
import org.jeecg.modules.pay.entity.TransactionVolumeVO;
import org.jeecg.modules.pay.service.ITransactionVolumeService;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;

/**
 * @Description: d
 * @Author: jeecg-boot
 * @Date: 2020-04-30
 * @Version: V1.0
 */
@Slf4j
@Api(tags = "d")
@RestController
@RequestMapping("/pay/transactionVolume")
public class TransactionVolumeController {
	@Autowired
	private ITransactionVolumeService transactionVolumeService;


	// 查询条件独立成方法，查询、统计、导出 三个接口使用
	private QueryWrapper<OrderInfoEntity> initQueryCondition(OrderInfoEntity orderInfoEntity, HttpServletRequest req) {
		LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();

		QueryWrapper<OrderInfoEntity> queryWrapper = QueryGenerator.initQueryWrapper(orderInfoEntity,
				req.getParameterMap(), false);

		queryWrapper.lambda().eq(OrderInfoEntity::getStatus, "2");
		queryWrapper.groupBy("agent_id", "agent_username");
		return queryWrapper;
	}

	/**
	 * 分页列表查询
	 *
	 * @param orderInfo
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "d-分页列表查询")
	@ApiOperation(value = "d-分页列表查询", notes = "d-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<TransactionVolumeVO>> queryPageList(OrderInfoEntity orderInfo,
															@RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
															@RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
															HttpServletRequest req) {
		Result<IPage<TransactionVolumeVO>> result = new Result<IPage<TransactionVolumeVO>>();
		QueryWrapper<OrderInfoEntity> queryWrapper = initQueryCondition(orderInfo, req);
		Page<OrderInfoEntity> page = new Page<OrderInfoEntity>(pageNo, pageSize);
		IPage<TransactionVolumeVO> pageList = transactionVolumeService.selectPage(page, queryWrapper);
		result.setSuccess(true);
		result.setResult(pageList);
		return result;
	}


	/**
	 * 导出excel
	 *
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = "/exportXls")
	public ModelAndView exportXls(HttpServletRequest request, HttpServletResponse response) {
		// Step.1 组装查询条件
		QueryWrapper<TransactionVolumeVO> queryWrapper = null;
		try {
			String paramsStr = request.getParameter("paramsStr");
			if (oConvertUtils.isNotEmpty(paramsStr)) {
				String deString = URLDecoder.decode(paramsStr, "UTF-8");
				TransactionVolumeVO transactionVolumeVO = JSON.parseObject(deString, TransactionVolumeVO.class);
				queryWrapper = QueryGenerator.initQueryWrapper(transactionVolumeVO, request.getParameterMap());
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		//Step.2 AutoPoi 导出Excel
		ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
		List<TransactionVolumeVO> pageList = transactionVolumeService.list(queryWrapper);
		//导出文件名称
		mv.addObject(NormalExcelConstants.FILE_NAME, "d列表");
		mv.addObject(NormalExcelConstants.CLASS, TransactionVolumeVO.class);
		mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("d列表数据", "导出人:Jeecg", "导出信息"));
		mv.addObject(NormalExcelConstants.DATA_LIST, pageList);
		return mv;
	}


}
