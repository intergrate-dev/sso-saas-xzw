<!DOCTYPE html>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="org.apache.shiro.web.filter.authc.FormAuthenticationFilter"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<% 
response.setHeader("Cache-Control","no-store"); 
response.setHeader("Pragrma","no-cache"); 
response.setDateHeader("Expires",0); 
%>
<html>
<head>
	<meta charset="UTF-8">
    <meta name="keywords" content=""/>
    <meta name="description" content=""/>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <meta name="viewport" content="initial-scale=1.0,user-scalable=1,maximum-scale=1,width=device-width"/>
    <meta name="apple-mobile-web-app-capable" content="yes"/>
    <meta name="apple-mobile-web-app-status-bar-style" content="black"/>
    <title>忘记密码</title>
    
    <link rel="stylesheet" type="text/css" href="${ctx}/static/app/css/basic.css" />
    <script type="text/javascript" src="${ctx}/static/app/js/jquery-2.2.4.min.js"></script>
    <script type="text/javascript" src="${ctx}/static/app/js/validate_forgetPwd.js"></script>
    <script type="text/javascript" src="${ctx}/static/js/md5.js"></script>
    <script type="text/javascript" src="${ctx}/static/app/jquery.similar.msgbox.js"></script>
</head>
<body>
<div class="regiTitle">
        <span class="regBackBtn"><ahref="javascript:;" onclick="javascript:history.go(-1);">&lt;</a></span>
        找回密码
    </div>
    <div class="regiCont">
        <form id="registerForm" method="post" action="${ctx }/user/password/find/resetPwd">
            	<input id="byType" name="byType" type="hidden" value="">
				<input id="value" name="value" type="hidden" value="">
				<input id="registerType" name="registerType" type="hidden" value="phone" />
				<input id="isAppType" name="isAppType" type="hidden" value="app" />
            <div class="regInput">
                <input id="phoneOrEmail" name="phoneOrEmail" class="required" type="text" value placeholder="请输入邮箱">
                <div class="inputIcon"><img src="${ctx}/static/app/img/43.png"></div>
                <div class="erroMsg"></div>
            </div>
            <div class="regInput">
                <input id="input_code" name="input_code" type="text" placeholder="请输入验证码">
                <div class="getVerifi" id="sendCode">点击获取</div>
            </div>
            <div class="regInput">
                <input id="password_hid" name="password_hid" type="password" class="required" value placeholder="请填写密码（不少于6位）">
                <input type="hidden" id="password" name="password"/>
                <div class="inputIcon"><img src="${ctx}/static/app/img/43.png"></div>
                <div class="promptMsg"></div>
                <div class="erroMsg"></div>
            </div>
            <div class="regInput ">
                <input id="confirm_password" name="confirm_password" type="password" class="required" value placeholder="请确认密码（不少于6位）">
                <div class="inputIcon"><img src="${ctx}/static/app/img/43.png"></div>
                <div class="erroMsg"></div>
            </div>
		    <div class="gryBox">
		        <div style="width: 100%;height:0.888rem;background: #f6f6f6"></div>
		        <input id="pwdRemdcCrtainBtn" class="pwdRemdcCrtainBtn" type="button" value="提交">
		    </div>
        </form>
    </div>
