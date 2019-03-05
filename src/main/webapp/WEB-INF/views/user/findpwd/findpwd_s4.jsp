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
	    <title>找回密码</title>
	</head>
	<body>
		<div class="wrapper">
			<%@include file="common_findpwd.jsp" %>
			<div class="nav">

            <a class="checked">通过邮箱找回密码</a>
			</div>
			<div class="center">
				<img src="${ctx}/static/images/findpwd/${byType}_s4.png">
			</div>
			<div class="infoDiv">
				<div>
					<img src="${ctx}/static/images/connectUser/right.png" style="width: 30px;height: 30px;vertical-align:text-bottom;">
					<span style="color:white;font-weight: bold;font-size: 1.2em;"> 修改密码成功！</span>
				</div>
			</div>
			<div class="buttons top50" >
				<%--<input type="button" value="关闭窗口" onclick="window.close()" class="btn btn-warning" style="width: 150px">--%>
				<input type="button" value="立即登录" onclick="location.href='${ctx }/user/login'" class="btn btn-warning" style="width: 150px">
			</div>
		</div>
	</body>
</html>