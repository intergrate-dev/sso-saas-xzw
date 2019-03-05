<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="com.founder.sso.admin.entity.Roles"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html>
	<head>
	    <title></title>
	    <meta charset="utf-8">
	    <meta http-equiv="X-UA-Compatible" content="IE=edge">
	    <script type="text/javascript">
		    $(document).ready(function(){
				//激活菜单
			    $("#admins_tab").addClass("active");
		    	jQuery.validator.addMethod("isMobile", function(value, element) {
		    		  var length = value.length;
		    		  var mobile = /^(((13[0-9]{1})|(15[0-9]{1}))+\d{8})$/;
		    		  var phone = /(^(\d{3,4}-)?\d{6,8}$)|(^(\d{3,4}-)?\d{6,8}(-\d{1,5})?$)|(\d{11})/;
		    		  return (this.optional(element) || (length == 11 && mobile.test(value))) || (this.optional(element) || (phone.test(value)));
		    	}, "请正确填写电话号码");
		    	
	            $("#updateAccountForm").validate({ 
	            	//在失去焦点时验证
		         	onfocusout: function(element){
		                $(element).valid();
		            },

	            	submitHandler: function(form) {  //通过之后回调 
	            		checkParams();
	            	 }, 
	            	 invalidHandler: function(form, validator) {  //不通过回调 
	            	       return false; 
	            	 } 
	            });
	    	});
	    	
	    	//检查参数是否合法
	    	function checkParams(){
	    		if($("input[name='roles']:checked").length<1){
	    			$("#blockInfoSpan").html("请给管理员赋予角色！");
	    			$("#blockDiv").show("slow");
	    			return false;
	    		} 
	    		//在新建账号时，检查登录名是否重复
	    		if($("#actionType").val()==0){
	    			$.ajax({ 
	    				async:false,
		    			type: "post", 
		    			url: "${ctx}/admin/account/isExistLoginName", 
		    			data: "loginname=" + $('#loginName').val(),//提交表单，相当于CheckCorpID.ashx?ID=XXX 
		    			success: function(msg){
		    				var json = eval('(' + msg + ')');
							if(json.result=="yes"){
								hideBlockDiv();
						 		$("#blockDiv").show("slow");
						 		$("#blockInfoSpan").html("用户名已经存在,请重新输入！");

							}else {
								hideBlockDiv();
								submitForm();
							}
						},
					 	error: function() {
					 		hideBlockDiv();
					 		$("#errorDiv").show("slow");
					    }
		    		});
	    		//修改管理员时，不需要检查登录名是否重复，直接提交
	    		}else {
	    			
	    			submitForm();
	    		}
	    	}
	    	
	    	function submitForm(){
	    		$.ajax({ 
	    			async:false,
	    			type: "post", 
	    			url: "${ctx}/admin/account/updateAccount", 
	    			data: $('#updateAccountForm').serialize(),//提交表单，相当于CheckCorpID.ashx?ID=XXX 
	    			success: function(msg){
	    				hideBlockDiv();
	    				$(".btn").prop("disabled","disabled"); 
						$("#successDiv").show("slow");
					},
				 	error: function() {
				 		hideBlockDiv();
				 		$("#errorDiv").show("slow");
				    }
	    		});
	    	}
	    	
	    	//重置密码
			function resetPassword(userId){
				if(confirm("您确定重置此用户的密码？")){
					//重定向查询用户列表时带上查询的参数
					location.href= "${ctx}/admin/account/" + userId + "/resetPassword";
				}
				hideBlockDiv();
			}
	    </script>
	</head>
	<body>
		<c:if test="${info=='resetPasswordSuccess'}">
			<tags:blockDiv type="success" hide="false" info="操作成功，密码被重置为123456！"/>
		</c:if>
		<tags:blockDiv type="success" hide="true" info="操作成功！"/>
		<tags:blockDiv type="error" hide="true" info="操作失败，请稍后重试！"/>
		<tags:blockDiv type="block" hide="true" info="用户名已经存在,请重新输入！"/>
		<ul class="breadcrumb">
		  	<li><a href="${ctx }/admin/account/">管理员设置</a> <span class="divider">/</span></li>
		  	<li class="active"><a href="#">${account==null?'创建管理员账号':'修改管理员账号' }</a></li>
		</ul>
		<input id="actionType" type="hidden" value="${account==null?0:1 }"/>
		<form id="updateAccountForm" name="updateAccountForm" class='form-signin' role='form' method="post" >
			<input type="hidden" id="accountId" name="id" value="${account.id}">  
			<table class="table table-bordered table-condensed">
				<tr>
					<td style="text-align:right;" >
			     		用户名：
					</td>
					<td>
						<c:if test="${account==null}">
				     		<input type="text" id="loginName" name="loginname" maxlength="10" placeholder='请输入1-10个字符的登录名' class="required" required autofocus>  
						</c:if>
						<c:if test="${account!=null}">
							${account.loginname}
						</c:if>
					</td>
				</tr>
				<tr>
					<td style="text-align:right;width: 20%">
			     		真实姓名：
					</td>
					<td>
						<!-- 超级管理员修改资料时，只能修改、邮箱、密码 -->
						<c:if test="${account.superAdmin}">
							${account.realname}
						</c:if>
						<c:if test="${!account.superAdmin}">
				     		<input type="text" name="realname" value="${account.realname}" maxlength="10" placeholder='请输入1-10个字符的真实姓名' required>  
						</c:if>
					</td>
				</tr>
				<tr>
					<td style="text-align:right">
			     		邮箱：
					</td>
					<td>
			     		<input type="text" name="email" class="email" value="${account.email}" placeholder='请输入合法的邮箱' required>  
					</td>
				</tr>
				<tr>
					<td style="text-align:right">
			     		角色：
					</td>
					<td>
						<!-- 1.修改时 -->
						<c:if test="${account!=null}">
							<!-- 1.1修改超级管理员时 -->
							<c:if test="${account.superAdmin}">
								<c:forEach items="<%=Roles.values() %>" var="role">
<%-- 									<c:if test="${role.value!='super_admin' }"> --%>
										<label class="checkbox">
											<input type="checkbox" name="roles" value="${role.value }" checked="checked" disabled="disabled"/>
											${role.name }
									    </label>
<%-- 									</c:if> --%>
								</c:forEach>
						    </c:if>
						    <!-- 1.2修改普通管理员时 -->
							<c:if test="${!account.superAdmin}">
							    <c:forEach items="<%=Roles.values() %>" var="role">
									<c:if test="${role.value!='super_admin' }">
										<label class="checkbox">
											<input type="checkbox" name="roles" value="${role.value }" <c:if test="${fn:contains(account.roles, role.value)}">checked="checked"</c:if> />
											${role.name }
									    </label>
									</c:if>
								</c:forEach>
						    </c:if>
						</c:if>
						
						<!-- 2.创建管理员时 -->
						<c:if test="${account==null}">
							<c:forEach items="<%=Roles.values() %>" var="role">
								<c:if test="${role.value!='super_admin' }">
									<label class="checkbox">
										<input type="checkbox" name="roles" value="${role.value }"/>
										${role.name }
								    </label>
								</c:if>
							</c:forEach>
						</c:if>
						
					</td>
				</tr>
			</table>
			<button class="btn btn-primary offset4" type="submit">保存</button>
			<c:if test="${account!=null}">
				<button class="btn btn-primary" type="button" onclick="resetPassword('${account.id}')">重置密码</button>
			</c:if>
		</form>
	</body>
</html>