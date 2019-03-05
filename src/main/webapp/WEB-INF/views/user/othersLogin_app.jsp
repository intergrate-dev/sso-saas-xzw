<!DOCTYPE html>
<%@page import="com.founder.sso.service.oauth.entity.OauthProviders"%>
<%@page import="com.founder.sso.service.oauth.OauthClient"%>
<%@page import="com.founder.sso.service.oauth.entity.OauthClientConfig"%>
<%@page import="com.founder.sso.service.oauth.OauthClientManager"%>
<%@page import="java.util.List"%>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<% 
response.setHeader("Cache-Control","no-store"); 
response.setHeader("Pragrma","no-cache"); 
response.setDateHeader("Expires",0); 
%>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
		<%
			List<OauthClientConfig> configList = OauthClientManager.getEnabledClientConfig();
			if(configList!=null && configList.size()>0){
		%>
			<div class="OtherLg">
		         <div class="OtherLgText">其它登录方式</div>
		         <div class="lineBox lineLf"></div>
		         <div class="lineBox lineRg"></div>
     		</div>
<div class="TrdLg" style="text-align:center;">
	<ul class="clearfix trdIcons">
		<div id="fb_div" style="margin: 23px 0 0 0;width: 33.33%;float: left;">
			<fb:login-button
					scope="public_profile,email"
					onlogin="checkLoginState();">
			</fb:login-button>
			<div id="status"></div>
		</div>

		<div id="googlePlus_div" style="margin: -6px 0 0 42px;float: left;">
			<span id="signinButton">
				<span
						class="g-signin"
						data-callback="signinCallback"
						data-theme="dark"
						data-clientid=""
						data-cookiepolicy="single_host_origin"
						data-requestvisibleactions="http://schemas.google.com/AddActivity"
						data-scope="https://www.googleapis.com/auth/plus.login">
				</span>
			</span>
		</div>

	</ul>

	<ul class="clearfix trdIcons" style="">
		<%-- clientid --%>

	</ul>
</div>

