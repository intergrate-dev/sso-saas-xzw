<!DOCTYPE html>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<% 
response.setHeader("Cache-Control","no-store"); 
response.setHeader("Pragrma","no-cache"); 
response.setDateHeader("Expires",0); 
%>
<html>
	<head>
	    <title>| 修改密码</title>
	    <meta charset="utf-8">
	    <meta http-equiv="X-UA-Compatible" content="IE=edge">
	    <script type="text/javascript" src="${ctx}/static/js/md5.js"></script>
	    <script type="text/javascript">
		    function getStrength(passwd){  
	            var intScore = 0;  
	            if (passwd.match(/[a-z]/) || passwd.match(/[A-Z]/)) // [验证]至少一个字母  
	            {  
	                intScore = (intScore+1)  
	            } 
	            if (passwd.match(/\d/)) // [验证]至少一个数字  
	            {  
	                intScore = (intScore+1)  
	            } 
	        	// 特殊字符验证  
	            if (passwd.match(/[!,@#$%^&*?_~ ]/)) // [验证]至少一个特殊字符  
	            {  
	                intScore = (intScore+1)  
	            }
	            return intScore;  
	        }
		    $(document).ready(function(){
				//激活菜单
			   	$("#profile_tab").addClass("checked");
				$("#menu_password").addClass("checked");
				jQuery.validator.addMethod("ispassword", function(value, element) {
					return this.optional(element) || (getStrength(value) >= 2);
				}, "6到20位英文字母、数字、字符组成,至少包含其中两种");
	            $("#updatePasswordForm").validate({ 
	            	//在失去焦点时验证
		         	onfocusout: function(element){
		                $(element).valid();
		            },

	            	submitHandler: function(form) {  //通过验证时之后回调 
	            		var currentPassword = hex_md5($("#currentPassword").val());  //对旧密码进行md5加密
		                $("#currentPassword").val(currentPassword);
	            		var newPassword = hex_md5($("#newPassword").val());  //对新密码进行md5加密
		                $("#newPassword").val(newPassword);
	            		$("#confirmPassword").val(newPassword);
	            		$.ajax({ 
		    				async:false,
			    			type: "post", 
			    			url: "${ctx}/user/password/change", 
			    			data: $('#updatePasswordForm').serialize(),//提交表单，相当于CheckCorpID.ashx?ID=XXX 
			    			success: function(msg){
			    				var json = eval('(' + msg + ')');
			    				if(json.result=="old_password_is_wrong"){
									hideBlockDiv();
							 		$("#blockDiv").show("slow");
									$("#currentPassword").val('');
									$("#currentPassword").focus();
								}else {
									hideBlockDiv();
							 		$("#successDiv").show("slow");
							 		//$(".btn").prop("disabled","disabled"); 
								}
							},
						 	error: function() {
						 		hideBlockDiv();
						 		$("#errorDiv").show("slow");
						    }
	            		});
	            	}, 
	            	invalidHandler: function(form, validator) {//不通过验证时回调 
	            	    return false; 
	            	} 
	            });
	    	});
	    	
	    </script>
	</head>
	<body>
		<%@include file="common_profile.jsp" %>
		<div class="contend_div">
			<div class="infoDiv" style="height: 50px;">
				<tags:blockDiv type="success" hide="true" info="操作成功！"/>
				<tags:blockDiv type="error" hide="true" info="操作失败，请稍后重试！"/>
				<tags:blockDiv type="block" hide="true" info="原始密码输入错误,请重新输入！"/>
				<c:if test="${!hasPassword }">
					<tags:blockDiv type="info" hide="false" info="您未绑定用户，只有绑定用户才能使用本地用户修改密码！"/>
				</c:if>
			</div>
			
			<div class="content_info">
			<c:if test="${hasPassword }">
				<form id="updatePasswordForm">
					<input type="hidden" name="hasPassword" value="${hasPassword }">
					<table class="table table-bordered">
						<c:if test="${hasPassword}">
							<tr>
								<td style="text-align:right;width: 20%">
						     		原始密码：
								</td>
								<td>
						     		<input id="currentPassword" name="currentPassword" type="password" placeholder='请输入原始密码' required autofocus>     
								</td>
							</tr>
						</c:if>
						<tr>
							<td style="text-align:right">
					     		新密码：
							</td>
							<td>
					     		<input id="newPassword" class="ispassword" type="password" rangelength="6,20"  name="newPassword" placeholder='请输入新密码' required>  
							</td>
						</tr>
						<tr>
							<td style="text-align:right">
					     		确认新密码：
							</td>
							<td>
					     		<input id="confirmPassword" name="confirmPassword" type="password" rangelength="6,20"  equalTo="#newPassword" placeholder='请输入确认新密码' required>  
							</td>
						</tr>
					</table>
					<button class="btn btn-primary " style="width: 200px;margin-left: 250px;margin-top: 30px;" type="submit"> 确 定 </button>
				</form>
			</c:if>
			</div>
		</div>
	</body>
</html>