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
<title>注册成功</title>
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
$(function(){
	//每1S检测一次
	window.timerr=setInterval("run()",1000*1);
	//填入成功信息
	var successType = "${successType}";
	if(successType == "register"){ //注册
		$("#successMsg").text("恭喜您注册成功！");
	}else if(successType == "restPwd"){ //重置密码
		$("#successMsg").text("恭喜您密码修改成功！");
	}
	//点击确定按钮跳转页面
	var isAppType = "${isAppType}";
	if(isAppType == "app"){
		$("#ljdlA").attr('href','${ctx }/user/login_app');
	}else{
		$("#ljdlA").attr('href','${ctx }/user/login');
	}
});
 function run(){
	var s = document.getElementById("online_second");
	var sv = (s.innerHTML) * 1;
	sv = sv <= 0 ? 6 : sv;
	sv = sv * 1 - 1;
	s.innerHTML = sv;
	if( sv <= 0 ){
		//window.close();
		clearInterval(window.timerr);
		location.href = "${ctx }/user/login_app";
	}
} 
</script>
</head>
<body>
	<div class="container_s">
		<!--
		<div style="margin-bottom: 60px;">
			<img src="${ctx}/static/images/login/logo.png" style="margin: 20px 10px;">
		</div>
		-->
		<div style="margin-bottom: 1rem;padding-top:8rem;">
			<img src="${ctx}/static/images/connectUser/right.png" style="width:  2.5rem;height:  2.5rem;vertical-align:text-bottom;">
			<span id="successMsg" style="color:#666;font-size: 3rem;font-weight: bold;"></span>
		</div>
		<a id="ljdlA">
			<button id="ljdl" class="btn btn-warning" style="width: 12rem;height:2.5rem;margin-top: 1rem;font-size: 2rem;"> 立即登录 </button>
		</a>
		<div style="margin-bottom: 30px;margin-top:2rem;">
			<span style="color: #666; font-weight: bold; font-size: 1.5em;"><span class="red" id="online_second">3</span> 秒钟后自动跳转到登录页</span>
		</div>
    </div> 
</body>
</html>