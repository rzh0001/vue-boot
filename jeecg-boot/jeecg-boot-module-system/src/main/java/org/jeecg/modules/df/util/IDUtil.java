package org.jeecg.modules.df.util;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;

/**
 * @author ruanzh
 * @since 2019/10/27
 */
public class IDUtil {
    
    /**
     * 生成充值订单ID
     *
     * @return
     */
    public static String genRechargeOrderId() {
        return "CZ" + DateUtil.today().replaceAll("-", "") + IdUtil.simpleUUID().substring(24);
    }
    
    /**
     * 生成代付订单ID
     *
     * @return
     */
    public static String genPayOrderId() {
        return "DF" + StrUtil.removeAll(DateUtil.today(), "-") + IdUtil.simpleUUID().substring(24);
    }
}
