package com.founder.sso.service.oauth.entity;

import org.apache.shiro.authc.AuthenticationToken;

import com.founder.sso.entity.LoginInfo;

public class OauthToken implements AuthenticationToken {
    private static final long serialVersionUID = 2351398492348124087L;

    public static enum LOGIN_TYPE{
    	OAUTH_PRINCIPAL, OTHER_SYSTEM;
    }
    
    private LoginInfo info;

    public OauthToken(LoginInfo info) {
        super();
        this.info = info;
    }

    @Override
    public Object getPrincipal() {
        return info;
    }

    @Override
    public Object getCredentials() {
        return "";
    }
    
//
//    private String principal;
//    private Object credentials = "";
//    private String host;
//    private String provider;
//
//    public OauthToken(String principal, String host,String provider) {
//        this.principal = principal;
//        this.host = host;
//        this.provider=provider;
//    }
//
//    public static long getSerialversionuid() {
//        return serialVersionUID;
//    }
//
//    public String getPrincipal() {
//        return principal;
//    }
//
//    public Object getCredentials() {
//        return credentials;
//    }
//
//    public void setPrincipal(String principal) {
//        this.principal = principal;
//    }
//
//    public void setCredentials(Object credentials) {
//        this.credentials = credentials;
//    }
//
//    public String getHost() {
//        return host;
//    }
//
//    public void setHost(String host) {
//        this.host = host;
//    }
//
//    public static String getLoginType() {
//        return LOGIN_TYPE;
//    }
//
//    public String getProvider() {
//        return provider;
//    }
//
//    public void setProvider(String provider) {
//        this.provider = provider;
//    }
//    
    

}
