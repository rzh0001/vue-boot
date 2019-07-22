package org.jeecg.modules.util;

import org.apache.commons.lang.StringUtils;
import org.jeecg.modules.exception.RRException;

/**
 * 数据校验
 * @author 
 * @email 
 * @date 2017-03-23 15:50
 */
public class Assert {

    public static void isBlank(Object str, String message) {
        if (StringUtils.isBlank(str+"")) {
            throw new RRException(message);
        }
    }

    public static void isNull(Object object, String message) {
        if (object == null) {
            throw new RRException(message);
        }
    }
}
