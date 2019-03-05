<%@ page language="java" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<script>
	function showHead(){
		$("#profileHead").show();
		
	}
</script>
<div id="header">
	<div id="title">
	    <h1><a href="${ctx}">方正翔宇</a><small>--用户信息管理平台</small>
	    <shiro:user>
			<div class="btn-group pull-right">
				<a class="btn dropdown-toggle" data-toggle="dropdown" href="#" onclick="showHead()">
					<i class="icon-user"></i> <shiro:principal property="realname"/>
					<span class="caret"></span>
				</a>
			
				<ul class="dropdown-menu" id="profileHead">
<%-- 					<li><a href="${ctx}/admin/profile">个人资料</a></li> --%>
					<li><a href="${ctx}/admin/account/initUpdatePassword">修改密码</a></li>
					<li><a href="${ctx}/admin/logout">注销</a></li>
				</ul>
			</div>
		</shiro:user>
		</h1>
	</div>
</div>