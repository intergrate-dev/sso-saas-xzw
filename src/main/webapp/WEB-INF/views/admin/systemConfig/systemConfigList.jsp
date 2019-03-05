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
			      $("#system_tab").addClass("active");
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
						<th>编码</th>
						<th>状态</th>
						<th>描述</th>
						<th>操作</th>
					<tr>
				</thead>
				<tbody>
		            <c:forEach items="${systemConfigList}" var="systemConfig" varStatus="i">
			            <tr>
			                <td width="5%">${i.index+1}  </td>
			                <td>${systemConfig.sname}  </td>
			                <td>${systemConfig.scode}</td>
			                <td>${systemConfig.sstatus}</td>
			                <td>${systemConfig.sdescribe}</td>
			                <td width="8%">
			                	<a href="initUpdateSystemConfig?id=${systemConfig.id }">
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