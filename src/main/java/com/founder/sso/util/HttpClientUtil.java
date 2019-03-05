package com.founder.sso.util;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.*;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;


public class HttpClientUtil {
	
	private final static Logger log = LoggerFactory.getLogger(HttpClientUtil.class);
	
	public String doPost(String url,List<NameValuePair> list,String charset) {
		String result = null;
		CloseableHttpClient httpclient = HttpClients.createDefault();
		// 创建httppost    
		HttpPost httppost = new HttpPost(url);
		UrlEncodedFormEntity uefEntity;
		CloseableHttpResponse response = null;
		try {
			uefEntity = new UrlEncodedFormEntity(list, "UTF-8");
			httppost.setEntity(uefEntity);
			response = httpclient.execute(httppost);
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				result = EntityUtils.toString(entity, charset);
			}
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (response != null) {
				try {
					response.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (httpclient != null) {
				try {
					httpclient.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return result;
	}
	
	public String callAmucAPI(String api,List<NameValuePair> params){
		String inner_api_url = "";
		if(System.getenv("INNER_API_URL") != null && System.getenv("INNER_API_URL") != ""){
			inner_api_url = System.getenv("INNER_API_URL");
		}else{
			inner_api_url = SystemConfigHolder.getConfig("inner_api_url");
		}

		System.out.println("api地址："+api);
		String url = inner_api_url + api;
		System.out.println("最后访问会员中心地址："+url);
		String charset = "utf-8";
		params.add(new BasicNameValuePair("fromSSO","true"));
		String result = doPost(url,params,charset); 
		// log.info("同步amuc会员表返回结果：{}",result);
		return result;
	}
	
	public JedisCluster getClusterInfo() {  
    	String serverInfo = getJedisClusterNodes();
        Set<HostAndPort> set = new HashSet<HostAndPort>();  
        if(serverInfo==null||"".equals(serverInfo.length())) {  
            throw new RuntimeException("The serverInfo can not be empty");  
        }  
        String ipPort[] = serverInfo.split(",");  
        int len = ipPort.length;  
        for(int i=0;i<len;i++) {  
            String server[] = ipPort[i].split(":");  
            set.add(new HostAndPort(server[0], Integer.parseInt(server[1])));  
        }  
        JedisCluster jc = new JedisCluster(set);
        return jc;  
    }
    
	public String getJedisClusterNodes() {  
    	//调用amuc同步接口
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		//params.add(new BasicNameValuePair("",""));
		String res = new HttpClientUtil().callAmucAPI("/api/param/paramConfig.do", params);
		if(res.indexOf("REDIS1_ADDR") == -1){
			return "";
		}
		JSONObject json;
		String REDIS1_ADDR = null;
		String REDIS2_ADDR = null;
		String REDIS3_ADDR = null;
		try {
			json = new JSONObject(res);
			REDIS1_ADDR = json.getString("REDIS1_ADDR");
			REDIS2_ADDR = json.getString("REDIS2_ADDR");
			REDIS3_ADDR = json.getString("REDIS3_ADDR");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		String clusterNodes = REDIS1_ADDR+":7000,"+REDIS2_ADDR+":7001,"+REDIS3_ADDR+":7002,"+REDIS3_ADDR+":7003,"+REDIS1_ADDR+":7004,"+REDIS2_ADDR+":7005";
		return clusterNodes;
    }
}
