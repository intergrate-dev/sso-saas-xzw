package com.founder.sso.util.email;

import java.util.HashMap;
import java.util.Map;

import com.founder.sso.service.oauth.entity.OauthToken;

public class ActsocialEmailTest {
    public static enum LOGIN_TYPE{
    	OAUTH_PRINCIPAL, OTHER_SYSTEM;
    }
    public static void main(String[] args) {
        System.out.println(LOGIN_TYPE.OAUTH_PRINCIPAL.toString());
        
    }
}
