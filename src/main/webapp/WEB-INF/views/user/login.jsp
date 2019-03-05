<!DOCTYPE html>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="org.apache.shiro.web.filter.authc.FormAuthenticationFilter"%>
<%@page import="com.founder.sso.entity.LoginInfo"%>
<%@page import="org.apache.shiro.SecurityUtils"%>
<%@page import="com.founder.sso.entity.User"%>
<%@ page import="java.net.URLDecoder" %>
<%@ page import="com.founder.sso.service.oauth.entity.SystemConfig" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<% 
	response.setHeader("Cache-Control","no-store");
	response.setHeader("Pragrma","no-cache");
	response.setDateHeader("Expires",0);
	Object attribute = request.getSession().getAttribute("login_status");
	String status = attribute != null ? (String) attribute : "";
	String fbAppID = (String) request.getSession().getAttribute("fb_appid");
	String googleClientId = (String) request.getSession().getAttribute("gp_clientid");
	String twitterAppID = (String) request.getSession().getAttribute("twitter_appid");
	String wechatAppID = (String) request.getSession().getAttribute("wechat_appid");

	//记住密码
	String password="";
	String username="";
	String checked="";
	Cookie[] cookies = request.getCookies();
	for(int i = 0; cookies != null && i < cookies.length;i++){
		Cookie cookie = cookies[i];
		if(cookie != null && "username".equals(cookie.getName())){
			username = URLDecoder.decode(cookie.getValue(), "UTF-8");
		}
		if(cookie != null && "password".equals(cookie.getName())){
			password = cookie.getValue();
		}
		if(cookie != null && "checked".equals(cookie.getName())){
			checked = cookie.getValue();
		}
	}
