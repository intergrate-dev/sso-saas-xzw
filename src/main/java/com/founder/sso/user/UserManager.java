package com.founder.sso.user;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
@SuppressWarnings("deprecation")
public class UserManager {
	
	private static final Logger log = LoggerFactory.getLogger(UserManager.class);
	public static HttpClient httpclient = null;
	static {
		httpclient = new DefaultHttpClient();
	}
	public static boolean AuthcAccount(String url, String username,String pwd)  {
		
		HttpPost httpost = new HttpPost(url);
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("username", username)); // 用户名称
		params.add(new BasicNameValuePair("pwd", pwd)); // 用户名称
		try {
			httpost.setEntity(new UrlEncodedFormEntity(params, "utf-8"));
			HttpResponse response = httpclient.execute(httpost);
			String jsonStr = EntityUtils.toString(response.getEntity(), "utf-8");
			System.out.println(jsonStr);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}

}
