package org.jeecg.modules.util;

/**
 * @title:
 * @Description:
 * @author: wangjb
 * @create: 2019-07-22 10:45
 */
public class BaseConstant {
    public static final String USER_NAME="username";
    public static final String TIMESTAMP="timestamp";
    public static final String SIGN="sign";
    public static final String DATA="data";
    public static final String CALLBACK_URL = "callbackUrl";
    public static final String AGENT_NAME = "agentName";
    public static final String REQUEST = "request";
    public static final String CHECK_PARAM_SUCCESS = "0";
    public static final String CODE = "code";
    public static final String DECRYPT_DATA = "dataObj";
    public static final String ORDER_INFO = "orderInfo";
    //订单查询返回字段
    public static final String STATUS = "status";
    public static final String ORDER_ID = "orderId";
    public static final String OUTER_ORDER_ID= "outerOrderId";

    //创建订单的入参字段
    public static final String SUBMIT_AMOUNT= "submitAmount";
    public static final String  PAY_TYPE= "payType";

    //订单状态
    //无效
    public static final int ORDER_STATUS_INVALID = -1;
    //未支付
    public static final int ORDER_STATUS_NOT_PAY = 0;
    //成功，未返回
    public static final int ORDER_STATUS_SUCCESS_NOT_RETURN = 1;
    //成功，已返回
    public static final int ORDER_STATUS_SUCCESS =2;

    public static final String QUERY_ORDER_STATUS_SUCCESS = "4";
    //用户类型
    //1：代理
    public static final String USER_AGENT = "1";
    //3：商户
    public static final String USER_MERCHANTS = "3";
    // 2：介绍人
    public static final String USER_REFERENCES = "2";

    //数据字典配置的挂马平台的地址
    public static final String API_KEY = "apiKey";
    public static final String IP_WHITE_LIST= "ipWhiteList";
    public static final String INNER_CALL_BACK_URL= "innerCallBackUrl";
    public static final String REQUEST_URL = "requestPay";
    public static final String QUERY_ORDER_STATUS_URL = "queryOrderStatusUrl";
    //支付通道
    //云闪付
    public static final String REQUEST_YSF = "ysf";
    //支付宝转账
    public static final String REQUEST_ALI_ZZ = "ali_zz";
    //支付宝转卡
    public static final String REQUEST_ALI_BANK = "ali_bank";
    //农信 微信
    public static final String REQUEST_NXYS_WX = "nxys_wx";
    //农信 支付宝
    public static final String REQUEST_NXYS_ALIPAY = "nxys_alipay";

    //四方回调挂马平台的返回码
    public static final int SUCCESS = 200;
}
