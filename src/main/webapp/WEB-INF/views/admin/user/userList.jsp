<!DOCTYPE html>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix='fmt' uri="http://java.sun.com/jsp/jstl/fmt" %> 
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<html>
	<head>
	    <title></title>
	    <meta charset="utf-8">
	    <meta http-equiv="X-UA-Compatible" content="IE=edge">
	    <script src="${ctx}/static/js/default.js" type="text/javascript"></script>
	    <script type="text/javascript">
	  		//激活菜单
			 $(document).ready(function() {
			      $("#users_tab").addClass("active");
			 });
	    	//删除用户
	    	function deleteUser(userId,hasId){
	    		var userIds = userId;
	    		if(hasId){
	    		}else {
	    			if(!confirmHasSelectedCheckbox()){
	    				return;
	    			}
	    			userIds = getSelectedIds();
	    		}
	    		if(confirm("删除用户将不可恢复，您确定执行此操作？")){
	    			//重定向查询用户列表时带上查询的参数
	    			location.href= '${ctx}/admin/deleteUser?userIds=' + userIds + "&" + getSearchParams();
	    		}
	    	}
	    	
	    	//冻结或恢复
	    	function changeStatus(actived,userId,hasId){
	    		$(".alert").css("display","none");
	    		var userIds = userId;
	    		if(hasId){
	    		}else {
	    			if(!confirmHasSelectedCheckbox()){
	    				return;
	    			}
	    			userIds = getSelectedIds();
	    		}
	    		var info = "冻结";
	    		if(actived=="true"){
	    			info= "恢复";
	    		}
    			if(confirm("您确定" + info + "所选用户吗？")){
    				//重定向查询用户列表时带上查询的参数
	    			location.href="${ctx}/admin/changeUserStatus?actived=" + actived + "&userIds=" + userIds + "&" + getSearchParams();
	    		}
	    	}
	    	
	    	//重置密码
	    	function resetPassword(userId,hasId){
	    		$(".alert").css("display","none");
	    		if(hasId){
	    			if(confirm("您确定重置所选用户的密码？")){
	    				//重定向查询用户列表时带上查询的参数
		    			location.href="${ctx}/admin/resetPassword?userIds=" + userId + "&" + getSearchParams();
		    		}
	    		}else {
	    			if(confirmHasSelectedCheckbox()){
	    				if(confirm("您确定重置所选用户的密码？")){
			    			var userIds = getSelectedIds();
			    			location.href="${ctx}/admin/resetPassword?userIds=" + userIds + "&" + getSearchParams();
	    				}
	    			}
	    		}
	    	}
	    	
	    	//得到搜索的条件参数
	    	function getSearchParams(){
	    		var params = "search_LIKE_username=" + $("#search_LIKE_username").val()
					+ "&search_LIKE_nickname=" + $("#search_LIKE_nickname").val() 
					+ "&find_GTE_registerDate=" + $("#find_GTE_registerDate").val() 
					+ "&find_LTE_registerDate=" + $("#find_LTE_registerDate").val() 
					+ "&search_LIKE_phone=" + $("#search_LIKE_phone").val() 
					+ "&search_LIKE_email=" + $("#search_LIKE_email").val() 
					+ "&pageSize=" + $("#pageSize").val() 
					+ "&page=" + $("#page").val() ;
	    		return params;
	    	}
	    	

	    	//检查是否选中了的checkbox,如果是返回true,如果否返回false并提示
	    	function confirmHasSelectedCheckbox(){
	    		var selectedSize = 0;
	    		$("[name='checkbox']:checked").each(function(){
	    			selectedSize ++;
	    		});
	    		if(selectedSize>0){
		    		return true;
	    		}else {
	    			$("#blockDiv").css("display","");
	    			return false;
	    		}
	    	}
		</script> 
	</head>
	<body>
		<div class="row">
			<div class="span12">
				<c:if test="${info=='success'}">
					<tags:blockDiv type="success" hide="false" info="操作成功！"/>
				</c:if>
				<c:if test="${info=='fail'}">
					<tags:blockDiv type="error" hide="false" info="操作失败，请稍后重试！"/>
				</c:if>
				<!-- 警告框 -->
				<tags:blockDiv type="block" hide="true" info="请选择用户！"/>
				<input id="page" value="${param.page }" type="hidden">
				<div class="navbar">
					<div class="navbar-inner">
						<form class="form-inline" action="${ctx}/admin/users" style="margin: 5px 0 5px 0;" autocomplete="on" method="post">
							<input type="text" id="search_LIKE_username" name="search_LIKE_username" placeholder="用户名" class="input-small" value="${param.search_LIKE_username}"> 
							<input type="text" id="search_LIKE_nickname" name="search_LIKE_nickname" placeholder="昵称" class="input-small" value="${param.search_LIKE_nickname}"> 
							<input type="text" id="search_LIKE_phone" name="search_LIKE_phone" placeholder="手机" class="input-small" value="${param.search_LIKE_phone}"> 
							<input type="text" id="search_LIKE_email" name="search_LIKE_email" placeholder="邮箱" class="input-small" value="${param.search_LIKE_email}"> 
							
							<label style="margin-left:10px;"> 注册时间：</label>
							
							<input type="text" id="find_GTE_registerDate"  readonly="readonly" style="width: 75px;" name="find_GTE_registerDate" value="${param.find_GTE_registerDate}"/>
							<div class="input-append date" >
							<span class="add-on" onclick="return showCalendar('find_GTE_registerDate', 'y-MM-dd 00:00:00');">
								<i class="icon-calendar" data-date-icon="icon-calendar" data-time-icon="icon-time"></i>
							</span>
							</div>
							
							<label>-</label>
							
							<input type="text" id="find_LTE_registerDate" readOnly="readonly" style="width: 75px;" name="find_LTE_registerDate"  value="${param.find_LTE_registerDate}"/>
							<div class="input-append date" >
								<span class="add-on" onclick="return showCalendar('find_LTE_registerDate', 'y-MM-dd 23:59:59');">
									<i class="icon-calendar" data-date-icon="icon-calendar" data-time-icon="icon-time"></i>
								</span>
							</div>
							<br/>
							<label style="margin-left:5px;">每页显示</label>
							<input type="text" style="width: 25px;" name="pageSize" id="pageSize" value="${pageSize}"/>
							<label style="margin-left:5px;">条</label>
							<button type="submit" class="btn btn-primary" id="search_btn">查 询</button>
					    </form>
					</div>
				</div>
		    </div>
