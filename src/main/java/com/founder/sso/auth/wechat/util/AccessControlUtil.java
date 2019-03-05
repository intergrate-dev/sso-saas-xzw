package com.founder.sso.auth.wechat.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * 访问量控制限制（1分钟100次）
 * 
 * @author hanpt
 */
public class AccessControlUtil {
	private static Logger log = LoggerFactory.getLogger(AccessControlUtil.class);
	// 第三方用户唯一凭证密钥
	private static Map<String,List<Date>> accessControlMap = new HashMap<String,List<Date>>();

	//访问控制的时间段(单位：毫秒)
	private static long time = 60000;
	//访问控制在一定时间段内允许的访问次数
	private static int count = 100;
	
	public static boolean trafficControlByIP(String ip,Date currentTime){
		boolean temp = true;
		List<Date> list = accessControlMap.get(ip);
		if(list == null){
			list = new ArrayList<Date>();
		}
		if(list.size()>0){
			temp = processTrafice(ip,currentTime,list);
		}else{
			list.add(currentTime);
			accessControlMap.put(ip, list);
			temp = true;
		}
		return temp;
	}
	
	/**
	 * 处理访问：查看该ip的所有访问时间，将访问时间超过time的移除，并计算在time之内的数量。
	 * 如果数量小于count，则为true，即允许访问；
	 * 如果数量大于等于count，则为false，即不允许访问。
	 * @param ip
	 * @param date
	 * @param list
	 * @return
	 */
	private static boolean processTrafice(String ip,Date currentTime,List<Date> list){
		boolean temp = true;
		//记录超出time的需要被移除的访问时间
		List<Date> removedList = new ArrayList<Date>();
		
		for(Date historyDate : list){
			long between = (currentTime.getTime() - historyDate.getTime());
			if(between>time){
				removedList.add(historyDate);
			}
		}
		//将出time的需要被移除的访问时间从list中移除
		for(Date dateTemp : removedList){
			list.remove(dateTemp);
		}
		//如果time时间内历史访问数量小于count，则为true，即允许访问；如果数量大于等于count，则为false，即不允许访问
		if(list.size()<count){
			temp = true;
		}else{
			temp = false;
		}
		log.info("在"+time+"毫秒内,IP为："+ip+"的访问次数为"+list.size());
		list.add(currentTime);
		accessControlMap.put(ip, list);
		return temp ;
	}
}