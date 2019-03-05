<%@ page contentType="text/html;charset=UTF-8" %>
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
		    	
	            $("#updatePasswordForm").validate({ 
	            	//在失去焦点时验证
		         	onfocusout: function(element){
		                $(element).valid();
		            },

	            	submitHandler: function(form) {  //通过验证时之后回调 
	            		$.ajax({ 
		    				async:false,
			    			type: "post", 
			    			url: "${ctx}/admin/account/updatePassword", 
			    			data: $('#updatePasswordForm').serialize(),//提交表单，相当于CheckCorpID.ashx?ID=XXX 
			    			success: function(msg){
			    				var json = eval('(' + msg + ')');
			    				if(json.result=="old_password_is_wrong"){
									hideBlockDiv();
							 		$("#blockDiv").show("slow");
									$("#oldPassword").val('');
									$("#oldPassword").focus();
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
		<tags:blockDiv type="success" hide="true" info="操作成功！"/>
		<tags:blockDiv type="error" hide="true" info="操作失败，请稍后重试！"/>
		<tags:blockDiv type="block" hide="true" info="原始密码输入错误,请重新输入！"/>
		<ul class="breadcrumb">
		  <li><a href="${ctx }/admin/account/">管理员设置</a> <span class="divider">/</span></li>
		  <li class="active"><a href="#">修改密码</a></li>
		</ul>
		<form id="updatePasswordForm">
			<input type="hidden" id="accountId" name="id" value="<shiro:principal property="accountId"/>">  
			<table class="table table-bordered table-condensed">
				<tr>
					<td style="text-align:right;width: 20%">
			     		原始密码：
					</td>
					<td>
			     		<input id="oldPassword" name="oldPassword" type="password" placeholder='请输入原始密码' required autofocus>     
					</td>
				</tr>
				<tr>
					<td style="text-align:right">
			     		新密码：
					</td>
					<td>
			     		<input id="newPassword" type="password" name="newPassword" placeholder='请输入新密码' required>  
					</td>
				</tr>
				<tr>
					<td style="text-align:right">
			     		确认新密码：
					</td>
					<td>
			     		<input name="confirmPassword" type="password" equalTo="#newPassword" placeholder='请输入确认新密码' required>  
					</td>
				</tr>
			</table>
			<button class="btn btn-primary offset4" type="submit">确  定</button>
		</form>
	</body>
</html>