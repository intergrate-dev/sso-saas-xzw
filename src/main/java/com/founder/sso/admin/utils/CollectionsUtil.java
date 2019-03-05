package com.founder.sso.admin.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class CollectionsUtil {

	/**
	 * 判断是否为空.
	 */
    @SuppressWarnings("rawtypes")
	public static boolean isEmpty(Map map) {
		return (map == null) || map.isEmpty();
	}

	/**
	 * 判断是否为空.
	 */
	@SuppressWarnings("rawtypes")
    public static boolean isNotEmpty(Collection collection) {
		return (collection != null) && !(collection.isEmpty());
	}
	
	/**
	 * 将String字符串转换为List<Long>
	 * @param sourceData： String字符串
	 * @param prefix：String字符串分隔符
	 */
	public static List<Long> convetStringToList(String sourceData,String prefix)  {
        
        List<String> stringList = Arrays.asList(sourceData.split(prefix));
        List<Long> longList = new ArrayList<Long>();
        for (String data : stringList) {
        	longList.add(Long.parseLong(data));
		}
        return longList;
    }
	/**
	 * 将以逗号分隔的String字符串转换为List<Long>
	 * @param sourceData： String字符串如，1,2,3,4
	 */
	public static List<Long> convetDotStringToList(String sourceData)  {
		return convetStringToList(sourceData,",");
	}
}
