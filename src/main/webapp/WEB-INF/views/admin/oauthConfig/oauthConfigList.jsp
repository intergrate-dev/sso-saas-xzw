<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<html>
	<head>
		<title>管理员列表</title>
		
		<script type="text/javascript">
			//激活菜单
			 $(document).ready(function() {
			      $("#oAuth_tab").addClass("active");
			 });
		</script>
	</head>
	<body>
		<div> 
		    <table class="table table-striped table-hover">
				<thead>
					<tr>
						<th>序号</th>
						<th>名称</th>
						<th>开发者Id</th>
						<th>开发者密钥</th>
						<th>说明信息</th>
						<th>状态</th>
						<th>操作</th>
					<tr>
				</thead>
				<tbody>
		            <c:forEach items="${oauthConfigList}" var="oauthConfig" varStatus="i">
			            <tr>
			                <td width="5%">${i.index+1}  </td>
			                <td>${ oauthConfig.providerName }  </td>
			                <td>${(oauthConfig.appId == null || oauthConfig.appId =='') ? "无": oauthConfig.appId}</td>
			                <td>${(oauthConfig.secretKey == null || oauthConfig.secretKey =='') ? "无": oauthConfig.secretKey}</td>
			                <td>${(oauthConfig.description == null || oauthConfig.description =='') ? "无": oauthConfig.description}</td>
			                <td>
			                	<c:if test="${oauthConfig.enabled}">
			                		<span class="label label-success">已启用</span>
			                	</c:if>
			                	<c:if test="${!oauthConfig.enabled}">
				                	<span class="label label-warning">未启用</span>
			                	</c:if>
			                </td>
			                <td width="8%">
			                	<a href="initUpdateOauthConfig?id=${oauthConfig.id }">
				                	<button class="btn btn-primary" type="button">修改</button>
			                	</a>
			                </td>
			            </tr>
		            </c:forEach>
		        </tbody>
		    </table>
		</div>
	</body>
</html>