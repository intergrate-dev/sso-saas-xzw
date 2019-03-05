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
<title>绑定第三方账户</title>
</head>
<body>
		<div class="menu">
			<ul>
				<li class="checked">
					<a id="no_decoration" href="#">
				  		合作网站
					</a>
				</li>
			</ul>
		</div>
		<div class="contend_div">
			<div class="infoDiv">
				<c:if test="${beBounded }">
					<tags:blockDiv type="error" hide="false" info="对不起，${nickname }已经被绑定，请选择其他账号。"/>
				</c:if>
			</div>
			<div class="content_info">
			    <form id="bindThirdAccount" action="${ctx}/user/BindThirdAccount" method="post">
			    	<input id="subsystemid" name="subsystemid" class="subsystemid" type="hidden" value="${systemid}">
			    	<input id="userid" name="userid" class="userid" type="hidden" value="${userid}">
					<table class="table table-bordered ">
						<tr>
							<td style="text-align:right;width: 20%">
					     		用户名：
							</td>
							<td>
					     		<input name="username" value="" type="text" class="username" required="required" maxlength="12" placeholder='请输入用户名'/>
							</td>
						</tr>
						<tr>
							<td style="text-align:right;width: 20%">
					     		密码：
							</td>
							<td>
					     		<input name="pwd" value="" type="password" class="pwd" required="required" maxlength="12" placeholder='请输入密码'/>  
							</td>
						</tr>
					</table>
					<button class="btn btn-primary " style="width: 100px;margin-left: 200px;" type="submit"> 绑定 </button>
					<input type="button" class="btn btn-warning" style="width: 100px;margin-left: 50px;" value="返 回 " onclick="location.href='${ctx}/user/myHome'"/> 
				 </form>
			</div>
		</div>
</body>
</html>