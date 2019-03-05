package com.founder.sso.service.oauth.filter;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.shiro.web.filter.authc.FormAuthenticationFilter;
import org.apache.shiro.web.util.SavedRequest;
import org.apache.shiro.web.util.WebUtils;

public class PermitLoginFilter extends FormAuthenticationFilter {

	@Override
	protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
 		Object principal = getSubject(request, response).getPrincipal();
		this.handlerByRememberMe((HttpServletRequest)request, (HttpServletResponse) response);
		if(principal!=null){
				return false;
		}
		if(isLoginRequest(request, response)){
			return false;
		}
		return true;
	}

	@Override
	protected boolean onAccessDenied(ServletRequest request, ServletResponse response)
			throws Exception {
		Object p = getSubject(request, response).getPrincipal();
		System.out.println("request.getParameter "+request.getParameter("anyUrl"));
		HttpServletResponse rep=(HttpServletResponse)response;
		HttpServletRequest req=(HttpServletRequest)request;
		if(p!=null){		
			SavedRequest savedRequest = WebUtils.getSavedRequest(request);
			if(savedRequest!=null)
				WebUtils.redirectToSavedRequest(request, response, savedRequest.getRequestURI());
			else{
				if(request.getParameter("anyUrl")!=null){
					rep.sendRedirect(request.getParameter("anyUrl"));
					System.out.println("---直接登录，点返回，有anyUrl,直接跳转");
					return true;
				} else if(req.getServletPath().contains("login_app")){
					if(req.getSession().getAttribute("anyUrl")!=null)
					rep.sendRedirect(req.getSession().getAttribute("anyUrl").toString());
					System.out.println("---注册完再登录，点返回，sesson里有anyUrl,再跳转");
					return true;
				}
				WebUtils.issueRedirect(request, response, "/user/myHome");
			}
			return false;
		}else{
			return super.onAccessDenied(request, response);
		}
	}

	private void handlerByRememberMe(HttpServletRequest request, HttpServletResponse response) {
		String username = request.getParameter("username");
		String password = request.getParameter("password");
		String checked = request.getParameter("checked");
		// String codeName="";
		if (StringUtils.isEmpty(username) || StringUtils.isEmpty(username) || StringUtils.isEmpty(username)) {
			return;
		}
		/*try {
			codeName = URLEncoder.encode(username, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}*/
		Cookie nameCookie = new Cookie("username", username);
		Cookie passwordCookie = new Cookie("password", password);
		Cookie checkedCookie = new Cookie("checked", checked);
		nameCookie.setPath(request.getContextPath()+"/");
		passwordCookie.setPath(request.getContextPath()+"/");
		checkedCookie.setPath(request.getContextPath()+"/");
		Integer maxAge = 7*24*3600;
		if(checked != null && "true".equals(checked)){
			nameCookie.setMaxAge(maxAge);
			passwordCookie.setMaxAge(maxAge);
			checkedCookie.setMaxAge(maxAge);
		}else{
			nameCookie.setMaxAge(0);
			passwordCookie.setMaxAge(0);
			checkedCookie.setMaxAge(0);
		}
		response.addCookie(nameCookie);
		response.addCookie(passwordCookie);
		response.addCookie(checkedCookie);
	}
}