<%-- 		    <tags:sort/> --%>
		</div>
		<div>
		    <table class="table table-striped table-hover" id="userTable">
				<thead>
					<tr>
						<th>
							<input id="controlAll" type="checkbox">
							<label style="display: inline;" for="controlAll">全选</label>
						</th>
<!-- 						<th>序号</th> -->
						<th>用户名</th>
						<th>昵称</th>
						<th>手机</th>
						<th>邮箱</th>
						<th>状态</th>
						<th>注册时间</th>
						<th>操作</th>
					<tr>
				</thead>
				<tbody>
		            <c:forEach items="${userList.content }" var="user" varStatus="index">
			            <tr>
			                <td>
			                	<input id="index${index.index}" name='checkbox' type="checkbox" value="${user.id }" style="margin-top: 0;">
			                	 &nbsp;
			                	 <label for="index${index.index}" style="display:inline ;">
				                	 ${index.index + 1}
			                	 </label>
			                </td>
<%-- 			                <td>${index.index + beginSize}</td> --%>
			                <td>
				            	<a href="profile?userId=${user.id }">
			                		${user.username }
			                	</a>
			                </td>
			                <td>
				            	<a href="profile?userId=${user.id }">
			                		${user.nickname }
			                	</a>
			                </td>
			                <td>
				            	<a href="profile?userId=${user.id }">
			                		<c:choose>
										<c:when test="${user.convertPhone=='' || user.convertPhone==null}">
											未填写
										</c:when>
										<c:otherwise>
					                		${user.convertPhone}
										</c:otherwise>
									</c:choose>
			                	</a>
			                </td>
			                <td>
				            	<a href="profile?userId=${user.id }">
					            	<c:choose>
										<c:when test="${user.convertEmail=='' || user.convertEmail==null}">
											未填写
										</c:when>
										<c:otherwise>
					                		${user.convertEmail}
										</c:otherwise>
									</c:choose>
			                	</a>
			                </td>
			                <td>
			                	<c:if test="${user.actived}">
			                		<span class="label label-success">正常</span>
			                	</c:if>
			                	<c:if test="${!user.actived}">
				                	<span class="label label-warning">已冻结</span>
			                	</c:if>
			                </td>
			                <td>
			                	<fmt:formatDate value="${user.registerDate }" pattern="yyyy-MM-dd"/>
			                </td>
			                <td width="24%">
 			                	<button class="btn btn-primary" type="button" onclick="resetPassword('${user.id }',true)">重置密码</button>
<%-- 			                	<button class="btn btn-primary" type="button" onclick="location.href='initUpdateProfile?userId=${user.id }'">修改</button> --%>
			                	<c:if test="${user.actived}">
				                	<button class="btn btn-warning" type="button" onclick="changeStatus('false','${user.id }',true)">冻结</button>
			                	</c:if>
			                	<c:if test="${!user.actived}">
				                	<button class="btn btn-success" type="button" onclick="changeStatus('true','${user.id }',true)">恢复</button>
			                	</c:if>
<%-- 			                	<button class="btn btn-danger" type="button" onclick="deleteUser('${user.id }',true)">删除</button> --%>
							</td>
			            </tr> 
		            </c:forEach>
		        </tbody>
		    </table>
		</div>
	    <div class="row">
		    <div class="span9">
			    <tags:pagination page="${userList}" paginationSize="5"/>
		    </div>
		    <div class="span3" style="text-align: right;margin-left: 10px;">
<!-- 			    <button class="btn btn-primary" type="button" onclick="resetPassword('',false)">重置密码</button> -->
			    <button class="btn btn-success" type="button" onclick="changeStatus('true','',false)">恢复</button>
			    <button class="btn btn-warning" type="button" onclick="changeStatus('false','',false)">冻结</button>
			    <button class="btn btn-danger" type="button" onclick="deleteUser('',false)">删除</button>
		    </div>
	    </div>
	</body>
</html>