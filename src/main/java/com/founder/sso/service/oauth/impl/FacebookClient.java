package com.founder.sso.service.oauth.impl;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.SocketAddress;
import java.net.URL;
import java.util.Date;

import javax.servlet.ServletRequest;

import org.apache.commons.lang.time.DateUtils;
import org.json.JSONException;
import org.json.JSONObject;

import com.founder.sso.admin.utils.Encodes;
import com.founder.sso.service.oauth.OauthClient;
import com.founder.sso.service.oauth.OauthClientManager;
import com.founder.sso.service.oauth.entity.OauthAccessToken;
import com.founder.sso.service.oauth.entity.OauthErrorMsg;
import com.founder.sso.service.oauth.entity.OauthProviders;
import com.founder.sso.service.oauth.entity.RemoteUser;
import com.founder.sso.util.WebUtil;
import com.founder.sso.util.http.HttpClient;
import com.founder.sso.util.http.Response;
import com.founder.sso.util.http.SocketClient;
import org.springframework.util.StringUtils;

public class FacebookClient extends OauthClient{
	
	private HttpClient client = new HttpClient();
	private static final Integer EXPIRE = 3000000;
	private static final String IMG_PREFIX = "http://graph.facebook.com/";
	private static final String IMG_POSTFIX = "/picture?type=large";
	@Override
	public String getProvider() {
		return OauthProviders.FACEBOOK.getValue();
	}

	@Override
	public String getProviderName() {
		return OauthProviders.FACEBOOK.getName();
	}

	@Override
	public String getAuthorizeUrl() {
		return "";
	}

	@Override
	public String getBindingUrl() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public OauthErrorMsg detectErrorMsg(ServletRequest request) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String parseAuthcCode(ServletRequest request) {
		return WebUtil.getFirstValue(request, "code");
	}

	@Override
	public OauthAccessToken exchangeAccessToken(String authcCode) throws OauthErrorMsg {
		OauthAccessToken accessToken = null;
		String token = "{\"access_token\": \"Founder123\"";
		accessToken = new OauthAccessToken(token, OauthProviders.FACEBOOK.getValue());
		accessToken.setProvider(getProvider());

		return accessToken;
	}

	@Override
	public OauthAccessToken exchangeAccessTokenForBinding(String authcCode) throws OauthErrorMsg {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RemoteUser fetchRemoteUser(OauthAccessToken accessToken) {
		RemoteUser remoteUser = null;
		/*String user_url = "https://graph.facebook.com/me?"
				+ "access_token=" + accessToken.getAccessToken()
				+ "&fields=id,name,birthday,gender,hometown,email,devices";*/
		/*String user_url = "https://graph.facebook.com/me?fields=id,name,birthday,gender,hometown,email,devices";

        String fbUserInfo = SocketClient.doGet(user_url);  
        JSONObject fbUserJson;
		try {
			fbUserJson = new JSONObject(fbUserInfo);
			remoteUser = new RemoteUser();
			remoteUser.setAccessToken(accessToken.getAccessToken());
			remoteUser.setNickname(fbUserJson.getString("name"));
			remoteUser.setProvider(getProvider());
			remoteUser.setTokenExpiresTime(accessToken.getTokenExpiresTime());
			remoteUser.setUid(fbUserJson.getString("id"));
		} catch (JSONException e) {
			e.printStackTrace();
		}*/

        return remoteUser;  
	}

	@Override
	public RemoteUser getRemoteUser(ServletRequest request) {
		RemoteUser remoteUser = null;
		try {
			String imgUrl = request.getParameter("headImg");
			if (StringUtils.isEmpty(imgUrl)) {
				imgUrl = this.getImgUrl(request);
			}
			remoteUser = new RemoteUser();
			remoteUser.setAccessToken(request.getParameter("access_token"));
			remoteUser.setNickname(request.getParameter("userName"));
			remoteUser.setUid(request.getParameter("userID"));
			remoteUser.setProvider(request.getParameter("provider"));
			remoteUser.setAvatarSmall(imgUrl);
			remoteUser.setAvatarMiddle(imgUrl);
			remoteUser.setAvatarLarge(imgUrl);
			remoteUser.setTokenExpiresTime(DateUtils.addMilliseconds(new Date(), FacebookClient.EXPIRE));
		} catch (Exception e) {
			e.printStackTrace();
		}

		return remoteUser;
	}

	private String getImgUrl(ServletRequest request) {
		return FacebookClient.IMG_PREFIX.concat(request.getParameter("userID")).concat(FacebookClient.IMG_POSTFIX);
	}

}
