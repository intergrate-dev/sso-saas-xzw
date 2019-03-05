<!DOCTYPE html>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix='fmt' uri="http://java.sun.com/jsp/jstl/fmt" %> 
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<% 
response.setHeader("Cache-Control","no-store"); 
response.setHeader("Pragrma","no-cache"); 
response.setDateHeader("Expires",0); 
%>
<html>
	<head>
	<title>我的头像</title>
		<script type="text/javascript">
			//激活菜单
			$(document).ready(function() {
				$("#profile_tab").addClass("checked");
				$("#menu_face").addClass("checked");
			});
		</script>
	</head>
	<body>
		
		<%@include file="common_profile.jsp" %>
		
		<div class="contend_div">
			<div class="infoDiv">
				<c:if test="${user.provider == null }">
					<div class="alert alert-error "style="text-align: center;padding-bottom: 20px;">
						如果要修改头像，请点击左侧的大图。
					</div>
				</c:if>
<%-- 				<tags:blockDiv type="success" hide="false" info="点击修改头像"/> --%>
			</div>
			
			<div class="content_info">
	    		<%@include file="../common/commonFace.jsp" %>
			</div>
		</div>
	</body>
</html>