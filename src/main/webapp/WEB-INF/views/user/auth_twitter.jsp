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
<title>授权登录成功</title>
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<link rel='stylesheet' href='${ctx}/static/bootstrap2_3_2/css/bootstrap.min.css'>
<script src='${ctx}/static/jquery1_11_1/jquery.js'></script>

<script src='${ctx}/static/bootstrap2_3_2/js/bootstrap.min.js'></script>
<style type="text/css">
body{
	background-image:none!important;
	background:#fff !important;
} 
.container_s{
	margin-top:100px;
	margin-left:20%;
	/* height: 80%; */
	width: 60%;
	text-align: center; 
} 
.red {
	color:red;
}
</style>
<script type="text/javascript">


</script>
</head>
<body>
	<div class="container_s">
		<!--
		<div style="margin-bottom: 60px;">
			<img src="${ctx}/static/images/login/logo.png" style="margin: 20px 10px;">
		</div>
		-->
		<div style="margin-bottom: 30px;">
			<img src="${ctx}/static/images/connectUser/right.png" style="width: 30px;height: 30px;vertical-align:text-bottom;">
			<span style="color:#666;font-size: 1.6em;font-weight: bold;">twitter授权登录成功，正在跳转中...</span>
		</div>
		<%-- 
		<a id="ljdlA">
			<button id="ljdl" class="btn btn-warning" style="width: 250px;margin-top: 40px;"> 立即登录 </button>
		</a>
		--%>
		<div style="margin-bottom: 30px;">
			<span style="color: #666; font-weight: bold; font-size: 1.2em;"><span class="red" id="online_second">4</span> 秒钟后此窗口将关闭</span>
		</div>
    </div> 
</body>
</html>