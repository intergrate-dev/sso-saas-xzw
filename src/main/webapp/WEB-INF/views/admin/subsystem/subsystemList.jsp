<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<html>
	<head>
		<title>子系统列表</title>
		
		<script type="text/javascript">
			//激活菜单
			 $(document).ready(function() {
			      $("#subsystem_tab").addClass("active");
			 });
			
			function deleteSubsystem(userId){
				if(confirm("删除子系统将不可恢复，您确定执行此操作？")){
					location.href= "${ctx}/admin/subsystem/" + userId + "/delete";
				}
			}
		</script>
		<style type="text/css">
			.table th, .table td {
				padding: 5px 8px;
				word-wrap:break-word;
				overflow:hidden; 
			}
		</style>
	</head>
	<body>
		<div> 
			<c:if test="${info=='success'}">
				<tags:blockDiv type="success" hide="false" info="操作成功！"/>
			</c:if>
			<c:if test="${info=='fail'}">
				<tags:blockDiv type="error" hide="false" info="操作失败，请稍后重试！"/>
			</c:if>
		    <table class="table table-striped table-hover"  style="word-break:break-all; word-wrap:break-all;">
				<thead>
					<tr>
						<th style="width: 7%;">代码</th>
						<th style="width: 8%;">名称</th>
						<th>系统入口</th>
						<th>同步退出地址</th>
						<th>重定向地址</th>
<!-- 						<th>说明信息</th> -->
						<th style="width: 5%;">状态</th>
						<th style="width: 7%;">加密</th>
						<th style="width: 8%;">操作</th>
					<tr>
				</thead>
				<tbody>
		            <c:forEach items="${subsystemList}" var="subsystem" varStatus="i">
			            <tr title="${subsystem.description }">
			                <td>
			                	<a href="${ctx }/admin/subsystem/initUpdate?id=${subsystem.id }">
			                		${subsystem.code }  
			                	</a>		
			                </td>
			                <td>
			                	<a href="${ctx }/admin/subsystem/initUpdate?id=${subsystem.id }">
			                		${ subsystem.name }  
			                	</a>		
			                </td>
			                <td>
			                	<a href="${ctx }/admin/subsystem/initUpdate?id=${subsystem.id }">
			                		${(subsystem.homePage == null || subsystem.homePage =='') ? "无": subsystem.homePage}
			                	</a>		
			                </td>
			                <td>
			                	<a href="${ctx }/admin/subsystem/initUpdate?id=${subsystem.id }">
			                		${(subsystem.logoutUrl == null || subsystem.logoutUrl =='') ? "无": subsystem.logoutUrl}
			                	</a>		
			                </td>
			                <td>
			                	<a href="${ctx }/admin/subsystem/initUpdate?id=${subsystem.id }">
			                		${(subsystem.redirectUrl == null || subsystem.redirectUrl =='') ? "无": subsystem.redirectUrl}
			                	</a>		
			                </td>
<%-- 			                <td>${(subsystem.description == null || subsystem.description =='') ? "无": subsystem.description}</td> --%>
			                <td>
			                	<c:if test="${subsystem.enabled}">
			                		<span class="label label-success">已启用</span>
			                	</c:if>
			                	<c:if test="${!subsystem.enabled}">
				                	<span class="label label-warning">未启用</span>
			                	</c:if>
			                </td>
			                <td>
			                	${subsystem.encryptType}
			                </td>
			                <td>
<%-- 			                	<a href="${ctx }/admin/subsystem/initUpdate?id=${subsystem.id }"> --%>
<!-- 				                	<button class="btn btn-primary" type="button">修改</button> -->
<!-- 			                	</a> -->
				                <button class="btn btn-primary" type="button" onclick="deleteSubsystem('${subsystem.id}')">删除</button>
			                </td>
			            </tr>
		            </c:forEach>
		        </tbody>
		    </table>
		</div>
		<div class="offset10">
			<a href="${ctx }/admin/subsystem/initUpdate">
				<button class="btn btn-primary">添加子系统</button>
			</a>
		</div>
	</body>
</html>