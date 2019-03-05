package com.founder.sso.service.oauth.filter;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authc.UserFilter;
import org.apache.shiro.web.util.WebUtils;

import com.founder.sso.entity.LoginInfo;
import org.joda.time.DateTimeUtils;


/**
 * Servlet Filter implementation class OauthUserFilter
 */
public class OauthUserFilter extends UserFilter {

	private static String oauthBindingUrl = "/user/connection/isConnected";
	private static String doBindingUrl = "/user/connection/doConnection";
	private static String newBindingUrl = "/user/connection/generateUser";
	//private static String loginApp = "/user/login_app";
	
	@Override
	protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
		System.out.println("============= OauthUserFilter isAccessAllowed start , time: " + DateTimeUtils.currentTimeMillis() + ", url: " + ((HttpServletRequest)request).getRequestURI());
		if (isLoginRequest(request, response)) {
			return true;
		}
		System.out.println("OauthUserFilter: " + WebUtils.getPathWithinApplication(WebUtils.toHttp(request)));
		if(pathsMatch(oauthBindingUrl,request)
//				||pathsMatch(doBindingUrl,request)
//				||pathsMatch(newBindingUrl,request)
				//||pathsMatch(loginApp,request)
				){
			return true;
		}
		Subject subject = getSubject(request, response);
		if(subject.getPrincipal()==null){
			return false;
		}
		LoginInfo loginInfo = (LoginInfo) subject.getPrincipal();
		System.out.println("============= OauthUserFilter isAccessAllowed end , time: " + DateTimeUtils.currentTimeMillis() + ", url: " + ((HttpServletRequest)request).getRequestURI());
		return loginInfo!=null;
	}

	@Override
	protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
		System.out.println("============= OauthUserFilter onAccessDenied, time: " + DateTimeUtils.currentTimeMillis() + ", url: " + ((HttpServletRequest)request).getRequestURI());
		Subject subject = getSubject(request, response);
		if(subject.getPrincipal()==null){
			saveRequestAndRedirectToLogin(request, response);
			return false;
		}
		WebUtils.issueRedirect(request, response, oauthBindingUrl);
		return false;
	}

}
