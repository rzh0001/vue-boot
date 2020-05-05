package org.jeecg.modules.api.util;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * HttpRequestUtil 工具类
 *
 * @author ruanzh
 */
public class HttpRequestUtil {

	/**
	 * 从HttpServletRequest获取 JSON
	 *
	 * @param req
	 * @return
	 */
	public static String getJsonString(HttpServletRequest req) {
		String s = null;
		try {
			BufferedReader reader = req.getReader();
			Stream<String> lines = reader.lines();
			s = lines.collect(Collectors.joining());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return s;
	}
}
