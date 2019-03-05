<!DOCTYPE html>
<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="org.apache.shiro.web.filter.authc.FormAuthenticationFilter"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags"%>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<% 
response.setHeader("Cache-Control","no-store"); 
response.setHeader("Pragrma","no-cache"); 
response.setDateHeader("Expires",0); 
%>
<html>
<head>
	<meta charset="UTF-8">
	<meta name="keywords" content="" />
	<meta name="description" content="" />
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
	<meta name="viewport" content="initial-scale=1.0,user-scalable=1,maximum-scale=1,width=device-width" />
	<meta name="apple-mobile-web-app-capable" content="yes" />
	<meta name="apple-mobile-web-app-status-bar-style" content="black" />
	
	<title>注册</title>

	<link rel="stylesheet" type="text/css" href="${ctx}/static/app/css/basic.css" />
	<script type="text/javascript" src="${ctx}/static/app/js/jquery-2.2.4.min.js"></script>
	<script type="text/javascript" src="${ctx}/static/app/js/validate.js"></script>
	<script type="text/javascript" src="${ctx}/static/js/md5.js"></script>
	<script type="text/javascript" src="${ctx}/static/app/jquery.similar.msgbox.js"></script>
	<style>
		body{min-height:10%;}
		.certainBtn{margin: 1rem 0;}
	</style>
