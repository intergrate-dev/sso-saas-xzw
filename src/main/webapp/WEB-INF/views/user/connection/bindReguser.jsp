<!DOCTYPE html>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@page import="com.founder.sso.service.oauth.OauthClientManager"%>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix='fmt' uri="http://java.sun.com/jsp/jstl/fmt" %> 
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<% 
response.setHeader("Cache-Control","no-store"); 
response.setHeader("Pragrma","no-cache"); 
response.setDateHeader("Expires",0); 
%>
<html>
<head>
<title>个人资料</title>
<script type="text/javascript" src="${ctx}/static/js/md5.js"></script>
<script type="text/javascript" src="${ctx}/static/app/jquery.similar.msgbox.js"></script>
<style type="text/css">
.info {
	font-size:12px;
}
.container_s{
	margin-top:10px;
	margin-left:10px;
	height: 80%;
	/* width: 50%; */
	text-align: center; 
} 
.table th, .table td{
	vertical-align:middle;
	border-top:0;
	font-size: 1.1em;
	font-weight: bold;
}

input.text{
	width: 250px;
	height: 25px;
	margin-top:3px; 
}

/* .content_bindUser{
	text-align: center; 
	width:100%;
	background-color: #F7F8F9;
	padding: 10px;
	padding-top: 20px;
	margin-left: auto;
	margin-right: auto;
	border-color:black;
	border: 1px solid ;
	border-radius: 20px;
} */
h5{
	margin-top: 5px;
	margin-bottom: 5px;
}
.hasRegister{
	display: none;
}
.bigger{
	/* font-weight: bold; */
	/* font-size: 1.2em; */
}
.hidden {
  display: none !important;
}
</style>
<script type="text/javascript">
$(document).ready(function(){
//验证表单是否正确
  	$("#connectForm").validate({ 
  		//在失去焦点时验证
       	onfocusout: function(element){
       		$(element).valid();
        },
        //表单通过验证之后
      	submitHandler: function(form) { 
      		var isR = $("#connectType").val();
      		if(isR == "noRegister"){  //未注册过账号
      			//检查输入的验证码是否正确
        		checkValidateCode($("#regtype").val(),"connectForm",form);
      		}else if(isR == "hasRegister"){  //已经有账号，则直接绑定
      			submitForm(form);
      		}
    	}
    });
	
  //检查用户名的格式是否正确
	jQuery.validator.addMethod("soleUsernameForm", function(value, element) {
		var result = true;
		if(value.trim() != ""){
			var reg = /^[a-zA-Z0-9_\s\u4e00-\u9fa5]{2,20}$/;
			result = reg.test(value);
		}else{
			return false;
		}
		return result;
	}, "用户名格式不正确");
	
   	//使用ajax检查用户名是否存在
	jQuery.validator.addMethod("soleUsername", function(value, element) {
		var result = true;
		if(value!=""){
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
  	
  	//邮箱
	//校验邮箱格式是否正确
	jQuery.validator.addMethod("isEmail", function(value, element) {
		var email = /^((([a-z]|\d|[!#\$%&'\*\+\-\/=\?\^_`{\|}~]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])+(\.([a-z]|\d|[!#\$%&'\*\+\-\/=\?\^_`{\|}~]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])+)*)|((\x22)((((\x20|\x09)*(\x0d\x0a))?(\x20|\x09)+)?(([\x01-\x08\x0b\x0c\x0e-\x1f\x7f]|\x21|[\x23-\x5b]|[\x5d-\x7e]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(\\([\x01-\x09\x0b\x0c\x0d-\x7f]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF]))))*(((\x20|\x09)*(\x0d\x0a))?(\x20|\x09)+)?(\x22)))@((([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])*([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])))\.)+(([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])*([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])))\.?$/i;
		return this.optional(element) || (email.test(value));
	}, "邮箱格式不正确");

    jQuery.validator.addMethod("passwordInput", function(value, element) {
        return this.optional(element) || (getStrength(value) >= 2);
    }, "6到20位英文字母、数字、字符组成,至少包含其中两种");

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
  	
  	
	$("#noRegister").click();

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

//切换是否注册
function changeGenearteStyle(isR){
 	if(isR=='noRegister'){
 		$("#info").html("为您创建一个新账号，并与${comFrom }[<shiro:principal property="username"/>]绑定。");
 		$("#butt").val("创建并绑定账号");
		$(".noRegister").show();	    		
		$(".hasRegister").hide();
		$("#noRegister_l").addClass("bigger");
		$("#hasRegister_l").removeClass("bigger");
 	}else {
 		$("#info").html("请查询注册过的账号，并与${comFrom }[<shiro:principal property="username"/>]绑定。");
 		$("#butt").val("绑定账号");
		$(".noRegister").hide();	    		
		$(".hasRegister").show();	    		
		$("#hasRegister_l").addClass("bigger");
		$("#noRegister_l").removeClass("bigger");
 	}
 	$("#connectType").val(isR);
 }

//点击按钮，发送验证码
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
                if(ss.code == 'fail'){
                    $.MsgBox.Alert("温馨提示", decodeURIComponent(ss.msg));
                }
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
					//form.submit();
					//验证通过之后，在提交表单之前，先去注册
					submitForm(form);
				}
			}else if(type == "phone"){
				if(json.result == "codeIsNull"){
					validator.showErrors({"inputCode_phone": "请获取验证码!"});
				}else if(json.result == "diffrent"){
					validator.showErrors({"inputCode_phone": "验证码不正确!"});
				}else if(validator.form()){
					//form.submit();
					submitForm(form);
				}
			}
		},
	 	error: function() {
	 		$("#errorDiv").show("slow");
	    }
	});
}
//提交表单
function submitForm(conForm){
 	var isR = $("#connectType").val();
 	//没有注册过账号
 	if(isR=='noRegister'){
 		var username = $("#username").val();
 		var password_re = $("#password_re").val();
 		password_re = hex_md5(password_re);  //对密码进行md5加密
 		var regtype = $("#regtype").val();  //注册方式
 		var user = {};
 		if(regtype == "phone"){
 			var phoneVal = $("#phone").val();
 			user = {"username":username,"phone":phoneVal,"password":password_re,"registerType":regtype};
 		}else if(regtype == "email"){
 			var emailVal = $("#email").val();
 			user = {"username":username,"email":emailVal,"password":password_re,"registerType":regtype};
 		}
 		$.ajax({
 			async:false,
 			type:"post",
 			url:"${ctx }/user/connection/register",
 			data:user,
 			success: function(msg){
 				var json = eval('(' + msg + ')');
 				var issuccess = json.success;
 				if(issuccess){   //注册成功
 					var password_re = $("#password_re").val();
 			 		password_re = hex_md5(password_re);  //对密码进行md5加密
 					$("#password_re").val(password_re);
 					conForm.submit();   //提交表单
 				}else{  //注册失败
 					for(var key in json){   //相应的地方给出错误提示信息
 						var value = decodeURI(json[key]);
 						$("#"+key).parent().append('<span for="'+key+'" class="error" style="display: inline;">'+value+'</span>');
 					}
 				}
 			},
 			error: function(){
 				$("#failInfo").html('系统正忙，请稍后重试');
 				$("#failInfo").show();
 			}
 		});
 	}else {
 		var password = hex_md5($("#password").val());  //对密码进行md5加密
 		$.ajax({
 			async:false,
 			type:"post",
 			url:"${ctx }/user/connection/doConnection?password=" + password + "&identity=" + $("#identity").val(),
 			success: function(msg){
 				var json = eval('(' + msg + ')');
 				if(json.result == 'false'){
 					$("#failInfo").hide(100);
 					$("#failInfo").show(200);
 				}else {
 					//TODO:sso重定向
 					location.href = "${ctx }/user/connection/connectionSucess?previousAvatar=" + json.previousAvatar
 									+ "&previousNickname=" + json.previousNickname + "&comFrom=" + json.comFrom ;
 				}
 			},
 			error: function(){
 				$("#failInfo").html('系统正忙，请稍后重试');
 				$("#failInfo").show();
 			}
 		});
 	}
}
</script>
</head>
<body>
	<div class="menu">
		<ul>
			<li class="checked">
				<a id="no_decoration" href="#">
			  		账号绑定
				</a>
			</li>
		</ul>
	</div>
	<div class="container_s">
		<%-- <div style="margin-bottom: 30px;">
			<img src="${ctx}/static/images/login/logo.png" style="margin: 20px 10px;">
		</div> --%>
		<div class="content_bindUser" style="" >
			<div>
		  		<input id="noRegister" type="radio" name="choose" checked="checked" onclick="changeGenearteStyle('noRegister');" style="margin-top: 0;"/>
		  		<label id="noRegister_l" class="bigger" for="noRegister" style="display:inline;">还没有注册过账号</label>
		  		
		  		<input id="hasRegister" type="radio" name="choose" onclick="changeGenearteStyle('hasRegister');" style="margin-left: 50px;margin-top: 0;"/>
		  		<label id="hasRegister_l" for="hasRegister" style="display:inline;">已经注册过账号</label>
			</div>
			<div class="alert alert-success" style="width: 50%;text-align: center;margin: 10px auto 0 auto;">
			  		<font id="info">
			  			为您创建一个新账号，并与${comFrom }:<shiro:principal property="username"/> 绑定。
			  		</font>
			  		<br>
			  		<font id="failInfo" style="color: red;display:none;">
			  			对不起,没有查询到相关的账号,请重新绑定。
			  		</font>
			</div>
			<form id="connectForm" action="${ctx}/user/connection/generateUser" method="post" >
				<input id="regtype" type="hidden" value="email" />
				<input id="connectType" type="hidden" value="noRegister">
				<table style="border: 0px;" class="table">
					<tr class="noRegister">
						<td style="text-align:right;">
				     		用户名：
						</td>
						<td>
				     		<input type="text" id="username" name="username" value="<shiro:principal property="nickname"/>" maxlength="32" class="isUser soleUsername soleUsernameForm text" placeholder='请输入用户名' required autofocus>     
						</td>
					</tr>

					<tr class="noRegister tr_email" >
						<td style="text-align:right;">
				     		邮箱：
						</td>
						<td>
				     		<input name="email" id="email" class="isEmail soleEmail text" type="text" placeholder='请输入邮箱' required>    
						</td>
					</tr>
					<tr class="noRegister tr_email">
						<td style="text-align:right">
					     	验证码：
						</td>
						<td>
							<button id="button_email" class="btn btn-success" type="button" onclick="sendValidateCode('email','connectForm')">获取邮箱验证码</button>
					   		<input id="inputCode_email" name="inputCode_email" style="width:85px;margin-bottom: 0;" type="text" placeholder='验证码' required>  
						</td>
					</tr>
					<tr class="noRegister">
						<td style="text-align:right;">
				     		密码：
						</td>
						<td>
				     		<input type="password" id="password_re" name="password_re" class="text passwordInput" rangelength="6,20" maxlength="32" placeholder='请输入密码' required>
						</td>
					</tr>
					<tr class="hasRegister">
						<td style="text-align:right;">
				     		登录账号：
						</td>
						<td>
				     		<input type="text" id="identity" name="identity" class="existIdentity text" placeholder='请输入用户名/邮箱' required autofocus>
						</td>
					</tr>
					<tr class="hasRegister">
						<td style="text-align:right">
				     		密码：
						</td>
						<td>
				     		<input type="password" id="password" name="password" class="text" maxlength="16" placeholder='请输入密码' required>  
						</td>
					</tr>
					<tr>
						<td  style="text-align:right;width:33%">
						</td>
						<td>
							<input value="创建并绑定账号" type="submit" id="butt" class="btn btn-success" style="width: 260px;font-size: 1.1em;">
						</td>
					</tr>
				</table>
			</form>
		</div>
	</div>
</body>
</html>