</div>
<script type="text/javascript">
var findPwd = {
	init : function(){
		//发送短信验证码
		$("#sendCode").click(function(){
			findPwd.sendValidateCode();
		});
		$("#pwdRemdcCrtainBtn").click(function() {
			findPwd.checkValidateCode();
		});
	},
	checkValidateCode :function(form){
		var phoneOrEmail = $("#phoneOrEmail").val(); //邮箱号码
		var registerType = $("#registerType").val(); //注册类型
		var input_code = $("#input_code").val(); //验证码
			$.ajax({
				async:false,
				type:"post",
				url:"${ctx}/user/checkCode?type=" + registerType +"&value="+ phoneOrEmail + "&inputCode=" + input_code + "&random=" + new Date().getTime() ,
				success: function(msg){
					var json = eval('(' + msg + ')');
					if(json.result == "codeIsNull"){
						$("#input_code").parent().find('.erroMsg').html('<img src="../static/app/img/42.png" >请先获取验证码!');
					}else if(json.result == "diffrent"){
						$("#input_code").parent().find('.erroMsg').html('<img src="../static/app/img/42.png" >验证码不正确!');
	   				}else if(json.result == "same"){
	   					var password = hex_md5($("#password_hid").val());  //对密码进行加密
	   					$("#password").val(password);   //赋值给隐藏框，为了避免页面显示时，密码字符串加长
	   					var len = $("#password_hid").val().length; //未加密的密码的长度
	   		            $("#password_hid").val(password.substring(0,len)); //避免传输明文和字符串加长，将password_hid中的密码进行加密后截取
	   		         	$("#registerForm").submit();
	   				}
				},
			 	error: function(jqXHR, textStatus, errorThrown) {
			 		//$("#errorDiv").show("slow");
			 		$.MsgBox.Alert("温馨提示","checkValidateCode失败：readyState="+jqXHR.readyState+",status="+jqXHR.status+",textStatus="+textStatus);
			    }
			});
		
	},
	
	isPhoneOrEmail :function(){
		$("#phoneOrEmail").removeClass();
		var phoneOrEmail = $("#phoneOrEmail").val(); //邮箱
		var phoneReg = /^1[34578]\d{9}$/; //正则
		var emailReg = /^(\w)+(\.\w+)*@(\w)+((\.\w+)+)$/; //邮箱正则
		if(phoneReg.test(phoneOrEmail)){ //
			$("#registerType").attr("value","phone"); //注册类型
			
			$("#byType").attr("value","phone"); //类型
			$("#value").attr("value",phoneOrEmail); 邮箱

			$("#phoneOrEmail").attr("name","phone");
			$("#phoneOrEmail").addClass("login-input1 isPhone solePhone");
			$("#errorMsg").text(""); //清空错误信息
			return true;
		}else if(emailReg.test(phoneOrEmail)){ //邮箱
			$("#registerType").attr("value","email"); //注册类型
			
			$("#byType").attr("value","email"); //类型
			$("#value").attr("value",phoneOrEmail); //邮箱

			$("#phoneOrEmail").attr("name","email");
			$("#phoneOrEmail").addClass("login-input1 isEmail soleEmail");
			$("#errorMsg").text(""); //清空错误信息
			return true;
		}else{
			$("#registerType").attr("value",""); //注册类型
			
			$("#byType").attr("value",""); //类型
			$("#value").attr("value",""); //邮箱

			$("#phoneOrEmail").attr("name","");
			$("#phoneOrEmail").addClass("login-input1");
			$("#errorMsg").text("请输入正确的者邮箱"); //加上错误信息
			return false;
		}
	},
	//发送验证码
	sendValidateCode :function(){
		var isPE = findPwd.isPhoneOrEmail();
		if(!isPE){
			return false;
		}
		var phoneOrEmail = $("#phoneOrEmail").val(); //邮箱号码
		var registerType = $("#registerType").val(); //注册类型
		//邮箱验证通过后
   		timer();
   		$.ajax({ 
   			async:true,
   			type: "post", 
   			url: "${ctx}/user/sendCode?type=" + registerType + "&value=" + phoneOrEmail + "&useType=1&random=" + new Date().getTime() ,
   			success: function(msg){
                var ss = JSON.parse(msg);
                if(ss.code == 'fail'){
                    $.MsgBox.Alert("温馨提示", decodeURIComponent(ss.msg));
                }
			},
		 	error: function(jqXHR, textStatus, errorThrown) {
		 		//$.MsgBox.Alert("温馨提示","网络问题，请稍后再试");
		 		//$.MsgBox.Alert("温馨提示","sendValidateCode失败：readyState="+jqXHR.readyState+",status="+jqXHR.status+",textStatus="+textStatus);
		    }
   		});
	}
};
//验证码多少秒后重新发送
var time = 60;
function timer(){
	var butt = $("#sendCode");
	if(time<=0){
		butt.attr("disabled",false);
		butt.text("重新发送");
		time = 60;
	}else {
		butt.attr("disabled",true);
		window.setTimeout("timer()",1000); 
		butt.text(time+"秒后重发");
		time--;
	}
}
$(function(){
	findPwd.init();
});
</script>
</body>
</html>