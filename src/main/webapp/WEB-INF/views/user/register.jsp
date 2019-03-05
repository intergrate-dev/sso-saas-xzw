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
    <title>注册</title>
    <script type="text/javascript" src="${ctx}/static/js/md5.js"></script>
    <script type="text/javascript" src="${ctx}/static/app/jquery.similar.msgbox.js"></script>
    <script type="text/javascript">
		String.prototype.trim = function () {
			return this .replace(/^\s\s*/, '' ).replace(/\s\s*$/, '' );
		}
	</script>
    <script type="text/javascript">
        var checkcode = ${checkcode};
    	//切换手机和邮箱注册
    	/*function changeType(registerType,theOther) {
    		$("#registerType").val(registerType);
    		$("#li_"+registerType).addClass("active");
    		$("#li_"+theOther).removeClass("active");
    		$(".tr_"+registerType).show();
    		$(".tr_"+theOther).hide();
    		$("#A"+theOther).removeClass();
    		$("#A"+theOther).addClass("eactive");
    		$("#A"+registerType).removeClass();
    		$("#A"+registerType).addClass("active");
    	}*/
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
    		
    		$("#button_email").attr("disabled",false);
			jQuery.validator.addMethod("ispassword", function(value, element) {
				return this.optional(element) || (getStrength(value) >= 2);
			}, "6到20位英文字母、数字、字符组成,至少包含其中两种");

			
			//使用ajax检查用户名是否存在
			jQuery.validator.addMethod("soleUsernameForm", function(value, element) {
				var result = true;
				if(value.trim()!=""){
					$.ajax({ 
		    			async:false,
		    			type: "post", 
		    			url: "${ctx}/user/identities/isLegal?value=" + value + "&field=username&random=" + new Date().getTime() , 
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
			}, "用户名已注册");

			//检查用户名的格式是否正确
			jQuery.validator.addMethod("soleUsername", function(value, element) {
				var result = true;
				if(value.trim() != ""){
					var reg = /^[a-zA-Z][a-zA-Z0-9_]{2,20}$/;
					result = reg.test(value);
				}else{
					return false;
				}
				return result;
			}, "用户名格式不对，请以字母开头，3-20个字符，支持数字、大小写字母和下划线");
			
			//使用ajax检查邮箱是是否存在
			jQuery.validator.addMethod("soleEmail", function(value, element) {
				var result = true;
				if(value!=""){
					$.ajax({ 
		    			async:false,
		    			type: "post", 
		    			url: "${ctx}/user/identities/isLegal?value=" + value + "&field=email&random=" + new Date().getTime() , 
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
			}, "邮箱已注册");	
			
	    	//验证表单是否正确
	    	$("#registerForm").validate({ 
	    		//在失去焦点时验证
	         	onfocusout: function(element){
	         		$(element).valid();
	            },
	          	//表单通过验证之后
            	submitHandler: function(form) {  
            		//检查输入的验证码是否正确
            		checkValidateCode($("#registerType").val(),"registerForm",form);
            	}
	         });
	    	
	    });
    	
    	//点击按钮， 发送验证码
    	function sendValidateCode(type,formId){
    		//邮箱验证通过后
	    	if($("#"+formId).validate().element("#" + type)){
	    		if(type=="email"){
    				timerEmail();
				}else {
    				timerPhone();
				}
	    		$.ajax({ 
	    			async:true,
	    			type: "post", 
	    			url: "${ctx}/user/sendCode?type=" + type + "&value=" + $("#"+type).val() + "&useType=0&random=" + new Date().getTime() ,
	    			success: function(msg){
                        var ss = JSON.parse(msg);
                        /*if(ss.code == 'fail'){
                            $.MsgBox.Alert("温馨提示", decodeURIComponent(ss.msg));
                        }*/
                        $.MsgBox.Alert("温馨提示", decodeURIComponent(ss.msg));
					},
				 	error: function() {
				 		$("#button_" + type).attr("disabled",false);
				 		$("#errorDiv").show("slow");
				    }
	    		});
	    	}
    	}
    	
    	//phone和email的计数器要分开写，不然会相互影响
    	var timePhone = 60;
    	function timerPhone(){
    		var butt = $("#button_phone");
    		if(timePhone<=0){
    			butt.attr("disabled",false);
    			butt.html("重新发送");
    			timePhone = 60;
    		}else {
    			butt.attr("disabled",true);
	    		window.setTimeout("timerPhone()",1000); 
	    		butt.html(timePhone+"秒后重新发送");
	    		timePhone--;
    		}
    	}
    	
    	var timeEmail = 60;
    	function timerEmail(){
    		var butt = $("#button_email");
    		if(timeEmail<=0){
    			butt.attr("disabled",false);
    			butt.html("重新发送");
    			timeEmail = 60;
    		}else {
    			butt.attr("disabled",true);
	    		window.setTimeout("timerEmail()",1000); 
	    		butt.html(timeEmail+"秒后重新发送");
	    		timeEmail--;
    		}
    	}
    	
    	//检查输入的验证码是否正确
    	function checkValidateCode(type,formId,form){
    		var inputCode = "inputCode_" + type;
 			var validator = $("#"+formId).validate();
 			var value = $("#"+type).val();
 			//判断是否进行验证码校验
 			if(checkcode == 1){
 				$.ajax({
 	    			async:false,
 	    			type:"post",
 	    			url:"${ctx}/user/checkCode?type=" + type +"&value="+ value + "&inputCode=" + $("#" + inputCode).val() + "&random=" + new Date().getTime() ,
 	    			success: function(msg){
 	    				var json = eval('(' + msg + ')');
 	   					
 	   					if(type=="email"){
 		   					if(json.result == "codeIsNull"){
 		   						validator.showErrors({"inputCode_email": "请获取验证码!"});
 		   					}else if(json.result == "diffrent"){
 		    					validator.showErrors({"inputCode_email": "验证码不正确!"});
 		    				}else if(validator.form()){
 		    					var password = hex_md5($("#password").val());  //对密码进行加密
 		    	                $("#password").val(password);
 		    					form.submit();
 		    				}
 	   					}else {
 		   					if(json.result == "codeIsNull"){
 		   						validator.showErrors({"inputCode_phone": "请获取验证码!"});
 		   					}else if(json.result == "diffrent"){
 		    					validator.showErrors({"inputCode_phone": "验证码不正确!"});
 		    				}else if(validator.form()){
 		    					var password = hex_md5($("#password").val());  //对密码进行加密
 		    	                $("#password").val(password);
 		    					form.submit();
 		    				}
 	   					}
 					},
 				 	error: function() {
 				 		$("#errorDiv").show("slow");
 				    }
 	    		});
 			}else{
 				var password = hex_md5($("#password").val());  //对密码进行加密
 	            $("#password").val(password);
 				form.submit();
 			}
    	}
    </script>
      <style type="text/css">
		
		.container_s{
			width: 100%;
			height: 580px;
		} 
		
		.content{
			/* margin-top:50px; */
			/* margin-left:500px; */
			margin-left:auto;
			margin-right:auto;
			height: 80%;
			width: 395px;
			text-align: left; 
		}
		.logo{
			text-align: left; 
		}
		input.text{
			width: 250px;
			height: 25px;
			margin-top:3px; 

		}
		.register-content{
			width: 100%;
			margin-left: 20px;
			color: white;
		}
		a{
			margin: 10px 5px;
			font-size: 1.1em;
			color: white;
		}

		span{
			margin:5px 5px;
		}

		.sign{
			width: 350px;
		}
		
		.left{
			float: left;
			margin: 10px 5px;
		}
		.right{
			float: right;
			margin: 10px 5px;
		}
		
		img.left{
			margin-left: 70px;
		}
		.content_left{
			margin-left: 20px;
			color:white;
		}
		label {
		    display: inline;
		    margin-bottom: 5px;
		}
		.table th, .table td{
			vertical-align:middle;
			border-top:0;
			font-size: 1.1em;
			font-weight: bold;
		}
		.active{
			color: white;
			font-size: 1.4em;
			font-weight: bold;
			margin-left: 0;
			margin-right: 0;
			text-decoration: none;
		}
		a{
			text-decoration: underline;
		}
		a:hover   {
			color: red;
		}
		
		a.active:hover   {
			color: white;
			text-decoration: none;
		}
		a.eactive:hover   {
			color: white;
			text-decoration: underline;
		}
		.eactive{
			color: red;
			font-size: 1em;
			font-weight: bold;
			margin-left: 0;
			margin-right: 0;
			text-decoration: none;
		}
		#_hr{
			margin-left: 40px;
		}
	</style>
    
</head>
<body>
	<div class="container_s">
		<div class="content">
			<div class="logo">
				<img src="${ctx}/static/images/login/logo.png" style="margin: 20px 10px;">
			</div>
			<%--<div style="margin: 15px 0 0 150px; ">
				<c:if test="${registerByMobileEnabled==1}">
				<a class="active" id="Aphone" onclick="changeType('phone','email')">手机注册</a> 
				<span style="color: white;font-weight: bold;margin-left: 0;margin-right: 0;">|</span> 
				<a class="eactive" id="Aemail" onclick="changeType('email','phone')">邮箱注册</a>
				</c:if>
			</div>--%>
			<div class="left content_left" style="text-align: left;margin: 5px 5px;">
				<form id="registerForm" action="${ctx }/user/register" method="post">
					<%--<c:if test="${registerByMobileEnabled==1}">
		     			<input id="registerType" name="registerType" type="hidden" value="phone">  
		     		</c:if>   
		     		<c:if test="${registerByMobileEnabled==0}">
		     			<input id="registerType" name="registerType" type="hidden" value="email">  
		     		</c:if>   --%>
					<table class="table" style="border: 0px;width: 100%;margin-bottom: 0;">
						<tr class="tr_email">
							<td style="text-align:right">
					     		邮箱：
							</td>
							<td>
					     		<input name="email" id="email" class="email soleEmail text" type="text" placeholder='请输入邮箱' required value="${email}">  
							</td>
						</tr>
						<c:if test="${checkcode ==1}">
							<tr class="tr_email">
							<td style="text-align:right">
					     		验证码：
							</td>
							<td>
	<%-- 				     		<input id="button_email" src="${ctx}/static/images/register/code_email.png" onclick="sendValidateCode('email','registerForm')"type="image"> --%>
								<button id="button_email" class="btn btn-success" type="button" onclick="sendValidateCode('email','registerForm')">获取邮箱验证码</button>
					     		<input id="inputCode_email" name="inputCode_email" style="width:85px;margin-bottom: 0;" type="text" placeholder='验证码' required>  
							</td>
						</tr>
						</c:if>
						
						<tr>
							<td style="text-align:right;width:100px;vertical-align:center;">
					     		用户名：
							</td>
							<td>
					     		<input name="username" class="text soleUsername soleUsernameForm" type="text" rangelength="1,16" maxlength="16" placeholder='请输入用户名' required value="${username}">     
							</td>
						</tr>
						<tr>
							<td style="text-align:right">
					     		密码：
							</td>
							<td>
					     		<input id="password" class="text ispassword" type="password" name="password" rangelength="6,20" maxlength="20" placeholder='请输入密码' required>  
							</td>
						</tr>
						<tr>
							<td style="text-align:right">
					     		确认密码：
							</td>
							<td>
					     		<input type="password" class="text" equalTo="#password" rangelength="6,20" maxlength="20" placeholder='请输入确认密码' required>  
							</td>
						</tr>
						
						<tr>
							<td colspan="2">
								<input type="image" src="${ctx}/static/images/register/button-regist.png" style="width:350px;margin-left: 30px;" type='submit'>
							</td>
						</tr>
					</table><!--
					<div class="left" style="margin:0 0 10px 40px;">
						已有账号，
						<a href="${ctx }/user/login" style="margin-left:0;">直接登录</a>
					</div>
 					<div style="clear:both;margin-left: 40px;"> -->
<!-- 						<div class="left"><hr style="width: 100px;margin-top:10px;"/></div> -->
<!-- 						<div class="left">使用其他账号登录</div> -->
<!-- 						<div class="left"><hr style="width: 100px;margin-top:10px;"/></div>	 -->
<!-- 					</div> -->
					<%@include file="othersLogin.jsp" %>
					<input id="siteId" name="siteId" type="hidden" value="${siteId}" />
				</form>
			</div>
			<div class="left" style="margin-top: 20px;">
				<c:if test="${email!=null}">
					<div class="alert alert-error input-medium controls"style="width:90px;text-align: left;">
					    <button class="close" data-dismiss="alert">×</button>邮箱格式不对，请重试.
					</div>
				</c:if>
				<c:if test="${phone!=null}">
					<div class="alert alert-error input-medium controls"style="width:90px;text-align: left;">
					    <button class="close" data-dismiss="alert">×</button>手机格式不对，请重试.
					</div>
				</c:if>
				<c:if test="${username!=null}">
					<div class="alert alert-error input-medium controls"style="width:90px;text-align: left;">
					    <button class="close" data-dismiss="alert">×</button>用户名含敏感词，请重试.
					</div>
				</c:if>
		   </div>
		   <div class="left" style="margin-top: 130px;">
		   		<c:if test="${username1!=null}">
					<div class="alert alert-error input-medium controls"style="width:90px;text-align: left;">
					    <button class="close" data-dismiss="alert">×</button>用户名格式不对，请重试.
					</div>
				</c:if>
		   </div>
		</div>
	</div>
</body>
</html>