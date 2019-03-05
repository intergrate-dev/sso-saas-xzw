<!DOCTYPE html>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="org.apache.shiro.web.filter.authc.FormAuthenticationFilter"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<% 
response.setHeader("Cache-Control","no-store"); 
response.setHeader("Pragrma","no-cache"); 
response.setDateHeader("Expires",0); 
%>
<html>
	<head>
	    <title>找回密码</title>
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
				jQuery.validator.addMethod("ispassword", function(value, element) {
					return this.optional(element) || (getStrength(value) >= 2);
				}, "6到20位英文字母、数字、字符组成,至少包含其中两种");
		    	//验证表单是否正确
		    	$("#findpwdForm").validate({ 
		    		//在失去焦点时验证
		         	onfocusout: function(element){
		         		$(element).valid();
		            },
		        	//表单通过验证之后
	            	submitHandler: function(form) {  
	            		//检查输入的验证码是否正确
	            		var password = hex_md5($("#password").val());  //对密码进行加密
    	                $("#password").val(password);
    					form.submit();
	            	}
		         });
		    });
			
		</script>
		<style type="text/css">
			
			a:HOVER {
				text-decoration: none;	
			}
			.table th, .table td {
			    border-top: 0px;
			    padding: 10px 0px;
			    
			}
		</style>
	</head>
	<body>
		<div class="wrapper">
			<%@include file="common_findpwd.jsp" %>
			<div class="nav">
				<a class="${phone?'echecked':'checked'}">通过邮箱找回密码</a>
			</div>
			<div class="center">
				<img src="${ctx}/static/images/findpwd/${byType}_s3.png">
			</div>
			<form id="findpwdForm" action="${ctx }/user/password/find/resetPwd" method="post">
				<input id="byType" name="byType" type="hidden" value="${byType }">
				<input id="value" name="value" type="hidden" value="${value }">
				<div class="infoDiv">
					<div class="info_div_info" style="margin-left:40%;font-size: 1.1em;">
						登录账号：${value }
					</div>
				</div>
				<div class="inputc2" style="margin-top: 10px;">
					<table class="table" style="border: 0px;margin-bottom: 0;">
						<tr>
							<td style="text-align:right;width:33%;vertical-align:center;">
								新密码： 
							</td>
							<td>
								<input id="password" class="text300 ispassword" style="height: 25px;" type="password" name="password" rangelength="6,20" maxlength="20" placeholder='请输入密码' required>  
							</td>
						</tr>
						<tr>
							<td style="text-align:right;width:33%;vertical-align:center;">
								确认密码： 
							</td>
							<td>
								<input type="password" class="text300" style="height: 25px;" equalTo="#password" rangelength="6,20" maxlength="20" placeholder='请输入确认密码' required>  
							</td>
						</tr>
					</table>
				</div>
				<div class="buttons top30" >
					<input id="sendCodeButton" type="submit" value="提 交 " class="btn btn-warning" style="width: 150px"/> 
				</div>
			</form>
		</div>
	</body>
</html>