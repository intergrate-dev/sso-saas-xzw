<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<% 
response.setHeader("Cache-Control","no-store"); 
response.setHeader("Pragrma","no-cache"); 
response.setDateHeader("Expires",0); 
%>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
	
		<div class="menu">
			<ul>
				<li id="menu_profile">
					<a href="${ctx }/user/profile">
				  		基本资料
					</a>
				</li>
				<li id="menu_face">
					<a href="${ctx }/user/face">我的头像</a>
				</li>
				<c:if test="${user.provider == null }">
					<li id="menu_password">
						<a href="${ctx }/user/password/initUpdatePassword">修改密码</a>
					</li>
				</c:if>
			</ul>
		</div>
