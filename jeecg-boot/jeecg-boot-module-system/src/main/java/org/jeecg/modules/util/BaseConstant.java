package org.jeecg.modules.util;

/**
 * @title:
 * @Description:
 * @author: wangjb
 * @create: 2019-07-22 10:45
 */
public class BaseConstant {
    public static final String USER_ID = "userId";
    public static final String USER_NAME="username";
    public static final String TIMESTAMP="timestamp";
    public static final String SIGN="sign";
    public static final String DATA="data";

    public static final String CHECK_PARAM_SUCCESS = "0";
    //订单查询返回字段
    public static final String STATUS = "status";
    public static final String ORDER_ID = "orderId";
    public static final String OUTER_ORDER_ID= "outerOrderId";

    //创建订单的入参字段
    public static final String SUBMIT_AMOUNT= "submitAmount";
    public static final String  PAY_TYPE= "payType";

    //支付通道
    public static final String PAY_CLOUD = "YSF";

    //订单状态
    //无效
    public static final int ORDER_STATUS_INVALID = -1;
    //未支付
    public static final int ORDER_STATUS_NOT_PAY = 0;
    //成功，未返回
    public static final int ORDER_STATUS_SUCCESS_NOT_RETURN = 1;
    //成功，已返回
    public static final int ORDER_STATUS_SUCCESS =2;
}
