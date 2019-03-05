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
			      $("#admins_tab").addClass("active");
			 });
			//删除用户
			function deleteUser(userId){
				hideBlockDiv();
				if(confirm("删除用户将不可恢复，您确定执行此操作？")){
					//重定向查询用户列表时带上查询的参数
					location.href= "${ctx}/admin/account/" + userId + "/delete";
				}
			}
			//冻结或恢复用户
			function changeStatus(userId,action){
				hideBlockDiv();
				var info = "恢复";
	    		if(action=="disable"){
	    			info= "冻结";
	    		}
				if(confirm("您确定"+info+"此用户？")){
					//重定向查询用户列表时带上查询的参数
					location.href= "${ctx}/admin/account/" + userId + "/" + action;
				}
			}
			
		</script>
	</head>
	<body>
		
		<c:if test="${info=='success'}">
			<tags:blockDiv type="success" hide="false" info="操作成功！"/>
		</c:if>
		<c:if test="${info=='fail'}">
			<tags:blockDiv type="error" hide="false" info="操作失败，请稍后重试！"/>
		</c:if>
		<div> 
		    <table class="table table-striped table-hover">
		        <thead><tr><th>序号</th><th>用户名</th><th>真实姓名</th><th>邮箱</th><th>电话</th><th>状态</th><th>操作</th><tr></thead>
		        <tbody>
		            <c:forEach items="${ accountList }" var="account" varStatus="index">
			            <c:choose>
			            	<c:when test="${account.superAdmin}">
			            		<c:if test="${inlineAccount.superAdmin }">
						            <tr>
						                <td>${index.index + 1} </td>
						                <td>
						                	<a href="initUpdate?id=${ account.id }">${ account.loginname }</a></td>
						                <td>
						                	<a href="initUpdate?id=${ account.id }">${ account.realname }</a></td>
						                <td>
						                	<a href="initUpdate?id=${ account.id }">${ account.email }</a></td>
						                <td>
						                	<a href="initUpdate?id=${ account.id }">
						                		${ account.mobile }
						                	</a>	
						                </td>
						                <td>
						                	<c:if test="${account.enabled}">
						                		<span class="label label-success">正常</span>
						                	</c:if>
						                	<c:if test="${!account.enabled}">
							                	<span class="label label-warning">已冻结</span>
						                	</c:if>
						                </td>
						                <td width="15%">
<%-- 						                	<a href="initUpdate?id=${ account.id }"> --%>
<!-- 							                	<button class="btn btn-primary" type="button">修改</button> -->
<!-- 						                	</a> -->
<%-- 						                	<button class="btn btn-primary" type="button" onclick="resetPassword('${account.id}')">重置密码</button> --%>
						                </td>
						            </tr>
			            		</c:if>
			            	</c:when>
			            	<c:otherwise>
					            <tr>
					                <td>${index.index + 1}</td>
					                <td>
					                	<a href="initUpdate?id=${ account.id }">${ account.loginname }</a></td>
					                <td>
					                	<a href="initUpdate?id=${ account.id }">${ account.realname }</a></td>
					                <td>
					                	<a href="initUpdate?id=${ account.id }">${ account.email }</a></td>
					                <td>
					                	<a href="initUpdate?id=${ account.id }">
					                		${ account.mobile }
					                	</a>	
					                </td>
					                <td>
					                	<c:if test="${account.enabled}">
					                		<span class="label label-success">正常</span>
					                	</c:if>
					                	<c:if test="${!account.enabled}">
						                	<span class="label label-warning">已冻结</span>
					                	</c:if>
					                </td>
					                <td width="15%">
<%-- 					                	<a href="initUpdate?id=${ account.id }"> --%>
<!-- 						                	<button class="btn btn-primary" type="button">修改</button> -->
<!-- 					                	</a> -->
<%-- 					                	<button class="btn btn-primary" type="button" onclick="resetPassword('${account.id}')">重置密码</button> --%>
					                	<c:if test="${ account.id != inlineAccount.id}">
					                		<button class="btn ${ account.enabled?'btn-warning':'btn-success' }" onclick="changeStatus('${account.id}','${account.enabled?'disable':'enable'}')">
					                			${ account.enabled?"冻结":"激活" }
					                		</button>
					                		
						                	<button class="btn btn-danger" onclick="deleteUser('${account.id}')">删除</button>
					                	</c:if>
					                </td>
					            </tr>
			            		
			            	</c:otherwise>
			            </c:choose>
			         
		            </c:forEach>
		        </tbody>
		    </table>
		</div>
		<div class="offset10">
			<a href="initUpdate">
				<button class="btn btn-primary">创建管理员</button>
			</a>
		</div>
	</body>
</html>