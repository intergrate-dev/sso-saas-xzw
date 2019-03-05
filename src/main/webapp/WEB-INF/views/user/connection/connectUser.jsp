<!DOCTYPE html>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="org.apache.shiro.web.filter.authc.FormAuthenticationFilter"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<%@ page import="com.founder.sso.entity.LoginInfo"%>
<%@ page import="org.apache.shiro.SecurityUtils"%>
<%@ page import="com.founder.sso.entity.User"%>
<%@ page import="com.founder.sso.util.SystemConfigHolder"%>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<% 
response.setHeader("Cache-Control","no-store"); 
response.setHeader("Pragrma","no-cache"); 
response.setDateHeader("Expires",0); 
%>
<html>
	<head>
	    <title>关联账号</title>
		<style type="text/css">
			.container_s{
				margin-top:100px;
				margin-left:20%;
				height: 80%;
				width: 60%;
				text-align: center; 
			} 
			
			
			.table th, .table td{
				vertical-align:middle;
				border-top:0;
				font-size: 1.1em;
				font-weight: bold;
				
			}
			
			input.text{
				width: 250px;
				height: 25px;
				margin-top:3px; 
	
			}
			
			.content{
				text-align: center; 
				margin-left: auto;
				margin-right: auto;
				margin-top: 20px;
			}
			
			.thumbnail{
				background-image: url('${ctx}/static/images/connectUser/div-bg.png');
				border:0px;
				margin-left: 10px;
			}
			.table img{
				width: 80px;
				height: 80px;
			}
			
			.table{
				margin-bottom: 0;
				line-height:10px;
			}
	    </style>    
	</head>
	<body>
		<c:forEach items="${subsystemList}" var="subsystem" varStatus="i">
			<iframe src="${subsystem.logoutUrl}" style="display:none;width: 0px;height: 0px;" ></iframe>
		</c:forEach>
		<div class="container_s">
			<div style="margin-bottom: 60px;">
				<img src="${ctx}/static/images/login/logo.png" style="margin: 20px 10px;">
			</div>
			<div style="margin-bottom: 30px;">
				<img src="${ctx}/static/images/connectUser/right.png" style="width: 30px;height: 30px;vertical-align:text-bottom;">
				<span style="color:white;font-size: 2.2em;font-weight: bold;">绑定成功！</span>
			</div>
			<div class="content">
				<%--<form action="${ctx }<%=SystemConfigHolder.getConfig("myHome")%>">--%>
				<form action="${ctx }/user/myHome">
					<%--<input type="hidden" name="targetUserId" value="${user.id}">--%>
					<ul class="thumbnails" style="margin-left: 50px;">
						<li class="span4 thumbnail">
							<div class="caption" >
						   		<table class="table">
						   			<tr>
						   				<td style="width: 30%">
								   			<img src='${previousAvatar}'class="img-polaroid">
						   				</td>
						   				<td>
											昵称：${previousNickname}
											<br>
											<br>
											<font style="font-weight: normal;"> 
												来自${comFrom }
											</font>
						   				
						   				</td>
						   			</tr>
						   		</table>
						   	</div>
						</li>
						<li class="span1" style="margin-top:30px;">
							<img src="${ctx}/static/images/connectUser/lock.png" >
						</li>
						<li class="span4 thumbnail">
					   		<div class="caption" >
						   		<table class="table">
						   			<tr>
						   				<td style="width: 30%">
								   			<img src='${targetUser.avatarSmall}'class="img-polaroid">
						   				</td>
						   				<td>
											用户名：${targetUser.username}
											<br>
											<br>
											<font style="font-weight: normal;"> 
												本地用户
											</font>
						   				
						   				</td>
						   			</tr>
						   		</table>
					   		</div>
						</li>
					</ul>
					<button class="btn btn-warning" style="width: 250px;margin-top: 40px;" type="submit"> 前往我的首页 </button>
				</form>
			</div>
		</div>
	</body>
</html>