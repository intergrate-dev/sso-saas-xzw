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
	    <title>登录成功</title>
	    <meta charset="utf-8">
	    <meta http-equiv="X-UA-Compatible" content="IE=edge">

		<script src='${ctx}/static/jquery1_11_1/jquery.js'></script>
		<script src='${ctx}/static/bootstrap2_3_2/js/bootstrap.min.js'></script>
   	</head>
	<body>
	<script type="text/javascript">
        $(function(){
            var topage = "${toPage}";
			location.href = topage;
        });
	</script>
	</body>
	
</html>