%>
<html>
<head>
	<meta name="renderer" content="webkit">
	<%--<meta name="google-site-verification" content="HYsVXRiOPaIrFCSCpo5m4Pf_lBlrJR4VXeo9evXO3Ao"/>--%>
	<meta name="google-site-verification" content="izWGVNJrRLvSQS5hb7MQZIgDHlIAnuHj4rVlI9srkaM" />
    <title>用户登录</title>
	<link rel="stylesheet" type="text/css" href="${ctx}/static/app/css/basic.css"/>
    <script type="text/javascript" src="${ctx}/static/js/md5.js"></script>
	<script type="text/javascript" src="${ctx}/static/js/thirdAuthLogin.js"></script>
	<script type="text/javascript" src="${ctx}/static/js/Base64.js"></script>
	<script type="text/javascript" src="${ctx}/static/js/default.js"></script>
	<script type="text/javascript" src="${ctx}/static/app/jquery.similar.msgbox.js"></script>
	<script type="text/javascript" src="${ctx}/static/app/js/crypto-sha1-hmac.js"></script>

    <script>

	</script>
    <script type="text/javascript">
	    $(document).ready(function(){
            if($('#checked').val() == 'true'){
                $("input:checkbox").prop("checked","checked");
            }

            $("#dl").click(function () {
                submitFormLogin(); //提交表单
            });
            $("#zx").click(function () {
                var email = $('#username').val();
                if(!email)return;
                $.ajax({
                    type: 'POST',
                    url: "${ctx}/user/deleteUser?email=" + email,
					//data: {email: encodeURIComponent(email)},
                    async: false,
                    contentType: "application/json",
                    success: function (res) {
                        try {
                            var ressult = JSON.parse(res);
                            if (ressult || ressult.code){
								alert("注销成功！");
                            } else {
                                alert("注销失败！");
                            }
                        } catch (e) {
                            alert("注销失败！");
                        }
                    },
                    error: function (e) {
                        alert("请求失败！");
                    }
                });
            });
	    	//隐藏错误信息
		    hideError();

		    //第三方平台登录
			var status = '<%=status %>', fbAppID = '<%=fbAppID %>', googleClientId = '<%=googleClientId %>',
                twitterAppID = '<%=twitterAppID %>', wechatAppID = '<%=wechatAppID %>',
				config = {contextPath: '${ctx}', status:status, fbAppID: fbAppID, googleClientId: googleClientId,
					twitterAppID: twitterAppID, wechatAppID: wechatAppID};
            loadThirdJSSDK(config, true);

	    });

        //删除用户
        /*function deleteUser(userId){
            hideBlockDiv();
            if(confirm("删除用户将不可恢复，您确定执行此操作？")){
                //重定向查询用户列表时带上查询的参数
                location.href= "${ctx}/admin/account/" + userId + "/delete";
            }
        }*/

	    function submitFormLogin(){
	    	//调用securityCode.jsp里面的方法，比对验证码
	    	if(checkValidateCode1()){
	    	    //用户存在？
                checkUserExit("${ctx}", $("#username").val(), function (res) {
					if(res == null) {
                        $.MsgBox.Alert("温馨提示", "请求失败！");
                        return;
                    }
                    if(!res.code) {
                        $.MsgBox.Alert("温馨提示", "登录失败，该用户不存在！");
                        return;
                    }

                    $('#checked').val($('#rememberMe').is(':checked'));
                    if($("#username").val()==''||$("#username").val()=='请输入用户名'){
                        $($("#username").parent().find('.erroMsg')[0]).html('<img src="${ctx}/static/app/img/42.png" >账号不能为空!');
                        return false;
                    }else if($("#password").val()==''){
                        $($("#password").parent().find('.erroMsg')[1]).html('<img src="${ctx}/static/app/img/42.png" >密码不能为空!');
                        return false;
                    }else {
						/*var password = hex_md5($("#password").val());  //对密码进行加密
						 $("#password").val(password);
						 console.log("login.jsp password: " + password + ", username: " + $("#username").val())
						 $("#loginForm").submit();*/

                        var password = null;
                        if($("#password").val().length < 32){
                            password = hex_md5($("#password").val());  //勿重复加密
                        } else{
                            password = $("#password").val();
                        }
                        $("#password").val(password);
                        console.log("login.jsp password: " + password + ", username: " + $("#username").val())
                        $("#loginForm").submit();
                        //return true;
                    }
                })
	    	}else {
                var password = null;
                if($("#password").val().length < 32){
                    password = hex_md5($("#password").val());  //勿重复加密
                } else{
                    password = $("#password").val();
                }
                $("#password").val(password);
	    		return false;
	    	}
	    }


		//判断浏览器是否支持 placeholder属性
		function isPlaceholder(){
			var input = document.createElement('input');
			return 'placeholder' in input;
		}
		
		//不支持placeholder 用jquery来完成
		if (!isPlaceholder()) {
			$(document).ready(function() {
			    if(!isPlaceholder()){
			        $("input").not("input[type='password']").each(//把input绑定事件 排除password框
			            function(){
			                if($(this).val()=="" && $(this).attr("placeholder")!=""){
			                    $(this).val($(this).attr("placeholder"));
			                    $(this).focus(function(){
			                        if($(this).val()==$(this).attr("placeholder")) $(this).val("");
			                    });
			                    $(this).blur(function(){
			                        if($(this).val()=="") $(this).val($(this).attr("placeholder"));
			                    });
			                }
			        });
			        //对password框的特殊处理1.创建一个text框 2获取焦点和失去焦点的时候切换
			        var pwdField	= $("input[type=password]");
			        var pwdVal		= pwdField.attr('placeholder');
			        pwdField.after('<input id="pwdPlaceholder" type="text" class="text form-control placeholder" name="password"  value='+pwdVal+' autocomplete="off" />');
			        var pwdPlaceholder = $('#pwdPlaceholder');
			        pwdPlaceholder.show();
			        pwdField.hide();
			        
			        pwdPlaceholder.focus(function(){
			        	pwdPlaceholder.hide();
			        	pwdField.show();
			        	pwdField.focus();
			        });
			        
			        pwdField.blur(function(){
			        	if(pwdField.val() == '') {
			        		pwdPlaceholder.show();
			        		pwdField.hide();
			        	}
			        });
			    }
			});
		}

        function hideError(){
            $("#errorInfo").hide();
        }
    </script>
    <style type="text/css">
		
		.container_s{
			width: 100%;
			height: 580px;
		} 
		
		.content{
			margin-top:70px;
			/* margin-left:500px; */
			margin-left:auto;
			margin-right:auto;
			height: 80%;
			width: 373px;
			text-align: center; 
		}
		.logo{
			text-align: left; 
		}
		input.text{
			width: 330px;
			height: 25px;
			/*margin-top:20px;*/

		}
		.login-content{
			width: 350px;
			margin-right: auto;
			margin-left: auto;
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
		a{
			margin: 10px 5px;
			font-size: 1.1em;
			color: white;
			text-decoration: underline;
		}
		a:hover   {color:red;}
		.securityCode a:hover   {color:red;}
		.left a:hover   {color:red;}
		a:visited {color:white;}
		a:active  {color:yellow;}
		img.left{
			margin-left: 50px;
		}
		.content_left{
			margin-left: 20px;
		}
	</style>
    
</head>
<body>
	<div class="container_s">
		<div class="content">
			<div class="logo">
				<img src="${ctx}/static/images/login/logo.png" style="margin: 20px 10px;">
			</div>
			<div class="left content_left">
				<form id="loginForm" action="${ctx }/user/login" class='form-signin' method="post" >
					<div class="login-content">
						<c:if test="${username!=null}">
							<div class="alert alert-error input-medium controls"style="width:90px;text-align: left;">
							    <button class="close" data-dismiss="alert">×</button>登录失败，请重试.
							</div>
						</c:if>
						<div style="margin-top: 20px;">
							<input class="text form-control placeholder" id="username" name="username" value="<%=username%>" type='text'placeholder="请输入用户名" required autofocus>
							<div class="erroMsg"></div>
						</div>
						<div style="margin-top: 20px;">
							<input class="text form-control"  id="password" name="password" type='password' placeholder="请输入密码" value="<%=password%>" required>
							<div class="erroMsg"></div>
						</div>
						<div class="securityCode" style="text-align: left;width: 100%; padding-left: 5px;margin-top: 15px;">
							<%@include file="/static/common/securityCode.jsp" %>
						</div>
						<div class="erroMsg"></div>
						<%--<div style="height: 30px">
							<a href="${ctx }/user/password/find" style="float:right;margin:2px 5px;">忘记密码?</a>
						</div>--%>

						<%--<input type="submit" class="loginbtn" value="登 录" style="color:white;font-size:1.2em;font-weight:bold;text-shadow:none;width:350px;height:40px;pointer;background-color:#EB6100;border-color:#EB6100 ;background-image:none; ">--%>
						<div class="regInput certainBtn" style="margin: 2rem 0;">
							<input id="dl" type="button" value="登录" style="border-radius: 6px;height: 3.3rem;letter-spacing: 23px;">
						</div>

						<%--<div class="regInput certainBtn" style="margin: 1rem 0;">
							<input id="zx" type="button" value="注销" style="border-radius: 6px;height: 3.3rem;letter-spacing: 23px;">
						</div>--%>

						<div>
							<div class="left">
								没有账号？
								<a href="${ctx }/user/register?siteId=${sessionScope.siteId} " style="margin-left:0;">立即注册</a>
								<a href="${ctx }/user/password/find" style="margin-left: 15px;">忘记密码</a>
							</div>

							<%--<div class="right">
								<a href="${ctx }/user/password/find" style="margin:2px 5px;">忘记密码?</a>
							</div>--%>

							<div class="right checkbox" style="margin: 10px 10px;">
								<label><input id="rememberMe" type='checkbox' style="float:right;margin:2px 5px;">记住我</label>
								<input class="text form-control"  id="checked" name="checked" value="<%=checked%>" type='hidden'/>
							</div>
						</div>
						
						<%@include file="othersLogin.jsp" %>
					</div>
				</form>
			</div>
		</div>	
	</div>
</body>
</html>