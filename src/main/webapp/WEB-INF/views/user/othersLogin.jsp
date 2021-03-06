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
        <hr style="width: 100px;margin-top:10px;border-color:#464646;"/>
    </div>
    <div class="left" style="color:#464646">使用其他账号登录</div>
    <div class="left">
        <hr style="width: 100px;margin-top:10px;border-color:#464646;"/>
    </div>
</div>

<style>
	#fb_div
	{
		height:50px;
	}
</style>

<%--margin: -6px -13px 0 0;--%>
<div style="clear:both; height: 50px;">
    <%--<div id="fb_div" style="display:inline;width: 100px;height: 40px;">
        <fb:login-button
                scope="public_profile,email"
                onlogin="facebook_login();">
        </fb:login-button>
    </div>--%>

    <button class="facebook-login" style="margin-right: 11px;">
        <img src="${ctx }/static/images/bindAccount/facebook.jpg" class="img-rounded" style="width: 23px;" onclick="facebook_login()">
    </button>

    <button class="googlePlus-login" style="margin-right: 11px;">
        <img src="${ctx }/static/images/bindAccount/googlePlus.jpg" class="img-rounded" style="width: 23px;" onclick="googlePlus_login()">
    </button>

    <button class="twitter-share-button" onclick = "twitter_login('twitter')" style="margin-right: 10px;">
        <img src="${ctx }/static/images/bindAccount/twitter.jpg" class="img-rounded" style="width: 23px;">
    </button>

    <button class="weixin-login" style="margin-right: 25px;">
        <img src="${ctx }/static/images/bindAccount/wechat.jpg" class="img-rounded" style="width: 23px;" onclick="wechat_login()">
    </button>

    <%--float: left;--%>
    <%--<div id="googlePlus_div" style="display:inline;width: 100px;height: 40px;">
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
    </div>--%>

    <%--twitter--%>
        <%--style="width: 30px; height: 30px;"--%>
    <%--<div id="twitter_div">
        <button class="twitter-share-button" onclick = "twitter_login('twitter')" style="margin-right: 10px;">
            <img src="${ctx }/static/images/bindAccount/twitter.jpg" class="img-rounded" style="width: 23px;">
        </button>
        <button class="weixin-login" style="margin-right: 25px;">
            <img src="${ctx }/static/images/bindAccount/wechat.jpg" class="img-rounded" style="width: 23px;" onclick="wechat_login()">
        </button>--%>
    </div>

</div>

<div style="clear:both; margin-top: 33px;">
    <%-- googlePlus --%>

</div>
