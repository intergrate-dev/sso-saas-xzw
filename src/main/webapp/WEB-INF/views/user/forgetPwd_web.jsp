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
    <div class="regiCont">
        <%--action="${ctx }/user/password/find/resetPwd"--%>
        <form id="registerForm" method="post" >
            	<input id="byType" name="byType" type="hidden" value="phone">
				<input id="isAppType" name="isAppType" type="hidden" value="web" />
				<input id="registerType" name="registerType" type="hidden" value="phone" />
				<input id="value" name="value" type="hidden" value="">
            <div class="regInput">
                <input id="phoneOrEmail" name="phoneOrEmail" class="required" type="text" value placeholder="请输入邮箱">
                <div class="inputIcon"><img src="${ctx}/static/app/img/43.png"></div>
                <div class="erroMsg"></div>
            </div>
            <div class="regInput">
                <input id="input_code" name="input_code" type="text" placeholder="请输入验证码" class="required">
                <div class="getVerifi" id="sendCode" style="cursor: pointer;">点击获取</div>
            </div>
            <div class="regInput">
                <%--<input id="password_hid" name="password_hid" type="password" class="required" value="111aaa" placeholder="请填写密码（不少于6位）">--%>
                <input id="password_hid" name="password_hid" type="password" class="required" value placeholder="请填写密码（不少于6位）">
                <input type="hidden" id="password" name="password"/>
                <div class="inputIcon"><img src="${ctx}/static/app/img/43.png"></div>
                <div class="promptMsg"></div>
                <div class="erroMsg"></div>
            </div>
            <div class="regInput ">
                <%--<input id="confirm_password" name="confirm_password" type="password" class="required" value="111aaa" placeholder="请确认密码（不少于6位）">--%>
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

<script type="text/javascript">
window.checkcode = ${checkcode};
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
	checkValidateCode :function(){
		var phoneOrEmail = $("#phoneOrEmail").val(); //邮箱号码
		var registerType = $("#registerType").val(); //注册类型
		var input_code = $("#input_code").val(); //验证码
        if(!/^[0-9]{6}$/.test(input_code)){
            $("#input_code").parent().find('.erroMsg').html('<img src="../static/app/img/42.png" >请先获取验证码!');
            return;
        }
        var password = hex_md5($("#password_hid").val());  //对密码进行加密
        $("#password").val(password);   //赋值给隐藏框，为了避免页面显示时，密码字符串加长
        var param = {value: phoneOrEmail, password: password, inputCode: input_code, registerType: registerType};
        var url = "${ctx}/user/password/find/resetPassword?value=" + phoneOrEmail + "&password=" + password +
                    "&inputCode=" + input_code + "&registerType=" + registerType;
        $.ajax({
            async:false,
            type:"post",
            url: url,
            //dataType: "json",
            success: function(robj){
                var ss = JSON.parse(msg);
                if(ss.code == 'fail'){
                    $.MsgBox.Alert("温馨提示", decodeURIComponent(ss.msg));
                    return;
                }
                var paramValue = "byType=" + $('#byType').val() + "&isAppType=" + $('#isAppType').val();
                // TODO
                /*var postHandleUrl = "${ctx}/user/resetPwd/postHandle?" + paramValue;
                location.href = postHandleUrl;*/
                $.MsgBox.Alert("温馨提示", "修改密码成功！");
                location.href = "${ctx }/user/login";
            },
            error: function(jqXHR, textStatus, errorThrown) {
                console.log("温馨提示","checkValidateCode失败：readyState="+jqXHR.readyState+",status="+jqXHR.status+",textStatus="+textStatus);//$.MsgBox.Alert
            }
        });

	},
	//填入注册信息，注册还是邮箱
	isPhoneOrEmail :function(){
		$("#phoneOrEmail").removeClass();
		var phoneOrEmail = $("#phoneOrEmail").val(); //邮箱
		//var emailReg = /^(\w)+(\.\w+)*@(\w)+((\.\w+)+)$/; //邮箱正则
        var emailReg = /^(([a-zA-Z0-9_-])+(.*))@(([a-zA-Z0-9_-])+).(([a-zA-Z0-9_-])+)$/; //邮箱正则
		if(emailReg.test(phoneOrEmail)){ //邮箱
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
			$("#errorMsg").text("请输入正确的邮箱"); //加上错误信息
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
        $('#sendCode').unbind();
        $('#sendCode').css("cursor", "not-allowed");
   		timer();
   		$.ajax({ 
   			async:true,
   			type: "post", 
   			url: "${ctx}/user/sendCode?type=" + registerType + "&value=" + phoneOrEmail + "&useType=1&random=" + new Date().getTime() ,
   			success: function(msg){
                var ss = JSON.parse(msg);
                /*if(ss.code == 'fail'){
                    $.MsgBox.Alert("温馨提示", decodeURIComponent(ss.msg));
                }*/
                $.MsgBox.Alert("温馨提示", decodeURIComponent(ss.msg));
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
        $("#sendCode").click(function() {
            register.sendValidateCode();
        });
        $('#sendCode').css("cursor", "pointer");
	}else {
		butt.attr("disabled",true);
		window.setTimeout("timer()",1000); 
		butt.text(time+"秒后重发");
		time--;
	}
}
$(function(){
	//$('#window_wnd_title', window.parent.document).text("忘记密码");
	findPwd.init();
});
</script>
</body>
</html>