package org.jeecg.modules.pay.service.impl;

import cn.hutool.crypto.SecureUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.jeecg.common.system.vo.DictModel;
import org.jeecg.common.util.DateUtils;
import org.jeecg.common.util.IPUtils;
import org.jeecg.modules.api.exception.BusinessException;
import org.jeecg.modules.pay.entity.OrderInfoEntity;
import org.jeecg.modules.pay.entity.UserBusinessEntity;
import org.jeecg.modules.pay.externalUtils.antUtil.AntUtil;
import org.jeecg.modules.pay.externalUtils.antUtil.GtpaiUtil;
import org.jeecg.modules.pay.service.ICallBackService;
import org.jeecg.modules.pay.service.IOrderInfoEntityService;
import org.jeecg.modules.pay.service.IUserBusinessEntityService;
import org.jeecg.modules.pay.service.factory.CallBackServiceFactory;
import org.jeecg.modules.system.entity.SysUser;
import org.jeecg.modules.system.service.ISysDictService;
import org.jeecg.modules.system.service.ISysUserService;
import org.jeecg.modules.util.*;
import org.jeecg.modules.v2.entity.PayBusiness;
import org.jeecg.modules.v2.service.impl.PayBusinessServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.util.*;

/**
 * @Author: wangjianbin
 * @Date: 2020/3/15 22:48
 */
@Service
@Slf4j
public class CallBackServiceImpl implements ICallBackService {
	@Autowired
	private IOrderInfoEntityService orderInfoEntityService;
	@Autowired
	private PayBusinessServiceImpl payBusinessService;
	@Override
	public String getApikey(String orderNo,String type)throws Exception{
		OrderInfoEntity order = orderInfoEntityService.queryOrderInfoByOrderId(orderNo);
		List<PayBusiness> useBusinesses = payBusinessService.getBusiness(order.getAgentUsername(),order.getPayType(),order.getProductCode());

		if(CollectionUtils.isEmpty(useBusinesses)){
			throw new BusinessException("查询挂马商户信息为空");
		}
		PayBusiness business = useBusinesses.get(0);
		return business.getBusinessApiKey();
	}

	@Override
	public Map<String, Object> getParam(){
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
		Object param = RequestHandleUtil.getReqParam(request);
		Map<String, Object> map = (Map<String, Object>) param;
		return map;
	}

	@Override
	public Object callBack(String orderNoField, String payType) throws Exception {
		return (String)CallBackServiceFactory.getCallBackRequest(payType)
			.callBack(orderNoField,payType);
	}
}
