package com.founder.sso.service.oauth.filter;

import java.util.Map;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authc.AuthenticatingFilter;
import org.apache.shiro.web.util.SavedRequest;
import org.apache.shiro.web.util.WebUtils;
import org.springframework.util.Assert;

import com.founder.sso.entity.LoginInfo;
import com.founder.sso.entity.User;
import com.founder.sso.service.oauth.OauthClientManager;
import com.founder.sso.service.oauth.entity.OauthErrorMsg;
import com.founder.sso.service.oauth.entity.OauthToken;
import com.founder.sso.service.oauth.entity.RemoteUser;
import com.google.common.collect.Maps;

public class OauthAuthenticationFilter extends AuthenticatingFilter {

	@Override
	protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
		return false;
	}

	@Override
	protected AuthenticationToken createToken(ServletRequest request, ServletResponse response) throws Exception {

		LoginInfo info = OauthClientManager.doOauth2Process(request);
		return new OauthToken(info);
	}

	@Override
	protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
		Map<String, String[]> map = request.getParameterMap();
		Map<String, String> params = Maps.newHashMap();
		;
		for (String key : map.keySet()) {
			params.put(key, map.get(key)[0]);
		}
		// 开始处理回调 完成请求的合法性效验和错误信息效验
		OauthErrorMsg errorMsg = OauthClientManager.detectErrors(request);
		if (errorMsg == null) {
			LoginInfo info = (LoginInfo) getSubject(request, response).getPrincipal();
			if (info == null) {
				// 请求合法继续处理
				executeLogin(request, response);
				boolean firstLogin = false;
				Subject subject = SecurityUtils.getSubject();
				Session session = subject.getSession(false);
				if (session != null && session.getAttributeKeys().contains("firstLogin")) {
					firstLogin = (Boolean) session.getAttribute("firstLogin");
				}
				if (firstLogin) {
					WebUtils.issueRedirect(request, response, "/user/connection/isConnected", params);
				} else {
					SavedRequest savedRequest = WebUtils.getSavedRequest(request);
					if (savedRequest != null)
						WebUtils.redirectToSavedRequest(request, response, savedRequest.getRequestURI());
					else
						WebUtils.issueRedirect(request, response, getSuccessUrl());
				}
			} else {
				WebUtils.issueRedirect(request, response, "/user/connection/create", params);
			}
			return false;
		} else {
			throw errorMsg;
		}

	}

}
