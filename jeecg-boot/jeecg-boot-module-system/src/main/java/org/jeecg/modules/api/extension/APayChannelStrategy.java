package org.jeecg.modules.api.extension;

import org.jeecg.common.system.vo.DictModel;
import org.jeecg.modules.pay.entity.OrderInfoEntity;
import org.jeecg.modules.pay.service.ICallBackService;
import org.jeecg.modules.pay.service.IOrderInfoEntityService;
import org.jeecg.modules.pay.service.impl.AsyncNotifyServiceImpl;
import org.jeecg.modules.system.service.ISysDictService;
import org.jeecg.modules.util.BaseConstant;
import org.jeecg.modules.util.RequestHandleUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class APayChannelStrategy implements ICallbackService{
	protected Logger log = LoggerFactory.getLogger(getClass());
	@Autowired
	public ISysDictService dictService;
	@Autowired
	public ICallBackService callBackService;
	@Autowired
	private IOrderInfoEntityService orderInfoEntityService;
	@Autowired
	private AsyncNotifyServiceImpl asyncNotify;
	/**
	 * 请求外部支付通道，生成支付链接
	 *
	 * @param orderInfo
	 * @return
	 */
	public abstract String pay(OrderInfoEntity orderInfo);

	@Override
	public Object callBack(String orderId,String payType) throws Exception{
		return executeCallBack(orderId,payType);
	}
	private final Object executeCallBack(String orderId,String payType) throws Exception{
		if(!this.checkCallBackIpIsOk(payType)){
			log.info("==>回调IP异常，IP未在白名单范围内");
			return "fail";
		}
		Map<String, Object> param =  this.getParam();
		Map<String,Object> callBackParam = this.getCallBackParam(param);
		OrderInfoEntity order = orderInfoEntityService.queryOrderInfoByOrderId(orderId);
		if (order == null || order.getStatus() == 2) {
			log.info("==>通道：{}，回调查询订单信息异常，订单号为：{}",payType,orderId);
			return "fail";
		}
		String apiKey = callBackService.getApikey(orderId,payType);
		if(!this.checkOrderStatusIsOK(callBackParam,apiKey)){
			log.info("==>查询挂马平台订单状态为未支付");
			return "fail";
		}
		if(!this.checkSign(callBackParam,apiKey)){
			log.info("==>签名校验失败");
			return "fail";
		}
		//异步通知客户
		asyncNotify.asyncNotify(orderId,payType);
		//回复三方
		return reply();
	}
	/**
	 * 获取请求参数
	 * @return
	 */
	public Map<String, Object> getParam(){
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
		Object param = RequestHandleUtil.getReqParam(request);
		Map<String, Object> map = (Map<String, Object>) param;
		return map;
	}
	/**
	 * 回复三方
	 * @return
	 */
	public abstract Object reply() throws Exception;

	/**
	 * 校验签名
	 * @param param 三方请求入参
	 * @param apiKey 三方秘钥
	 * @return
	 * @throws Exception
	 */
	public abstract boolean checkSign(Map<String, Object> param,String apiKey) throws Exception;
	/**
	 * 如果不需要对参数进行转换，直接返回入参的map
	 * @param map
	 * @return
	 */
	public abstract Map<String,Object> getCallBackParam(Map<String,Object> map);

	/**
	 * 查询三方订单状态是否成功
	 * @param map 三方请求入参
	 * @return
	 */
	public abstract boolean checkOrderStatusIsOK(Map<String,Object> map,String apiKey) throws Exception;
	/**
	 * 校验回调IP
	 * @param payType
	 * @return
	 */
	boolean checkCallBackIpIsOk(String payType){
		String callBackIp = org.jeecg.common.util.IPUtils.getIpAddr(((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest());
		//IP白名单
		List<String> ips = null;
		List<DictModel> whiteIps = dictService.queryDictItemsByCode(BaseConstant.CALLBACK_IP_WHITE);
		if(!CollectionUtils.isEmpty(whiteIps)){
			ips = whiteIps.stream().filter(model->payType.equals(model.getText()) && org.apache.commons.lang.StringUtils.isNotBlank(model.getValue()))
				.map(model -> Arrays.asList(model.getValue().split(","))).findFirst().get();
		}
		//如果未配置ip白名单则认为都是白名单
		if(!CollectionUtils.isEmpty(ips) && !ips.contains(callBackIp)){
			log.info("==>回调IP白名单校验不通过，IP白名单为：{},回调IP为：{}",ips,callBackIp);
			return false;
		}
		return true;
	}

	public static void main(String[] args) {
		DictModel m = new DictModel();
		m.setText("alipay");
		m.setValue("1,2,3");
		List<DictModel> whiteIps = new ArrayList<>();
		whiteIps.add(m);
		List<String> ips = null;
		if(!CollectionUtils.isEmpty(whiteIps)){
			ips = whiteIps.stream().filter(model->"alipay".equals(model.getText()) && org.apache.commons.lang.StringUtils.isNotBlank(model.getValue()))
				.map(model -> Arrays.asList(model.getValue().split(","))).findFirst().get();
		}
		System.out.println(ips);
	}
}
