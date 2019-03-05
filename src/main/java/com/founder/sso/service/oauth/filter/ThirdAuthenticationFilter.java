package com.founder.sso.service.oauth.filter;

import com.founder.sso.entity.LoginInfo;
import com.founder.sso.entity.User;
import com.founder.sso.service.PrincipalException;
import com.founder.sso.service.oauth.OauthClientManager;
import com.founder.sso.service.oauth.entity.*;
import com.founder.sso.util.securityCode.CacheUtil;
import com.google.common.collect.Maps;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authc.AuthenticatingFilter;
import org.apache.shiro.web.util.SavedRequest;
import org.apache.shiro.web.util.WebUtils;
import org.joda.time.DateTimeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Map;

@Configuration
public class ThirdAuthenticationFilter extends AuthenticatingFilter {

	@Autowired
	CacheUtil cacheUtil;

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
		System.out.println("============= ThirdAuthenticationFilter onAccessDenied, time: " + DateTimeUtils.currentTimeMillis() + ", url: " + ((HttpServletRequest)request).getRequestURI());
		Map<String, String[]> map = request.getParameterMap();
		Map<String, String> params = Maps.newHashMap();
		;
		for (String key : map.keySet()) {
			params.put(key, map.get(key)[0]);
		}

		String uriPath = ((HttpServletRequest) request).getRequestURI();
		if (uriPath.contains("undefined")) {
			WebUtils.issueRedirect(request, response, "/user/connection/isConnected", params);
			return false;
		}
		/*String redirectUrl = null;
		if (((HttpServletRequest) request).getSession().getAttribute("redirectUrl") != null) {
			redirectUrl = (String) ((HttpServletRequest) request).getSession().getAttribute("redirectUrl");
		}*/

		// 开始处理回调 完成请求的合法性效验和错误信息效验
		OauthErrorMsg errorMsg = OauthClientManager.detectErrors(request);
		if (errorMsg == null) {
			LoginInfo info = (LoginInfo) getSubject(request, response).getPrincipal();
			if (info == null) {
				//TODO buildLocalUser  UserService.generateFace  client.getLocalUser
				info = OauthClientManager.getAuthLoginInfo(request);
				cacheUtil.cacheCode("gp_accessToken::", ((HttpServletRequest) request).getSession().getId(),
						info.getRemoteUser().getAccessToken(), ((HttpServletRequest) request));
				AuthenticationToken token = new OauthToken(info);

				// 请求合法继续处理
				// executeLogin(request, response);
				this.executeLogin(request, response, token);
				boolean firstLogin = false;
				Subject subject = SecurityUtils.getSubject();
				Session session = subject.getSession(false);
				if (session != null && session.getAttributeKeys().contains("firstLogin")) {
					firstLogin = (Boolean) session.getAttribute("firstLogin");
				}
				//登录或者绑定
				//WebUtils.issueRedirect(request, response, "/user/connection/isConnected", params);
				String cachedCode = cacheUtil.getCachedCode("toSsoLogin::" + ((HttpServletRequest) request).getSession().getId(), ((HttpServletRequest) request));
				if (!StringUtils.isEmpty(cachedCode)) {
					//子系统（/user/ssoLogin）
					WebUtils.issueRedirect(request, response, "/user/login", params);
				} else {
					WebUtils.issueRedirect(request, response, "/user/connection/isConnected", params);
				}
			} else {
				// TODO
				if (!StringUtils.isEmpty(request.getParameter("toBind"))) {
					WebUtils.issueRedirect(request, response, "/user/connection/doBindThirdAccount", params);
				} else{
					WebUtils.issueRedirect(request, response, "/user/connection/create", params);
				}
			}
			return false;
		} else {
			throw errorMsg;
		}

	}

	private boolean executeLogin(ServletRequest request, ServletResponse response, AuthenticationToken token) throws Exception {
		if(token == null) {
            String e1 = "createToken method implementation returned null. A valid non-null AuthenticationToken must be created in order to execute a login attempt.";
            throw new IllegalStateException(e1);
        } else {
            try {
				HttpSession session = ((HttpServletRequest) request).getSession();
				session.setAttribute("provider", request.getParameter("provider"));
				// session.setAttribute("provider", provider);
				// session.setAttribute(provider + "_access_token", request.getParameter("access_token"));
                Subject e = this.getSubject(request, response);
                e.login(token);
                return this.onLoginSuccess(token, e, request, response);
            } catch (AuthenticationException var5) {
                return this.onLoginFailure(token, var5, request, response);
            }
        }
	}

}
