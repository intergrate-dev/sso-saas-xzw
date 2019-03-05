<%@ page contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<script>
	
	function hideDiv(divId){
		$("#" + divId).hide();
	}
</script>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<div class="navbar">
	<div class="navbar-inner">
		<ul class="nav">
			<li id="myHome_tab"><a href="${ctx}/user/myHome">个人主页</a></li>
<%-- 			<li id="systems_tab"><a href="${ctx}/user/systems">我的网站</a></li> --%>
<%-- 			<li id="growth_tab"><a href="${ctx}/user/growth">今日成长</a></li> --%>
			<li id="profile_tab"><a href="${ctx}/user/profile">个人资料</a></li>
		</ul>
	</div>
</div>