package org.jeecg.modules.system.util;

import javax.servlet.http.HttpServletRequest;

public class IPUtils {

	private static final String UNKNOWN = "unknown";

	protected IPUtils(){

	}

	/**
	 * 使用ng代理，获取真实ip;
	 * 1、在ng中配置
	 * proxy_set_header Host $host;
	 * proxy_set_header X-Real-IP $remote_addr;
	 * proxy_set_header REMOTE-HOST $remote_addr;
	 * proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
	 * 2、
	 * 使用 Nginx等反向代理软件， 则不能通过request.getRemoteAddr()获取IP地址
	 * 如果使用了多级反向代理的话，X-Forwarded-For的值并不止一个，而是一串IP地址，X-Forwarded-For中第一个非 unknown的有效IP字符串，则为真实IP地址
	 */
	public static String getIpAddr(HttpServletRequest request) {
		String ip = request.getHeader("x-forwarded-for");
		if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
			 ip = request.getRemoteAddr();
		}
		return "0:0:0:0:0:0:0:1".equals(ip) ? "127.0.0.1" : ip;
	}

}
