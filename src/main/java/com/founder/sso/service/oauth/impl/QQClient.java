package com.founder.sso.service.oauth.impl;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.founder.sso.admin.utils.Encodes;
import com.founder.sso.service.oauth.OauthClient;
import com.founder.sso.service.oauth.OauthClientManager;
import com.founder.sso.service.oauth.entity.OauthAccessToken;
import com.founder.sso.service.oauth.entity.OauthErrorMsg;
import com.founder.sso.service.oauth.entity.OauthProviders;
import com.founder.sso.service.oauth.entity.RemoteUser;
import com.founder.sso.util.SystemConfigHolder;
import com.founder.sso.util.WebUtil;
import com.founder.sso.util.http.HttpClient;
import com.founder.sso.util.http.Response;
import com.founder.sso.util.json.JSONObject;

/**
 *	这里之所以没用到QQ的SDK，是因为腾讯提供的SDK是基于request，不符合我们的框架 
 * 	@author zhangmc
 */
@Component
public class QQClient extends OauthClient{
	
    private static final Logger log = LoggerFactory.getLogger(QQClient.class);
    private static final OauthProviders OAUTH_PROVIDER = OauthProviders.TENCENT_QQ;
    private HttpClient client = new HttpClient();

    @Override
    public String getProvider() {
        return OAUTH_PROVIDER.getValue();
    }

    @Override
    public String getProviderName() {
        return OAUTH_PROVIDER.getName();
    }

    /**
     * 得到QQ登录页面的url
     */
    @Override
    public String getAuthorizeUrl() {
    	
    	return 
    			"https://graph.qq.com/oauth2.0/authorize?response_type=code&client_id=" + config.getAppId() + "&redirect_uri=" + 
//    			Encodes.urlEncode("http://xiangyuceshi.sinaapp.com:7080/SSOv2/user/oauth2/session/new?provider=tencent_QQ") + 
    			Encodes.urlEncode(OauthClientManager.getAuthCallbackUrl(getProvider())) +
    			"&scope=get_user_info";
    }
    
