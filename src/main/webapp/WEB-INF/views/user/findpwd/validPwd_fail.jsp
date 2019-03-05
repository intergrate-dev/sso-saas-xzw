<!DOCTYPE html>
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
<html>
<head>
	<title>验证码错误</title>
	<script type="text/javascript" src="${ctx}/static/js/md5.js"></script>
	<style type="text/css">
		a:HOVER {
			text-decoration: none;
		}
		.table th, .table td {
			border-top: 0px;
			padding: 10px 0px;

		}
	</style>
</head>
<body>
	<div class="wrapper">
		<%@include file="common_findpwd.jsp" %>
		<div class="nav">
			<%--<h3>验证失败</h3>--%>
			<%--<img src="${ctx}/static/images/findpwd/${byType}_s2.png">--%>
		</div>
		<div class="center">
			<h3>${errorMsg}</h3>
			<a onclick="toFindPwd();"><h5>重新发送验证码</h5></a>
		</div>
	</div>
	<script>
		function toFindPwd() {
			var ctx = "${ctx}";
			location.href = "${ctx}/user/password/find?byType=${byType}";
		}
	</script>
</body>
</html>
