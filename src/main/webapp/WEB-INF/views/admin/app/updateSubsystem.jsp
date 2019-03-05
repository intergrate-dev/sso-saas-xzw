<!DOCTYPE html>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="com.founder.sso.admin.entity.Roles"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<html>
	<head>
	    <title>修改子系统 </title>
	    <meta charset="utf-8">
	    <meta http-equiv="X-UA-Compatible" content="IE=edge">
	    <script type="text/javascript">
		    $(document).ready(function(){
				//激活菜单
			    $("#app_tab").addClass("active");
			  	//使用ajax检查code是否存在
				jQuery.validator.addMethod("soleCode", function(value, element) {
					var result = true;
					if(value!=""){
						$.ajax({ 
			    			async:false,
			    			type: "post", 
			    			url: "${ctx}/admin/app/isCodeLegal?code=" + value + "&systemId=${subsystem.id}&random=" + new Date().getTime() , 
			    			success: function(msg){
			    				var json = eval('(' + msg + ')');
			    				if(json.result=='false'){
			    					result = false;
			    				}
							},
						 	error: function() {
						 		$("#errorDiv").show("slow");
						    }
			    		});
					}
					return result;
				}, "code已注册");	
			  	
// 				jQuery.validator.addMethod("checkHomepage", function(value, element) {
// 					var strRegex = "^((https|http|ftp|rtsp|mms)://)?[a-z0-9A-Z]{3}\.[a-z0-9A-Z][a-z0-9A-Z]{0,61}?[a-z0-9A-Z]\.com|net|cn|cc (:s[0-9]{1-4})?/$";
// 			            var re = new RegExp(strRegex);
// 			            if (re.test(value)) {
// 			                return true;
// 			            } else {
// 			                return false;
// 			            }
// 				}, "系统入口不是合法的url");	
			  	
			  
	            $("#updateSubsystemForm").validate({ 
	            	//在失去焦点时验证
		         	onfocusout: function(element){
		                $(element).valid();
		            },

	            	submitHandler: function(form) {  //通过之后回调 
	            		submitForm();
					}, 
					invalidHandler: function(form, validator) {  //不通过回调 
					   return false; 
					} 
	            });
	    	});
	    	
	    	function submitForm(){
	    		$.ajax({ 
	    			async:false,
	    			type: "post", 
	    			url: "${ctx}/admin/app/save", 
	    			data: $('#updateSubsystemForm').serialize(),//提交表单，相当于CheckCorpID.ashx?ID=XXX 
	    			success: function(msg){
	    				hideBlockDiv();
	    				if($("#subsystemId").val()==""){
		    				$(".btn").prop("disabled","disabled"); 
	    				}
						$("#successDiv").show("slow");
					},
				 	error: function() {
				 		hideBlockDiv();
				 		$("#errorDiv").show("slow");
				    }
	    		});
	    	}
	    </script>
	</head>
	<body>
		<tags:blockDiv type="success" hide="true" info="操作成功！"/>
		<tags:blockDiv type="error" hide="true" info="操作失败，请稍后重试！"/>
		<tags:blockDiv type="block" hide="true" info="用户名已经存在,请重新输入！"/>
		<ul class="breadcrumb">
		  <li><a href="${ctx }/admin/app/">app子系统管理</a> <span class="divider">/</span></li>
		  <li class="active"><a href="#">${subsystem==null?'添加子系统':'修改子系统信息' }</a></li>
		</ul>
		<form id="updateSubsystemForm" name="updateSubsystemForm" class='form-signin' method="post" >
			<input type="hidden" id="subsystemId" name="id" value="${subsystem.id}">  
			<table class="table table-bordered table-condensed">
				<tr>
					<td style="text-align:right;" >
			     		系统代码：
					</td>
					<td>
						<c:if test="${subsystem != null }">
							<input type="text" name="code" class="soleCode" value="${subsystem.code }" placeholder='请输入系统代码，唯一' class="required" required readonly>
						</c:if>
			     		<c:if test="${subsystem == null }">
							<input type="text" name="code" class="soleCode" value="${subsystem.code }" placeholder='请输入系统代码，唯一' class="required" required autofocus>
						</c:if>  
<!-- 			     		<font style="font-size: 0.9em;color: red;">系统代码是子系统同步登录时的凭证，如需修改，请与相关子系统开发人员联系。</font> -->
					</td>
				</tr>
				<tr>
					<td style="text-align:right;" >
			     		系统名称：
					</td>
					<td>
			     		<input type="text" name="name" value="${subsystem.name }" placeholder='请输入系统名称' class="required" required>  
					</td>
				</tr>
				<tr>
					<td style="text-align:right;width: 20%">
			     		子系统域名：
					</td>
					<td>
			     		<input type="text" name="domain" class="checkHomepage" value="${subsystem.domain}" style="width: 700px;" placeholder='请输入子系统域名' class="input400" required>  
					</td>
				</tr>
				<tr>
					<td style="text-align:right">
			     		同步退出地址：
					</td>
					<td>
			     		<input type="text" name="logoutUrl" value="${subsystem.logoutUrl}" placeholder='请输入同步退出地址' style="width: 700px;" class="input400" required>  
					</td>
				</tr>
				<tr>
					<td style="text-align:right">
			     		同步登陆地址：
					</td>
					<td>
			     		<input type="text" name="loginUrl" value="${subsystem.loginUrl}" placeholder='请输入同步登录地址' style="width: 700px;" class="input400" required>  
					</td>
				</tr>
				<tr>
					<td style="text-align:right">
			     		说明信息：
					</td>
					<td>
			     		<input type="text" name="description" value="${subsystem.description}" style="width: 700px;" placeholder='请输入说明信息'>  
					</td>
				</tr>
				<tr>
					<td style="text-align:right">
			     		是否启用子系统：
					</td>
					<td>
						<input id="using" type="radio" name="enabled" value="true" <c:if test="${subsystem==null || subsystem.enabled}">checked</c:if>> 
						<label for="using" style="display:inline;">启用</label>
						&nbsp;&nbsp;
						<input id="unUsing" type="radio" name="enabled" value="false"<c:if test="${subsystem!=null && !subsystem.enabled}">checked</c:if>> 
						<label for="unUsing"style="display:inline;">不启用</label>
					</td>
				</tr>
				<tr>
					<td style="text-align:right">
			     		加密方式：
					</td>
					<td>
						<input id="encrypt-none" type="radio" name="encryptType" value="none" <c:if test="${subsystem==null || subsystem.encryptType eq 'none'}">checked</c:if>> 
						<label for="encrypt-none" style="display:inline;">none(不加密)</label>
						&nbsp;&nbsp;
						<input id="encrypt-aes" type="radio" name="encryptType" value="aes"<c:if test="${subsystem!=null && subsystem.encryptType eq 'aes'}">checked</c:if>> 
						<label for="encrypt-aes" style="display:inline;">aes加密</label>
					</td>
				</tr>
				<c:if test="${subsystem != null }">
				<tr>
					<td style="text-align:right">
			     		密钥：
					</td>
					<td>
						<input id="secretKey" type="text" name="secretKey" value="${subsystem.secretKey}" style="width: 700px;" readonly> 
					</td>
				</tr>
				</c:if>
			</table>
			<button class="btn btn-primary offset4" type="submit">保存</button>
			<button class="btn btn-primary" type="reset" >重置</button>
		</form>
	</body>
</html>