    /**
     * 得到QQ登录页面的url,用于再次绑定第三方账号
     */
    @Override
    public String getBindingUrl() {
    	
    	return "https://graph.qq.com/oauth2.0/authorize?response_type=code&client_id=" + config.getAppId() + "&redirect_uri=" + 
//    			Encodes.urlEncode("http://xiangyuceshi.sinaapp.com:7080/SSOv2/user/connection/create?provider=tencent_QQ" ) + 
    			Encodes.urlEncode(OauthClientManager.getAuthCallbackUrlBinding(getProvider())) +
    			"&scope=get_user_info";
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

	/**
	 * 获取accessToken，参考QQ文档：http://wiki.open.qq.com/wiki/website/%E4%BD%BF%E7%94%A8Authorization_Code%E8%8E%B7%E5%8F%96Access_Token
	 */
	@Override
	public OauthAccessToken exchangeAccessToken(String authcCode) throws OauthErrorMsg {
		//获取accessToken
		OauthAccessToken accessToken = null;
        String getAccessTokenUrl = "https://graph.qq.com/oauth2.0/token?grant_type=authorization_code&client_id=" + config.getAppId()+ 
        		"&client_secret=" + config.getSecretKey() + "&code=" + authcCode + "&redirect_uri=" + 
//    			Encodes.urlEncode("http://xiangyuceshi.sinaapp.com:7080/SSOv2/user/oauth2/session/new?provider=tencent_QQ")
    			Encodes.urlEncode(OauthClientManager.getAuthCallbackUrl(getProvider()))
    			;

        try {
            Response resp = client.get(getAccessTokenUrl);
            String returnValues = resp.asString();
            if(returnValues.contains("error")){
            	throw new OauthErrorMsg();
            }
            accessToken = new OauthAccessToken(returnValues, OauthProviders.TENCENT_QQ.getValue());
            accessToken.setProvider(getProvider());
        } catch (Exception e) {
        	log.error("QQ登录，在获取accessToken时出错，出错信息：" + e);
        	e.printStackTrace();
            throw new OauthErrorMsg();
        }
        return accessToken;
	}
	/**
	 * 获取accessToken，参考QQ文档：http://wiki.open.qq.com/wiki/website/%E4%BD%BF%E7%94%A8Authorization_Code%E8%8E%B7%E5%8F%96Access_Token
	 */
	@Override
	public OauthAccessToken exchangeAccessTokenForBinding(String authcCode) throws OauthErrorMsg {
		//获取accessToken
		OauthAccessToken accessToken = null;
		String getAccessTokenUrl = "https://graph.qq.com/oauth2.0/token?grant_type=authorization_code&client_id=" + config.getAppId()+ 
				"&client_secret=" + config.getSecretKey() + "&code=" + authcCode + "&redirect_uri=" + 
//				Encodes.urlEncode("http://xiangyuceshi.sinaapp.com:7080/SSOv2/user/connection/create?provider=tencent_QQ")
    			Encodes.urlEncode(OauthClientManager.getAuthCallbackUrlBinding(getProvider()))
    			;
		
		try {
			Response resp = client.get(getAccessTokenUrl);
			String returnValues = resp.asString();
			if(returnValues.contains("error")){
				throw new OauthErrorMsg();
			}
			accessToken = new OauthAccessToken(returnValues, OauthProviders.TENCENT_QQ.getValue());
		} catch (Exception e) {
			log.error("QQ登录，在获取accessToken时出错，出错信息：" + e);
			e.printStackTrace();
			throw new OauthErrorMsg();
		}
		return accessToken;
	}

	/**
	 * 得到QQ的用户信息，这里需要分两步走
	 * 1、根据accessToken得到openid，参考QQ文档：http://wiki.open.qq.com/wiki/website/%E8%8E%B7%E5%8F%96%E7%94%A8%E6%88%B7OpenID_OAuth2.0
	 * 2、根据openid得到用户信息，参考QQ文档：http://wiki.open.qq.com/wiki/website/OpenAPI%E8%B0%83%E7%94%A8%E8%AF%B4%E6%98%8E_OAuth2.0
	 */
	@Override
	public RemoteUser fetchRemoteUser(OauthAccessToken accessToken) {
		RemoteUser remoteUser = null;
		String accessTokenStr = accessToken.getAccessToken();
		if (accessTokenStr.equals("")) {
//          我们的网站被CSRF攻击了或者用户取消了授权 做一些数据统计工作
          System.out.println("没有获取到响应参数");
		} else {
			
			//1、先得到openId
			String getOpenIdUrl = "https://graph.qq.com/oauth2.0/me?access_token=" + accessTokenStr;
			String openId = "";
			try {
				Response openIdResponse = client.get(getOpenIdUrl);
				String jsonp = openIdResponse.asString();
				//腾讯QQ的SDK中得到openId的方式
				Matcher m = Pattern.compile("\"openid\"\\s*:\\s*\"(\\w+)\"").matcher(jsonp);
				if (m.find()){
					openId = m.group(1);
				} else {
				   throw new Exception("get openId server error!");
				}
				
			} catch (Exception e) {
				log.error("QQ登录，在获取openid时出错，出错信息：" + e);
				e.printStackTrace();
			}
			
			//2、根据openId得到 用户信息
			if(StringUtils.isBlank(openId)){
				log.error("QQ登录，没有得到openid");
			}
			String getUserInfoUrl = "https://graph.qq.com/user/get_user_info?access_token=" + accessTokenStr
						+ "&oauth_consumer_key=" + config.getAppId() 
						+ "&openid=" + openId;
			try {
				Response userInfoResponse = client.get(getUserInfoUrl);
				//TODO 需要判断response是否返回正确
				JSONObject json = userInfoResponse.asJSONObject();
				remoteUser = new RemoteUser();
				remoteUser.setAccessToken(accessTokenStr);
				//大頭像
				remoteUser.setAvatarLarge(json.getString("figureurl_2"));
				//中頭像
				remoteUser.setAvatarMiddle(json.getString("figureurl_1"));
				//小頭像
				remoteUser.setAvatarSmall(json.getString("figureurl"));
				//昵称
				remoteUser.setNickname(json.getString("nickname"));
				remoteUser.setProvider(getProvider());
				//token过期时间
				remoteUser.setTokenExpiresTime(accessToken.getTokenExpiresTime());
				remoteUser.setUid(openId);
				
			} catch (Exception e) {
				log.error("QQ登录，在获取QQ用户信息时出错，出错信息：" + e);
				e.printStackTrace();
			}
		}
        return remoteUser;
	}

	@Override
	public RemoteUser getRemoteUser(ServletRequest request) {
		return null;
	}

}
