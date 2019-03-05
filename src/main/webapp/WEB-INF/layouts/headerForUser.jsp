<%@page import="com.founder.sso.entity.LoginInfo"%>
<%@page import="org.apache.shiro.SecurityUtils"%>
<%@page import="com.founder.sso.entity.User"%>
<%@ page language="java" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<div id="header">
	<div id="title">
	    <h1><a href="${ctx}">方正翔宇</a><small>--我的信息平台</small>
	    <shiro:user>
			<div class="btn-group pull-right">
				<%User u = ((LoginInfo)SecurityUtils.getSubject().getPrincipal()).getUser(); %>
				<img width="50px;" height="50px;" src='${ctx}<%=u.getAvatarSmall() %>' style="margin-right: 5px;" class="img-polaroid">
				<a class="btn dropdown-toggle" data-toggle="dropdown" href="#">
					<i class="icon-user"></i> 
					<shiro:principal property="nickname"/>
					<span class="caret"></span>
				</a>
			
				<ul class="dropdown-menu">
					<li><a href="${ctx}/user/profile">个人资料</a></li>
					<li><a href="${ctx}/user/password/initUpdatePassword">修改密码</a></li>
					<li><a href="${ctx}/user/toSynchroLogout">注销</a></li>
				</ul>
			</div>
		</shiro:user>
		</h1>
	</div>
</div>