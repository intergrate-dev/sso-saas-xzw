<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="org.apache.shiro.web.filter.authc.FormAuthenticationFilter"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<% 
response.setHeader("Cache-Control","no-store"); 
response.setHeader("Pragrma","no-cache"); 
response.setDateHeader("Expires",0); 
%>
<!DOCTYPE html>
<html>
<head>
    <title>找回密码</title>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <link rel='stylesheet' href='${ctx}/static/bootstrap2_3_2/css/bootstrap.min.css'>
    <link type="image/x-icon" href="${ctx}/static/images/favicon.ico" rel="shortcut icon">
	<link href="${ctx}/static/bootstrap2_3_2/css/bootstrap.min.css" type="text/css" rel="stylesheet" />
	<link href="${ctx}/static/bootstrap-datetimepicker-0.0.11/css/bootstrap-datetimepicker.min.css" type="text/css" rel="stylesheet" />
	<link href="${ctx}/static/jquery-validation1_11_1/validate.css" type="text/css" rel="stylesheet" />
	<link href="${ctx}/static/styles/default.css" type="text/css" rel="stylesheet" />
	
	<script src="${ctx}/static/jquery1_11_1/jquery.min.js" type="text/javascript"></script>
	<script src="${ctx}/static/jquery-validation1_11_1/jquery.validate.min.js" type="text/javascript"></script>
	<script src="${ctx}/static/jquery-validation1_11_1/messages_bs_zh.js" type="text/javascript"></script>
	<script src="${ctx}/static/bootstrap-datetimepicker-0.0.11/js/bootstrap-datetimepicker.min.js" type="text/javascript"></script>
	<script src="${ctx}/static/js/default.js" type="text/javascript"></script>
    <script src='${ctx}/static/bootstrap2_3_2/js/bootstrap.min.js'></script>
	 
</head>
<body>
	<div class='container' style="margin-top: 100px;">
		<ul class="nav nav-tabs">
			<li id="li_email">
			  <a onclick="changeType('email','phone')">通过邮箱找回密码</a>
			</li>
			<li id="li_miBao">
			  <a onclick="changeType('email','phone')">通过密保找回密码</a>
			</li>
		</ul>
    </div>
</body>
</html>