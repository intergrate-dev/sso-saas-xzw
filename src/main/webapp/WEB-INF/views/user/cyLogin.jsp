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
	    <title>跳转页面</title>
	    <meta charset="utf-8">
	    <meta http-equiv="X-UA-Compatible" content="IE=edge">
   	</head>
	<body>
		<c:forEach items="${subsystemList}" var="subsystem" varStatus="i">
			<iframe src="${subsystem.logoutUrl}" style="display:none;width: 0px;height: 0px;" ></iframe>
		</c:forEach>
		正在登录，请稍等……
		
		<div id="block_hint" style="visibility : hidden">
			如果您看到这个页面，说明您的网速缓慢或者浏览器阻止了跳转。<br />
			请您点击<a href='${toPage}'><strong><font color=red>这里</font></strong></a>继续。
		</div>
		<script type="text/javascript">
			location.replace('${toPage}')
			setTimeout(function(){
				document.getElementById("block_hint").style.visibility = 'visible'; 
			},2000);
		</script>
	</body>
	

</html>