</head>
<body >
	<div class="wrap">
		<div class="regiCont">
			<form id="registerForm" method="post" action="${ctx }/user/register">
				<input id="registerType" name="registerType" type="hidden" value="phone" />
				<input id="siteId" name="siteId" type="hidden" value="${siteId}" />
				<div class="regInput">
					<input id="phoneOrEmail" name="phone" class="required" type="text"
						value="" placeholder="请输入邮箱" >
					<div class="inputIcon">
						<img src="../static/app/img/43.png">
					</div>
					<div class="erroMsg"></div>
				</div>
				<div class="regInput">
					<input class="identifying_code" type="text" value
						placeholder="请输入验证码" id="input_code" name="input_code">
					<div id="sendCode" class="getVerifi " style="bottom: 1.8rem;cursor: pointer;">点击获取</div>
				</div>
				<div class="regInput">
					<input type="password" id="password_hid" name="password_hid" placeholder="请填写密码（不少于6位）" class="required" >
					<input id="password" name="password" type="hidden"
						class="required" value >
					<div class="inputIcon">
						<img src="../static/app/img/43.png">
					</div>
					<div class="promptMsg"></div>
					<div class="erroMsg"></div>
				</div>
				<div class="regInput ">
					<input id="confirm_password" name="confirm_password"
						type="password" class="required" value placeholder="请确认密码（不少于6位）">
					<div class="inputIcon">
						<img src="../static/app/img/43.png">
					</div>
					<div class="erroMsg"></div>
				</div>
				<%--<div class="securityCode" style="text-align: left;width: 100%; padding-left: 5px;margin-top: 15px;">
					<%@include file="/static/common/securityCode.jsp" %>
				</div>--%>
				<div  class="regInput certainBtn">
					<input id="submitBtn" type="button" value="确定">
				</div>
			</form>
		</div>
	</div>
	<script type="text/javascript">
		var register = {
			init : function() {
				//register.validateForm(); //给表单增加校验
				//发送短信验证码
				$("#sendCode").click(function() {
					register.sendValidateCode();
				});
				$("#submitBtn").click(function() {
                    $("#password").val($("#password_hid").val())
					//if(checkValidateCode1()){
			    		if($("#phoneOrEmail").val()==''||$("#phoneOrEmail").val()=='请输入邮箱'){
					    	alert("邮箱不能为空!");
					    	return false;
			    		} else if($("#password").val()==''){
					    	alert("密码不能为空!"); 
					    	return false;
			    		}
			    	/*}else {
			    		return false;
			    	}*/
					
					register.checkValidateCode();
				});
			},
			checkValidateCode : function() {
				var phoneOrEmail = $("#phoneOrEmail").val(); //邮箱号码
				var registerType = $("#registerType").val(); //注册类型
				var input_code = $("#input_code").val(); //验证码
				$.ajax({
					async : false,
					type : "post",
					url : "${ctx}/user/checkCode?type=" + registerType
							+ "&value=" + phoneOrEmail + "&inputCode="
							+ input_code + "&random=" + new Date().getTime(),
					success : function(msg) {
						//alert(msg);
						var json = eval('(' + msg + ')');
						if (json.result == "codeIsNull") {
							$($(".erroMsg")[2]).html('<img src="../static/app/img/42.png" >请先获取验证码!');
						} else if (json.result == "diffrent") {
							$($(".erroMsg")[2]).html('<img src="../static/app/img/42.png" >验证码不正确!');
						} else if (json.result == "same") {
							var password = hex_md5($("#password_hid").val()); //对密码进行加密
							$("#password").val(password); //赋值给隐藏框，为了避免页面显示时，密码字符串加长
							var len = $("#password_hid").val().length; //未加密的密码的长度
							$("#password_hid").val(password.substring(0, len)); //避免传输明文和字符串加长，将password_hid中的密码进行加密后截取
							$("#registerForm").submit();
						}
					},
					error : function(jqXHR, textStatus, errorThrown) {
						//$("#errorDiv").show("slow");
						$.MsgBox.Alert("温馨提示",
								"checkValidateCode失败：readyState="
										+ jqXHR.readyState + ",status="
										+ jqXHR.status + ",textStatus="
										+ textStatus);
					}
				});

			},


			//填入注册信息，是注册还是邮箱
			isPhoneOrEmail : function() {
				$("#phoneOrEmail").removeClass();
				var phoneOrEmail = $("#phoneOrEmail").val(); //邮箱
				var phoneReg = /^1[34578]\d{9}$/; //正则
				//var emailReg = /^(\w)+(\.\w+)*@(\w)+((\.\w+)+)$/; //邮箱正则
				var emailReg = /^(([a-zA-Z0-9_-])+(.*))@(([a-zA-Z0-9_-])+).(([a-zA-Z0-9_-])+)$/; //邮箱正则
				if (phoneReg.test(phoneOrEmail)) { //
					$("#registerType").attr("value", "phone"); //注册类型
					$("#phoneOrEmail").attr("name", "phone");
					$("#phoneOrEmail").addClass("login-input1 isPhone solePhone");
					$("#phoneOrEmail").parent().find('.erroMsg').html(''); //清空错误信息
					return true;
				} else if (emailReg.test(phoneOrEmail)) { //邮箱
					$("#registerType").attr("value", "email"); //注册类型
					$("#phoneOrEmail").attr("name", "email");
					$("#phoneOrEmail").addClass("login-input1 isEmail soleEmail");
					$("#phoneOrEmail").parent().find('.erroMsg').html('');  //清空错误信息
					return true;
				} else {
					$("#registerType").attr("value", ""); //注册类型
					$("#phoneOrEmail").attr("name", "");
					$("#phoneOrEmail").addClass("login-input1");
					$("#phoneOrEmail").parent().find('.erroMsg').html('<img src="../static/app/img/42.png" >请输入正确邮箱'); //加上错误信息
					return false;
				}
			},
			//发送验证码
			sendValidateCode : function() {
				var isPE = register.isPhoneOrEmail();
				if (!isPE) {
					return false;
				}
				var phoneOrEmail = $("#phoneOrEmail").val(); //邮箱号码
				var registerType = $("#registerType").val(); //注册类型
				//邮箱验证通过后
                $('#sendCode').unbind();
                $('#sendCode').css("cursor", "not-allowed");
				timer();
				$.ajax({
					async : true,
					type : "post",
					url : "${ctx}/user/sendCode?type=" + registerType
							+ "&value=" + phoneOrEmail + "&useType=0&random="
							+ new Date().getTime(),
					success : function(res) {
                        var ss = JSON.parse(res);
                        /*if(data.code == 'fail')
                        	$($(".erroMsg")[0]).html('<img src="../static/app/img/42.png" >' + data.msg);*/
                        $.MsgBox.Alert("温馨提示", decodeURIComponent(ss.msg));
					},
					error : function(jqXHR, textStatus, errorThrown) {
						//$.MsgBox.Alert("温馨提示","网络问题，请稍后再试");
						//$.MsgBox.Alert("温馨提示","sendValidateCode失败：readyState="+jqXHR.readyState+",status="+jqXHR.status+",textStatus="+textStatus);
					}
				});
			}
		};

		//验证码多少秒后重新发送
		var time = 60;
		function timer() {
			var butt = $("#sendCode");
			if (time <= 0) {
				butt.attr("disabled", false);
				butt.text("重新发送");
				time = 60;
                $("#sendCode").click(function() {
                    register.sendValidateCode();
                });
                $('#sendCode').css("cursor", "pointer");
			} else {
				butt.attr("disabled", true);
				window.setTimeout("timer()", 1000);
				butt.text(time + "秒后重发");
				time--;
			}
		};
		$(function() {
			//$('#window_wnd_title', window.parent.document).text("注册");
			register.init();
		});
	</script>
</body>
</html>