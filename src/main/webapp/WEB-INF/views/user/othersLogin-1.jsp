<!DOCTYPE html>
<%@page import="com.founder.sso.service.oauth.entity.OauthProviders" %>
<%@page import="com.founder.sso.service.oauth.OauthClient" %>
<%@page import="com.founder.sso.service.oauth.entity.OauthClientConfig" %>
<%@page import="com.founder.sso.service.oauth.OauthClientManager" %>
<%@page import="java.util.List" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%
    response.setHeader("Cache-Control", "no-store");
    response.setHeader("Pragrma", "no-cache");
    response.setDateHeader("Expires", 0);
%>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<div style="clear:both;" id="_hr">
    <div class="left">
        <hr style="width: 100px;margin-top:10px;"/>
    </div>
    <div class="left">使用其他账号登录</div>
    <div class="left">
        <hr style="width: 100px;margin-top:10px;"/>
    </div>
</div>
<%--margin: -6px -13px 0 0;--%>
<div style="clear:both; height: 50px;">
    <div id="fb_div" style="display:inline;width: 100px;height: 40px;">
        <fb:login-button
                scope="public_profile,email"
                onlogin="checkLoginState();">
        </fb:login-button>
    </div>

    <%--float: left;--%>
    <div id="googlePlus_div" style="display:inline;width: 100px;height: 40px;">
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

    <%--twitter--%>
    <div id="twitter_div" style="">

    </div>
</div>

<div style="clear:both; margin-top: 33px;">
    <%-- googlePlus --%>

</div>
