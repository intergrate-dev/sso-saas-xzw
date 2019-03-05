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
	<title>个人资料</title>
		<script type="text/javascript">
			//激活菜单
			$(document).ready(function() {
			     $("#profile_tab").addClass("checked");
			     $("#menu_profile").addClass("checked");
			});
		</script>
		<style type="text/css">
		</style>
	</head>
	<body>
	
		<%@include file="common_profile.jsp" %>
		<div class="contend_div">
			<div class="infoDiv">
<%-- 				<tags:blockDiv type="success" hide="true" info="修改成功！"/> --%>
			</div>
			
			<div class="content_info">
				<table class="table table-bordered">
					<tr>
						<td style="text-align:right;width: 20%">
				     		用户名：
						</td>
						<td>
				     		${user.username} 
						</td>
					</tr>
					<tr>
						<td style="text-align:right;width: 20%">
				     		昵称：
						</td>
						<td>
				     		${user.nickname } 
						</td>
					</tr>
					
					<tr>
						<td style="text-align:right">
				     		邮箱：
						</td>
						<td>
							<c:choose>
								<c:when test="${user.email=='' || user.email==null}">
									未填写
								</c:when>
								<c:otherwise>
									${user.email}
								</c:otherwise>
							</c:choose>
						</td>
					</tr>
				</table>
				<c:if test="${user.provider == null }">
					<a href="initUpdateProfile">
						<button class="btn btn-primary" style="width: 200px;margin-left: 250px;" onclick="location.href='${ctx }/user/initUpdateProfile'"> 修 改 </button>
					</a>
				</c:if>
			</div>
		</div>
	</body>
</html>