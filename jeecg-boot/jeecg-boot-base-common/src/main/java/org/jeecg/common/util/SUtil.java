package org.jeecg.common.util;

import org.slf4j.helpers.MessageFormatter;

/**
 * String 工具类
 *
 * @author ruanzh
 * @date 2020-04-25 21:56:12
 */
public class SUtil {


	/**
	 * 字符串拼接（调用Slf4j源码）
	 *
	 * @param format
	 * @param args
	 * @return
	 */
	public static String concat(String format, Object... args) {
		return MessageFormatter.arrayFormat(format, args).getMessage();
	}


}
