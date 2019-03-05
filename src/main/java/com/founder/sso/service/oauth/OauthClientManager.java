package com.founder.sso.service.oauth;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.founder.sso.entity.LoginInfo;
import com.founder.sso.entity.User;
import com.founder.sso.service.oauth.entity.OauthAccessToken;
import com.founder.sso.service.oauth.entity.OauthClientConfig;
import com.founder.sso.service.oauth.entity.OauthErrorMsg;
import com.founder.sso.service.oauth.entity.OauthToken;
import com.founder.sso.service.oauth.entity.RemoteUser;
import com.founder.sso.util.SystemConfigHolder;
import com.founder.sso.util.WebUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class OauthClientManager {
    private static Logger log = LoggerFactory.getLogger(OauthClientManager.class);

    private static String authCallbackBaseUrl = SystemConfigHolder.getConfig("default_auth_callback_url");
    private static String authCallbackBaseUrlBinding = SystemConfigHolder.getConfig("default_auth_callback_url_binding");
    private static String providerParamName = SystemConfigHolder.getConfig("default_provider_flag");

    private static Map<String, OauthClient> clients = Maps.newHashMap();

    //将各家的client放入clients内存中
    public static void registerClient(String provider, OauthClient client) {
        checkArgument(StringUtils.isNotBlank(provider), "OauthClient provider Cant NOT be Blank");
        checkArgument(client != null, "Oauth Client refrenece Can NOT be Null");
        if (checkConfig(client.getConfig())) {
            if (clients.get(provider) != null) {
                log.warn("OauthClient[" + provider + "] Has Been Registered Replacing!");
            }
            clients.put(provider, client);
        } else {
            log.warn("OauthClient[" + provider + "] Config may contains error skipping!");
        }
    }

    private static boolean checkConfig(OauthClientConfig config) {
        if (StringUtils.isBlank(config.getProvider())) {
            log.warn("OauthClient[" + config.getId() + "] Config retrun a BLANK provider!");
            return false;
        }
        if (config.isEnabled()) {
            if (StringUtils.isBlank(config.getAppId())) {
                log.warn("OauthClient[" + config.getId() + "] Config retrun a BLANK AppID!");
                return false;
            }
            if (StringUtils.isBlank(config.getSecretKey())) {
                log.warn("OauthClient[" + config.getId() + "] Config retrun a BLANK SecretKey!");
                return false;
            }
        }
        return true;
    }

    /**
     * 获取此Oauth供应商标示在请求参数中的属性名
     * 在处理Oauth供应商的callback时通过此属性的值明确应该由哪个Client处理
     * 
     * @return 此Oauth供应商标示在callback地址中的属性名
     */
    public static String getProviderParamName() {
        return providerParamName;
    }

    /**
     * 获取指定标示的Oauth认证地址
     * 
     * @param provider 供应商标示
     * @return 如果不存在此供应商则返回 当前锚点
     */
    public static String getAuthorizeUrl(String provider) {
        String url = "#";
        OauthClient client = clients.get(provider);
        //判断config此时是否启用,如果启用的话才能得到地址,否则得到的地址为#
        OauthClientConfig config = client.getConfig();
        
        if (config.isEnabled() && client != null) {
            url = client.getAuthorizeUrl();
            if(StringUtils.isBlank(url)){
            	url = "#";
            }
        }
        return url;
    }
    
    /**
     * 获取指定标示绑定的地址，其实和getAuthorizeUrl()得到的url一样，只是return_uri不一样
     * 
     * @param provider 供应商标示
     * @return 如果不存在此供应商则返回 当前锚点
     */
    public static String getBindingUrl(String provider) {
    	String url = "#";
    	OauthClient client = clients.get(provider);
    	//判断config此时是否启用,如果启用的话才能得到地址,否则得到的地址为#
        if (client != null) {
            OauthClientConfig config = client.getConfig();
            if (config.isEnabled() && client != null) {
                url = client.getBindingUrl();
                if(StringUtils.isBlank(url)){
                    url = "#";
                }
            }
        }
    	return url;
    }


    /**
     * 获取指定标示的Oauth认证地址
     * 
     * @return 如果不存在此供应商则返回 当前锚点
     */
    public static List<OauthClientConfig> getEnabledClientConfig() {
        Set<String> keySet = clients.keySet();
        List<OauthClientConfig> configList = Lists.newArrayList();
        for (String key : keySet) {
			OauthClient client = clients.get(key);
			OauthClientConfig config = client.getConfig();
			if(config.isEnabled()){
				configList.add(config);
			}
		}
       
        return configList;
    }
    
    /**
     * 获取Oauth认证回调地址
     * 
     * @param provider Client标示
     * @return
     */
    public static String getAuthCallbackUrl(String provider) {
        return authCallbackBaseUrl + "?" + getProviderParamName() + "=" + provider;
    }
    
    /**
     * 获取Oauth认证回调地址，用于再次绑定账号
     * 
     * @param provider Client标示
     * @return
     */
    public static String getAuthCallbackUrlBinding(String provider) {
    	return authCallbackBaseUrlBinding + "?" + getProviderParamName() + "=" + provider;
    }

    /**
     * 通过请求参数集判定响应的Client实例
     * 
     * @param request
     * @return
     */
    public static OauthClient detectClient(ServletRequest request) {
        String providerFlag = WebUtil.getFirstValue(request, getProviderParamName());
        checkArgument(StringUtils.isNotBlank(providerFlag), "参数错误-供应商标示值为空");
        return getClient(providerFlag);
    }

    /**
     * 处理Oauth授权回调 主要完成查找特定供应商的Client并委托进行Error信息处理
     * 
     * @param mappedValues 请求
     * @return 客户端处理的供应商返回的错误信息 无则返回null
     */
    public static OauthErrorMsg detectErrors(ServletRequest request) {
        OauthClient client = detectClient(request);
        return client.detectErrorMsg(request);
    }

    public static OauthClient getClient(String providerFlag) {
        OauthClient oauthClient = clients.get(providerFlag);
        checkNotNull(oauthClient, "不支持的供应商或供应商标示错误");
        return oauthClient;
    }

    /**
     * 用第三方账号注册时：
     * 从第三方那得到用户信息封装成对象，此时并保存到数据库，如果用户选择跳过关联账号后才会保存user等数据(generateUser)
     */
    public static LoginInfo doOauth2Process(ServletRequest request) {
		OauthClient client = detectClient(request);
		String authcCode = client.parseAuthcCode(request);
		OauthAccessToken accessToken = client.exchangeAccessToken(authcCode);
		RemoteUser remoteUser = client.fetchRemoteUser(accessToken);
        User localUser = client.getLocalUser(remoteUser);

        return new LoginInfo(localUser, request.getRemoteHost(), OauthToken.LOGIN_TYPE.OAUTH_PRINCIPAL.toString(), remoteUser);
    }
    
    /**
     * facebook
     * 根据request,生成RemoteUser
     */
    public static RemoteUser getRemoteUser(ServletRequest request) {
		OauthClient client = detectClient(request);
		String authcCode = client.parseAuthcCode(request);
		OauthAccessToken accessToken = client.exchangeAccessTokenForBinding(authcCode);
		RemoteUser remoteUser = client.fetchRemoteUser(accessToken);
		return remoteUser;
    }

    /**
     * 用第三方账号注册时：
     * 从第三方那得到用户信息封装成对象，此时并保存到数据库，如果用户选择跳过关联账号后才会保存user等数据(generateUser)
     */
    public static LoginInfo getAuthLoginInfo(ServletRequest request) {
        OauthClient client = detectClient(request);
        RemoteUser remoteUser = client.getRemoteUser(request);
        User localUser = client.getLocalUser(remoteUser);
        return new LoginInfo(localUser, request.getRemoteHost(), OauthToken.LOGIN_TYPE.OAUTH_PRINCIPAL.toString(), remoteUser);
    }

}
