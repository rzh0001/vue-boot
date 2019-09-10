package org.jeecg.modules.pay.service.requestPayUrl.impl;

import org.jeecg.modules.pay.entity.OrderInfoEntity;
import org.jeecg.modules.pay.entity.UserBusinessEntity;
import org.jeecg.modules.pay.service.requestPayUrl.RequestPayUrl;
import org.jeecg.modules.util.R;

/**
 * 微信转卡
 */
public class WechatToBankImpl implements RequestPayUrl<OrderInfoEntity, String, String, String,String, UserBusinessEntity> {
    @Override
    public R requestPayUrl(OrderInfoEntity order, String userName, String url, String key, String callbackUrl, UserBusinessEntity userBusiness) throws Exception {
        return null;
    }

    @Override
    public boolean orderInfoOk(OrderInfoEntity order, String url, UserBusinessEntity userBusiness) throws Exception {
        return false;
    }
}
