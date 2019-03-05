<!DOCTYPE html>
<%@ page contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="sitemesh" uri="http://www.opensymphony.com/sitemesh/decorator" %>  
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<html>
	<head>
		<title><sitemesh:title default="用户信息管理平台"/></title>
		<%@ include file="commonHead.jsp" %>
		<sitemesh:head/>
		<style type="text/css">
			body {
				margin: 0;
				padding: 0;
				width: 100%;
				height: 100%;
 				background-image: url(${ctx}/static/images/login/login-background.jpg); 
				font: 14px/22px "宋体", "Arial Narrow", HELVETICA;
			}
		</style>
	</head>

	<body>
		<sitemesh:body/>
	</body>
</html>