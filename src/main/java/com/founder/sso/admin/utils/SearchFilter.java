package com.founder.sso.admin.utils;

import java.util.Map;
import java.util.Map.Entry;
import org.apache.commons.lang3.StringUtils;
import com.google.common.collect.Maps;

/**
 * 模仿springside中的SearchFilter类
 * @author zhangmc
 */
public class SearchFilter {
	
	public Object value;
	public String fieldName;
	public Operator operator;
	
	public enum Operator {
		EQ, LIKE, GT, LT, GTE, LTE
	}
	
	public SearchFilter(String fieldName, Operator operator, Object value) {
		this.fieldName = fieldName;
		this.value = value;
		this.operator = operator;
	}
	
	public static Map<String,SearchFilter> parse(Map<String, Object> searchParams){
		
		Map<String,SearchFilter> filters = Maps.newHashMap();
		for (Entry<String, Object> entry : searchParams.entrySet()) {
			String key = entry.getKey();
			Object value = entry.getValue();
			//过滤空值
			if(value == null){
				continue;
			}else if(value instanceof java.lang.String && StringUtils.isBlank(value.toString())){
				continue;
			}
			
			String[] names = StringUtils.split(key,"_");
			if(names.length != 2){
				throw new IllegalArgumentException(key + " is not a valid search filter name");
			}
			
			String filedName = names[1];
			Operator operator = Operator.valueOf(names[0]);
			
			// 创建searchFilter
			SearchFilter filter =new SearchFilter(filedName,operator,value);
			filters.put(key, filter);
		}
		return filters;
	}
}
