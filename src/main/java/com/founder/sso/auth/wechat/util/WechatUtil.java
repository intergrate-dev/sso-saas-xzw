package com.founder.sso.auth.wechat.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Iterator;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.servlet.http.HttpServletRequest;

import com.founder.sso.util.json.JSONObject;
//import net.sf.json.JSONException;
//import net.sf.json.JSONObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.founder.sso.auth.wechat.entity.AccessToken;
import com.founder.sso.auth.wechat.entity.JsapiTicket;

/**
 * 公众平台通用接口工具类
 * 
 * @author hanpt
 */
public class WechatUtil {
	private static Log log = LogFactory.getLog(WechatUtil.class);
	
	/**
	 * 发起https请求并获取结果
	 * 
	 * @param requestUrl 请求地址
	 * @param requestMethod 请求方式（GET、POST）
	 * @param outputStr 提交的数据
	 * @return JSONObject(通过JSONObject.get(key)的方式获取json对象的属性值)
	 */
	public static JSONObject httpsRequest(String requestUrl, String requestMethod, String outputStr) {
		JSONObject jsonObject = null;
		StringBuffer buffer = new StringBuffer();
		BufferedReader bufferedReader = null;
		InputStreamReader inputStreamReader = null;
		InputStream inputStream = null;
		HttpsURLConnection httpUrlConn = null;
		try {
			TrustManager[] tm = {new MyX509TrustManager()};
			SSLContext sslContext = SSLContext.getInstance("SSL", "SunJSSE");
			sslContext.init(null, tm, new java.security.SecureRandom());
			// 从上述SSLContext对象中得到SSLSocketFactory对象
			SSLSocketFactory ssf = sslContext.getSocketFactory();

			URL url = new URL(requestUrl);

			httpUrlConn = (HttpsURLConnection) url.openConnection();
			httpUrlConn.setSSLSocketFactory(ssf);

			httpUrlConn.setDoOutput(true);
			httpUrlConn.setDoInput(true);
			httpUrlConn.setUseCaches(false);
			httpUrlConn.setRequestMethod(requestMethod);

			if ("GET".equalsIgnoreCase(requestMethod)){
				httpUrlConn.connect();
			}

			if (null != outputStr) {
				OutputStream outputStream = httpUrlConn.getOutputStream();
				outputStream.write(outputStr.getBytes("UTF-8"));
				outputStream.close();
			}
			inputStream = httpUrlConn.getInputStream();
			inputStreamReader = new InputStreamReader(inputStream, "utf-8");
			bufferedReader = new BufferedReader(inputStreamReader);

			String str = null;
			while ((str = bufferedReader.readLine()) != null) {
				buffer.append(str);
			}
			//jsonObject = JSONObject.fromObject(buffer.toString());
			jsonObject = new JSONObject(buffer.toString());
		} catch (ConnectException ce) {
			log.error("Weixin server connection timed out.");
		} catch (Exception e) {
			log.error("https request error:{}", e);
		} finally {
			if (bufferedReader != null) {
				try {
					bufferedReader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			if (inputStreamReader != null) {
				try {
					inputStreamReader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			inputStream = null;
			if (httpUrlConn != null) {
				httpUrlConn.disconnect();
			}
		}
		return jsonObject;
	}

	/**
	 * 获取access_token
	 * @param appid 凭证
	 * @param appsecret 密钥
	 * @return
	 */
	public static AccessToken getAccessToken(String appid, String appsecret) throws com.founder.sso.util.json.JSONException {
		AccessToken accessToken = null;
		String requestUrl = WeiXinConstant.access_token_url.replace("APPID", appid).replace("APPSECRET", appsecret);
		JSONObject jsonObject = httpsRequest(requestUrl, "GET", null);
		// 如果请求成功
		if (null != jsonObject) {
			try {
				accessToken = new AccessToken();
				accessToken.setToken(jsonObject.getString("access_token"));
				accessToken.setExpiresIn(jsonObject.getInt("expires_in"));
			} catch (Exception e) {
				accessToken = null;
				// 获取token失败
				log.error("获取token失败 errcode:{} errmsg:{}  "+ jsonObject.getInt("errcode")+"   "+ jsonObject.getString("errmsg"));
			}
		}
		
		
		return accessToken;
	}
	/**
	 * 获取Jsapi_Ticket
	 * @param accessToken 
	 * @return JsapiTicket
	 */
	public static JsapiTicket getJsapiTicket(AccessToken accessToken) throws com.founder.sso.util.json.JSONException {
		JsapiTicket jsapiTicket = null;
		String requestUrl = WeiXinConstant.JSAPI_TICKET_URL.replace("ACCESS_TOKEN", accessToken.getToken());
		JSONObject jsonObject = httpsRequest(requestUrl, "GET", null);
		// 如果请求成功
		if (null != jsonObject) {
			try {
				jsapiTicket = new JsapiTicket();
				jsapiTicket.setExpiresIn(jsonObject.getInt("expires_in"));
				jsapiTicket.setTicket(jsonObject.getString("ticket"));
			} catch (Exception e) {
				jsapiTicket = null;
				// 获取jsapiTicket失败
				log.error("获取jsapiTicket失败 errcode:{} errmsg:{}  "+ jsonObject.getInt("errcode")+"   "+ jsonObject.getString("errmsg"));
			}
		}
		
		
		return jsapiTicket;
	}
	
	/*public static void sendNews(String sJson){
		*//*String requestUrl=WeiXinConstant.url_send.replace("ACCESS_TOKEN", TokenThread.accessToken.getToken());
		log.info("URL      "+requestUrl);*//*
		String requestUrl=WeiXinConstant.authorizationCodeForQQ.replace("client_id", nullForQQ).replace("redirect_uri", "http%3A%2F%2Fweixin.crtp.com.cn%2findex.jhtml");
		JSONObject jsonObject = httpsRequest(requestUrl, "POST", sJson);
		log.info("OUTJSON  "+jsonObject);
	}*/

	/**
	 * 网页授权获取用户信息：1.请求授权，获取code
	 * @param appid 凭证
	 * @return
	 */
	public static String getAuthorizationCode(String appid,String url,String scope) {
		String requestUrl = WeiXinConstant.authorization_code_url.replace("APPID", appid);
		//获取oauth2授权code回调地址
		requestUrl = requestUrl.replace("REDIRECT_URI", urlEnodeUTF8(url));
		//应用授权作用域
		requestUrl = requestUrl.replace("SCOPE", scope);
		//重定向后会带上state参数，自行设置
		requestUrl = requestUrl.replace("STATE", "1");
		return requestUrl;
	}
	/**
	 * 网页授权获取用户信息：2.通过code换取网页授权access_token
	 * @param appid 凭证
	 * @param appsecret 密钥
	 * @return
	 */
	public static JSONObject getAuthorizationAccessToken(String appid, String appsecret,String code) {
		String requestUrl = WeiXinConstant.authorization_access_token_url.replace("APPID", appid).replace("APPSECRET", appsecret).replace("CODE", code);
		JSONObject jsonObject = httpsRequest(requestUrl, "GET", null);
		return jsonObject;
	}
	/**
	 * 网页授权获取用户信息：3.获取用户信息
	 * @return
	 */
	public static JSONObject getAuthorizationUserInfo(String accessToken, String openid) {
		String requestUrl = WeiXinConstant.authorization_userinfo_url.replace("ACCESS_TOKEN", accessToken).replace("OPENID", openid);
		return httpsRequest(requestUrl, "GET", null);
	}
	/**
	 * 直接获取用户信息
	 * @return
	 */
	public static JSONObject getUserInfo(String accessToken, String openid) {
		System.out.println("********openid:********"+openid);
		String requestUrl = WeiXinConstant.userinfo_url.replace("ACCESS_TOKEN", accessToken).replace("OPENID", openid);
		JSONObject jsonObject = httpsRequest(requestUrl, "GET", null);
		log.info("**********获取用户信息：**********");
		log.info(jsonObject);
		return jsonObject;
	}
	/**
	 * 通过OAuth返回的code获取openid，并保持openid到session
	 * @param code
	 * @throws Exception
	 */
	public static String authorizationOpenid(String code,HttpServletRequest request) throws Exception {
		log.info("**********通过OAuth返回的code获取access_token和openid**********");
		log.warn("公众号可通过下述接口来获取网页授权access_token。如果网页授权的作用域为snsapi_base，则本步骤中获取到网页授权access_token的同时，也获取到了openid，snsapi_base式的网页授权流程即到此为止");
		JSONObject jsonObject = WechatUtil.getAuthorizationAccessToken(null, null, code);
		Iterator it = jsonObject.keys();  
        while (it.hasNext()) {  
            String key = (String) it.next();  
            String value = jsonObject.getString(key);  
            System.out.println("********测试WeiXinUtil的JSon--Key："+key+";value:"+value);
        }  
		String openid=jsonObject.getString("openid");
		return openid;
	}
    public static String urlEnodeUTF8(String str){ 
        String result = str; 
        try { 
            result = URLEncoder.encode(str,"UTF-8"); 
        } catch (Exception e) { 
            e.printStackTrace(); 
        } 
        return result; 
    }
	
}