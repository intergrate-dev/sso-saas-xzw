<%@ page contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<%@ page import="com.founder.sso.admin.service.account.AdminDbRealm" %>
<%@ page import="org.apache.shiro.SecurityUtils" %>
<script>
	
	function hideDiv(divId){
		$("#" + divId).hide();
	}
</script>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<div class="navbar">
	<div class="navbar-inner">
		<ul class="nav" id="menuBar">
<%-- 			<shiro:hasAnyRoles name="super_admin,system_state_admin"> --%>
<%-- 				<li id="dashboard_tab"><a href="${ctx}/admin/dashboard">系统状态</a></li> --%>
<%-- 			</shiro:hasAnyRoles> --%>
			<li id="homepage_tab"><a href="${ctx}/admin/homepage">首页</a></li>
			<shiro:hasAnyRoles name="super_admin,user_admin">
				<li id="users_tab"><a href="${ctx}/admin/users">用户管理</a></li>
			</shiro:hasAnyRoles>
			<shiro:hasAnyRoles name="super_admin,account_admin">
				<li id="admins_tab"><a href="${ctx}/admin/account/">管理员设置</a></li>
			</shiro:hasAnyRoles>
<%-- 			<shiro:hasAnyRoles name="super_admin,config_admin"> --%>
<%-- 				<li id="config_tab" class=""><a href="${ctx}/admin/config">系统设置</a></li> --%>
<%-- 			</shiro:hasAnyRoles> --%>
			<shiro:hasAnyRoles name="super_admin,child_config_admin">
				<li id="subsystem_tab" class=""><a href="${ctx}/admin/subsystem">子系统管理</a></li>
			</shiro:hasAnyRoles>
			<shiro:hasAnyRoles name="super_admin,child_config_admin">
				<li id="app_tab" class=""><a href="${ctx}/admin/app">app子系统管理</a></li>
			</shiro:hasAnyRoles>
<%-- 			<shiro:hasAnyRoles name="super_admin,api_admin"> --%>
<%-- 				<li id="api_tab" class=""><a href="${ctx}/admin/api">API管理</a></li> --%>
<%-- 			</shiro:hasAnyRoles> --%>
<%-- 			<shiro:hasAnyRoles name="super_admin,log_admin"> --%>
<%-- 				<li id="logs_tab" class=""><a href="${ctx}/admin/logs">系统日志</a></li> --%>
<%-- 			</shiro:hasAnyRoles> --%>
			<shiro:hasAnyRoles name="super_admin,oauth_config_admin">
				<li id="oAuth_tab" class=""><a href="${ctx}/admin/findAllOauthConfig">第三方登录</a></li>
			</shiro:hasAnyRoles>
			<shiro:hasAnyRoles name="super_admin,system_config_admin">
				<li id="system_tab" class=""><a href="${ctx}/admin/findAllSystemConfig">系统配置</a></li>
			</shiro:hasAnyRoles>
<%-- 			<shiro:hasAnyRoles name="super_admin,email_admin"> --%>
<%-- 				<li id="email_tab" class=""><a href="${ctx}/admin/initSendEmail">邮件管理</a></li> --%>
<%-- 			</shiro:hasAnyRoles> --%>
		</ul>
	</div>
</div>