package com.founder.sso.admin.utils;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import javax.servlet.ServletRequest;

import org.apache.commons.lang3.Validate;

/**
* Http与Servlet工具类.模仿的springside中的Servlets类
* 
* @author zhangmichao
*/
public class ServletsUtil {
	
	/**
	 * 取得带相同前缀的Request Parameters, copy from spring WebUtils.
	 * 
	 * 返回的结果的Parameter名已去除前缀.
	 */
	public static Map<String, Object> getParametersStartingWith(ServletRequest request,String prefix){
		Validate.notNull(request, "Request must not be null");
		Enumeration<String> paramNames = request.getParameterNames();
		Map<String,Object> params = new TreeMap<String,Object>();
		if(prefix == null){
			prefix = "";
		}
		while((paramNames != null) && paramNames.hasMoreElements()){
			String paramName = (String)paramNames.nextElement();
			if("".equals(prefix) || paramName.startsWith(prefix)){
				
				String unprefixed = paramName.substring(prefix.length());
				String[] values = request.getParameterValues(paramName);
				if(values == null || values.length == 0){
					//do nothing
				}else if(values.length>1){
					params.put(unprefixed, values);
				}else {
					params.put(unprefixed, values[0].trim());
				}
				
			}
		}
		return params;
	}
	
	/**
	 * 组合Parameters生成Query String的Parameter部分, 并在paramter name上加上prefix.
	 * 
	 * @see #getParametersStartingWith
	 */
	public static String encodeParameterStringWithPrefix(Map<String, Object> params, String prefix) {
		
		if(CollectionsUtil.isEmpty(params)){
			return "";
		}
		if(prefix == null){
			prefix = "";
		}
		
		StringBuilder queryStringBuilder = new StringBuilder(); 
		Iterator<Entry<String, Object>> it = params.entrySet().iterator();
		while (it.hasNext()) {
			Entry<String, Object> entry = it.next();
			queryStringBuilder.append(prefix).append(entry.getKey()).append("=").append(entry.getValue());
			if(it.hasNext()){
				queryStringBuilder.append("&");
			}
		}
		return queryStringBuilder.toString();
	}
}
