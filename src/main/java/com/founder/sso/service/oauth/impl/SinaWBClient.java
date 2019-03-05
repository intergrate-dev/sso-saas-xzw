package com.founder.sso.service.oauth.impl;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.Date;

import javax.servlet.ServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.founder.sso.admin.utils.Encodes;
import com.founder.sso.service.oauth.OauthClient;
import com.founder.sso.service.oauth.OauthClientManager;
import com.founder.sso.service.oauth.entity.OauthAccessToken;
import com.founder.sso.service.oauth.entity.OauthClientConfig;
import com.founder.sso.service.oauth.entity.OauthErrorMsg;
import com.founder.sso.service.oauth.entity.OauthProviders;
import com.founder.sso.service.oauth.entity.RemoteUser;
import com.founder.sso.util.BeanMapper;
import com.founder.sso.util.Clock;
import com.founder.sso.util.WebUtil;
import com.google.common.base.Throwables;

import weibo4j.Oauth;
import weibo4j.model.WeiboException;
import weibo4j.util.WeiboConfig;

@Component
public class SinaWBClient extends OauthClient {
    private static final Logger log = LoggerFactory.getLogger(SinaWBClient.class);
    private static final OauthProviders OAUTH_PROVIDER = OauthProviders.SINA_WB;

    @Override
    protected void init(OauthClientConfig cfg) {
        WeiboConfig.updateProperties("client_ID", cfg.getAppId());
        WeiboConfig.updateProperties("client_SERCRET", cfg.getSecretKey());
        WeiboConfig.updateProperties("redirect_URI", OauthClientManager.getAuthCallbackUrl(getProvider()));
    }

    @Override
    public String getProvider() {
        return OAUTH_PROVIDER.getValue();
    }

    @Override
    public String getProviderName() {
        return OAUTH_PROVIDER.getName();
    }

    @Override
    public String getAuthorizeUrl() {
        Oauth oauth = new Oauth();
        try {
            return oauth.authorize("code");
        } catch (WeiboException e) {
            log.error("新浪微博OauthClient获取认证地址故障", e);
            Throwables.propagate(e);
        }
        return null;
    }
    
    // TODO 以后再处理 
    @Override
    public String getBindingUrl() {
    	return WeiboConfig.getValue("authorizeURL").trim() + "?client_id="
				+ WeiboConfig.getValue("client_ID").trim() + "&redirect_uri="
				+ Encodes.urlEncode(OauthClientManager.getAuthCallbackUrlBinding(getProvider()))
//				+ WeiboConfig.getValue("redirect_URI").trim()
				+ "&response_type=code";
    }

    @Override
    public String parseAuthcCode(ServletRequest request) {
        String code = WebUtil.getFirstValue(request, "code");
        checkArgument(StringUtils.isNotBlank(code), "没有获得Code参数");
        return code;
    }

    //	TODO 可能不需要此方法
    @Override
    public OauthAccessToken exchangeAccessTokenForBinding(String authcCode) throws OauthErrorMsg {
    	return exchangeAccessToken(authcCode);
    }
    
    @Override
    public OauthAccessToken exchangeAccessToken(String authcCode) throws OauthErrorMsg {
        OauthAccessToken accessToken = new OauthAccessToken();
        Oauth oauth = new Oauth();
        try {
            weibo4j.http.AccessToken sinaAccessToken = oauth.getAccessTokenByCode(authcCode);
            long expiresTime = Long.parseLong(sinaAccessToken.getExpireIn()) * 1000 + Clock.DEFAULT.getCurrentTimeInMillis();
            accessToken.setTokenExpiresTime(new Date(expiresTime));
            accessToken.setAccessToken(sinaAccessToken.getAccessToken());
            accessToken.setUid(sinaAccessToken.getUid());
            accessToken.setProvider(getProvider());
            return accessToken;
        } catch (WeiboException e) {
        	e.printStackTrace();
            // TODO fengdd 完善异常处理机制
            throw new OauthErrorMsg();
        }
    }

    @Override
    public RemoteUser fetchRemoteUser(OauthAccessToken accessToken) {
        weibo4j.Users remoteUserManager = new weibo4j.Users(accessToken.getAccessToken());
        try {
            weibo4j.model.User remoteUser = remoteUserManager.showUserById(accessToken.getUid());
             //不特殊处理远程用户 直接映射为标准封装返回
            RemoteUser defaultRemoteUser = BeanMapper.map(remoteUser, RemoteUser.class);
            defaultRemoteUser.setAccessToken(accessToken.getAccessToken());
            defaultRemoteUser.setTokenExpiresTime(accessToken.getTokenExpiresTime());
            defaultRemoteUser.setProvider(getProvider());
            defaultRemoteUser.setUid(accessToken.getUid());
            return defaultRemoteUser;
            
//        	long expiresTime = 5 * 60 * 1000 * 1000 + Clock.DEFAULT.getCurrentTimeInMillis();
//            RemoteUser defaultRemoteUser = new RemoteUser();
//            defaultRemoteUser.setAccessToken("2.008eYLJEurlseDed4f81672dUZUvyB");
//            defaultRemoteUser.setTokenExpiresTime(new Date(expiresTime));
//            defaultRemoteUser.setProvider(getProvider());
//            defaultRemoteUser.setNickname("springdb");
//            defaultRemoteUser.setUid("3800273163");
//            defaultRemoteUser.setAvatarSmall("http://tp1.sinaimg.cn/5142549376/50/0/1");
//            defaultRemoteUser.setAvatarMiddle("http://tp1.sinaimg.cn/5142549376/180/0/1");
//            defaultRemoteUser.setAvatarLarge("http://tp1.sinaimg.cn/5142549376/180/0/1");
//            return defaultRemoteUser;
        } catch (WeiboException e) {
            // TODO fengdd 完善异常处理机制
            throw new OauthErrorMsg();
        }
    }

    @Override
    public RemoteUser getRemoteUser(ServletRequest request) {
        return null;
    }

    @Override
    public OauthErrorMsg detectErrorMsg(ServletRequest request) {
        // TODO Auto-generated method stub
        return null;
    }

}
