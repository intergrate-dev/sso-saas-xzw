<!DOCTYPE html>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="org.apache.shiro.web.filter.authc.FormAuthenticationFilter"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<% 
response.setHeader("Cache-Control","no-store"); 
response.setHeader("Pragrma","no-cache"); 
response.setDateHeader("Expires",0); 
%>
<html>
<head>
	<meta charset="UTF-8">
    <meta name="keywords" content=""/>
    <meta name="description" content=""/>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <meta name="viewport" content="initial-scale=1.0,user-scalable=1,maximum-scale=1,width=device-width"/>
    <meta name="apple-mobile-web-app-capable" content="yes"/>
    <meta name="apple-mobile-web-app-status-bar-style" content="black"/>
    <title>用户登录</title>
    
    <link rel="stylesheet" type="text/css" href="${ctx}/static/app/css/basic.css" />
    <script type="text/javascript" src="${ctx}/static/app/js/jquery-2.2.4.min.js"></script>
    <script type="text/javascript" src="${ctx}/static/app/js/validate.js"></script>
    <script type="text/javascript" src="${ctx}/static/js/md5.js"></script>
</head>
<body>
<div class="wrap">
    <div class="regiTitle">
        <a href="javascript:;" onclick="javascript:history.go(-1);"><span class="regBackBtn" >&lt;</span></a>
        立即登录
    </div>
    <div class="regiCont">
        <form id="loginForm" method="post" action="${ctx }/user/login_app">
            <div class="regInput">
                <input id="username" name="username" class="required" type="text" placeholder="请输入邮箱">
                <div class="inputIcon"><img src="${ctx}/static/app/img/43.png"></div>
                <div class="erroMsg"></div>
            </div>
            <div class="regInput">
            	<input type="password" id="password_hid" name="password_hid" placeholder="请输入密码（不少于6位）" class="login-input1">
				<input type="hidden" id="password" name="password" />
				<div class="erroMsg"></div>
				<c:if test="${checkcode == 1}">
				<div class="getVerifi"><a href="${ctx }/user/forgetPwd_app" class="forget-pass">忘记密码</a></div>
				</c:if>
            </div>
            <c:if test="${username!=null}">
				<div id="loginInfo" style="margin:auto;font-size:1.155rem;color: #ff0000;">用户名或密码错误，请重新输入</div>
			</c:if>
            <div class="regInput certainBtn">
                <input id="dl" type="button" value="确定">
            </div>
			<input type="hidden" id="isSsoLogin" name="isSsoLogin" value="${sessionScope.isSsoLogin}" />
        </form>
    </div>
    <div class="PrompRegis">
        	没有账号?去<a href="${ctx }/user/register_app?siteId=${sessionScope.siteId}">注册</a>
    </div>
	<%@include file="othersLogin_app.jsp" %>
</div>
<script type="text/javascript">
$(function(){
	$("#dl").click(function(){
		submitForm(); //提交表单
	});
	$("#loginInfo").click(function(){
		$("#loginInfo").text("");
	});
});
//提交订单之前需要调用的方法
function submitForm(){
	if($("#username").val()==''||$("#username").val()==null){
	   	$("#username").parent().find('.erroMsg').html('<img src="../static/app/img/42.png" >账号不能为空!');
	   	return false;
	}else if($("#password_hid").val()==''||$("#password_hid").val()==null){
	   	$("#password_hid").parent().find('.erroMsg').html('<img src="../static/app/img/42.png" >密码不能为空!');
	   	return false;
	}else{
		var password = hex_md5($("#password_hid").val());  //对密码进行加密
		var len = $("#password_hid").val().length;
		$("#password_hid").val(password.substring(0,len)); //避免传输明文和字符串加长，将password_hid中的密码进行加密后截取
		$("#password").val(password);
		$("#loginForm").submit();
	}
}
</script>
</body>
</html>