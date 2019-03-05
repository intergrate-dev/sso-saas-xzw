package com.founder.sso.auth.wechat.util;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
/**
 * 查询IP工具类
 * @author guo.zd
 */
public class IpAddressUtil {
	public static String getRemoteIpAddr() {
		HttpServletRequest request = getRequest();
		String ip = request.getHeader("x-forwarded-for");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}
		return ip;
	}

	public static HttpSession getSession() {
		return getRequest().getSession();
	}

	public static HttpServletRequest getRequest() {
		//return ServletActionContext.getRequest();
		return ( (ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();  
	}
}
