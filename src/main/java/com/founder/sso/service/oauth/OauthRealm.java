package com.founder.sso.service.oauth;

import javax.annotation.PostConstruct;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.credential.CredentialsMatcher;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;

import com.founder.sso.entity.LoginInfo;
import com.founder.sso.entity.User;
import com.founder.sso.service.oauth.entity.OauthToken;
/**
 * shiro框架的Realm，用来提供shiro认证的AuthenticationInfo
 * 
 * @author fengdd,zhangmc
 * 
 */
public class OauthRealm extends AuthorizingRealm {

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        // TODO 权限配置
        return null;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        // 这里的OauthToken是在OauthAuthenticationFilter中封装好的:createToken()
        OauthToken oauthToken = (OauthToken) token;
        return new SimpleAuthenticationInfo(oauthToken.getPrincipal(), null, getName());
    }

    @Override
    public boolean supports(AuthenticationToken token) {
        return OauthToken.class.isAssignableFrom(token.getClass());
    }

    /**
     * 因为已经进行了oAuth认证，所以没必要再进行shiro的认证，
     * 只需要验证user是否被冻结即可
     * 1、如果刚刚注册时，user的isActived()
     */
    @PostConstruct
    public void initCredentialsMatcher() {
        setCredentialsMatcher(new CredentialsMatcher() {

            @Override
            public boolean doCredentialsMatch(AuthenticationToken token, AuthenticationInfo info) {
            	boolean theResult = true;
            	OauthToken oauthToken = (OauthToken) token;
            	LoginInfo loginInfo = (LoginInfo)(oauthToken.getPrincipal());
            	if(loginInfo != null){
            		User user = loginInfo.getUser();
            		if(user != null){
            			theResult = user.isActived();
            		}
            	}
                return theResult;
            }
        });
    }

}
