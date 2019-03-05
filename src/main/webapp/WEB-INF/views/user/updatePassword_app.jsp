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
	<title>修改密码</title>
	<meta charset="utf-8">
	<meta http-equiv="X-UA-Compatible" content="IE=edge">
	<meta content="width=device-width,initial-scale=1.0,maximum-scale=1.0,user-scalable=no" name="viewport">
	<meta content="yes" name="apple-mobile-web-app-capable">
	<meta content="black" name="apple-mobile-web-app-status-bar-style">
	<meta content="telephone=no" name="format-detection">
	<meta content="email=no" name="format-detection">
	<meta name="msapplication-tap-highlight" content="no">
	<link rel="stylesheet" href="${ctx}/static/app/css/style.css">
	<link rel="stylesheet" href="${ctx}/static/jquery-validation1_11_1/validate.css">
	<script type="text/javascript" src="${ctx}/static/js/md5.js"></script>
	<script type="text/javascript" src="${ctx}/static/app/js/jquery-2.2.4.min.js"></script>
	<script type="text/javascript" src="${ctx}/static/jquery-validation1_11_1/jquery.validate.min.js"></script>
	<script type="text/javascript" src="${ctx}/static/jquery-validation1_11_1/messages_bs_zh.js"></script>
	<script type="text/javascript" src="${ctx}/static/app/js/main.js"></script>
	<script type="text/javascript" src="${ctx}/static/app/jquery.similar.msgbox.js"></script>

</head>
<body>
<header>
	<a class="nav_wrap_left" href="javascript:;" onclick="javascript:history.go(-1);"><span class="back_icon"></span></a>
	<h2>修改密码</h2>
</header>
<section class="cont pt44">
	<form id="updatePasswordForm">
		<div class="login-cont">
			<dl class="border-b">
				<dt><em class="icon-pass2"></em></dt>
				<dd>
					<input type="password" id="currentPassword_hid" name="currentPassword_hid" placeholder="请输入原始密码" class="login-input1">
					<input type="hidden" id="currentPassword" name="currentPassword">
				</dd>
			</dl>
			<dl class="border-b">
				<dt><em class="icon-pass2"></em></dt>
				<dd>
					<input type="password" id="newPassword_hid" name="newPassword_hid" placeholder="请输入新密码（6~16位，数字和字母均可）" rangelength="6,20" minlength="6" maxlength="20" class="login-input1 ispassword">
					<input type="hidden" id="newPassword" name="newPassword">
				</dd>
			</dl>
			<dl class="border-b">
				<dt><em class="icon-pass"></em></dt>
				<dd>
					<input type="password" id="confirmPassword" name="confirmPassword" equalTo="#newPassword_hid" placeholder="请确认新密码" rangelength="6,20" minlength="6" maxlength="20" class="login-input1">
				</dd>
			</dl>
			<!-- <p class="error-text" style="display:none;">您输入的密码有误~~</p> -->
		</div>
		<p class="f-btn"><span class="f-b-3"><input class="f-btn1" type="submit" value="提交"></span></p>
	</form>
</section>
<script type="text/javascript">
var updatePwd = {
	init :function(){
		jQuery.validator.addMethod("ispassword", function(value, element) {
			return this.optional(element) || (getStrength(value) >= 2);
		}, "6到20位英文字母、数字、字符组成,至少包含其中两种");
		//验证表单是否正确，并提交表单
    	$("#updatePasswordForm").validate({ 
    		//在失去焦点时验证
         	onfocusout: function(element){
         		$(element).valid();
            },
          	//表单通过验证之后
        	submitHandler: function(form) {  //通过验证时之后回调 
        		//检查输入的验证码是否正确，正确则提交表单
        		updatePwd.subForm(form);
        	},
            invalidHandler :function(form, validator) {//不通过验证时回调 
        	    return false; 
        	} 
    	});
	},
	subForm :function(form){
		//原始密码
		var currentPassword = hex_md5($("#currentPassword_hid").val());  //对旧密码进行md5加密
        $("#currentPassword").val(currentPassword);   //给隐藏框赋值
        var currentPassword_len = $("#currentPassword_hid").val().length; //未加密的密码的长度
        $("#currentPassword_hid").val(currentPassword.substring(0,currentPassword_len)); //避免传输明文和字符串加长，将password_hid中的密码进行加密后截取
        
        //新密码
		var newPassword = hex_md5($("#newPassword_hid").val());  //对新密码进行md5加密
        $("#newPassword").val(newPassword);  //给隐藏框赋值
        var newPassword_len = $("#newPassword_hid").val().length; //未加密的密码的长度
        $("#newPassword_hid").val(newPassword.substring(0,newPassword_len)); //避免传输明文和字符串加长，将password_hid中的密码进行加密后截取
        
        //确认新密码
		$("#confirmPassword").val(newPassword.substring(0,newPassword_len));
        
		$.ajax({ 
			async:false,
			type: "post", 
			url: "${ctx}/user/password/change", 
			data: $('#updatePasswordForm').serialize(),//提交表单，相当于CheckCorpID.ashx?ID=XXX 
			success: function(msg){
				var json = eval('(' + msg + ')');
				if(json.result=="old_password_is_wrong"){
					$.MsgBox.Alert("温馨提示","原始密码有误，请重新输入");
				}else {
					$.MsgBox.NoCancleConfirm("温馨提示","密码修改成功，请重新登录",function(){
						location.href = decodeURIComponent("${anyUrl}");
					});
				}
			},
		 	error: function(jqXHR, textStatus, errorThrown) {
		 		$.MsgBox.Alert("温馨提示","修改密码失败：readyState="+jqXHR.readyState+",status="+jqXHR.status+",textStatus="+textStatus);
		    }
		});
	}
}

$(function(){
	updatePwd.init();
});

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
</script>
</body>
</html>