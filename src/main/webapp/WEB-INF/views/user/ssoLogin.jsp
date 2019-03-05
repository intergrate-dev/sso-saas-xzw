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
	    <script type="text/javascript" src="${ctx}/static/js/loading.js"></script>
   	</head>
	<body>
		<c:forEach items="${reUrlList}" var="subsystem" varStatus="i">
			<iframe src="${subsystem}" style="display:none;width: 0px;height: 0px;" ></iframe>
		</c:forEach>
		
		<div id="block_hint" style="visibility : hidden">
			如果您看到这个页面，说明您的网速缓慢或者浏览器阻止了跳转。<br />
			请您点击<a href='${toPage}'><strong><font color=red>这里</font></strong></a>继续。
		</div>
		<script type="text/javascript">
		loadingHtml("正在登录，请稍等...","${ctx}");
		//alert("login");
		$(window).load(function(){
			loadingHtml("正在登录，请稍等...","${ctx}");
			setTimeout(function(){
				var isAppType = "${isAppType}";
				var anyUrl="${anyUrl}";//到任意网址去的参数
				if(anyUrl!=null && anyUrl!= undefined && anyUrl!=""){
					if(isAppType=="app"){
						location.replace(anyUrl);
					}else{
						if(window.opener){
							window.opener.location.href=anyUrl;
							window.close();
						}else{
							location.replace(anyUrl);
						}
					}
				}else{
					location.replace('${toPage}');
				}
			},500);   //延迟跳转页面
			setTimeout(function(){
				document.getElementById("block_hint").style.visibility = 'visible'; 
			},20000);
		});
		</script>
	</body>
</html>