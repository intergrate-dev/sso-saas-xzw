<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="org.apache.shiro.web.filter.authc.FormAuthenticationFilter"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>

<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html>
<head>
    <title>管理员登录</title>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <link rel='stylesheet' href='${ctx}/static/bootstrap2_3_2/css/bootstrap.min.css'>
    <link type="image/x-icon" href="${ctx}/static/images/favicon.ico" rel="shortcut icon">
	<link href="${ctx}/static/bootstrap2_3_2/css/bootstrap.min.css" type="text/css" rel="stylesheet" />
	<link href="${ctx}/static/bootstrap-datetimepicker-0.0.11/css/bootstrap-datetimepicker.min.css" type="text/css" rel="stylesheet" />
	<link href="${ctx}/static/jquery-validation1_11_1/validate.css" type="text/css" rel="stylesheet" />
	<link href="${ctx}/static/styles/default.css" type="text/css" rel="stylesheet" />
    <script src='${ctx}/static/jquery1_11_1/jquery.js'></script>
    <script src='${ctx}/static/bootstrap2_3_2/js/bootstrap.min.js'></script>
     <script type="text/javascript">
	    function submitForm(){
	    	//调用securityCode.jsp里面的方法，比对验证码
	    	if(checkValidateCode()){
	    		$("#loginForm").submit();
	    	}
	    }
    </script>
</head>
<body>
<div class='container ' style='margin-top: 10%; width: 350px;' >
	  <%//已经登录的用户跳转到根目录 %>
	  <shiro:user><%response.sendRedirect(request.getContextPath()+"/admin");%></shiro:user>
	  <%
	  String error = (String) request.getAttribute(FormAuthenticationFilter.DEFAULT_ERROR_KEY_ATTRIBUTE_NAME);
	  if(error != null){
	  %>
	      <div class="alert alert-error input-medium controls">
	          <button class="close" data-dismiss="alert">×</button>登录失败，请重试.
	      </div>
	  <%
	  }
	  %>
      <form id="loginForm" action="${ctx }/admin/login" class='form-signin' role='form' method="post">
        <h4 class='form-signin-heading' >管理员登录</h4>
        <input name="username" type='text' class='form-control' placeholder='请输入用户名' required autofocus>
        <div style='height:10px;clear:both;display:block'></div>
        <input name="password" type='password' class='form-control' placeholder='请输入密码' required>
        <div style='height:10px;clear:both;display:block'></div>
        
<%--         <%@include file="/static/common/securityCode.jsp" %> --%>
        <%--<div class='checkbox'>
          <label>
            <input type='checkbox' value='rememberMe'> 记住登录状态
          </label>
        </div>--%>
        <button class='btn btn-lg btn-primary btn-block' style="width:65%" type="submit">登 录</button>
<!--         <button class='btn btn-lg btn-primary btn-block' style="width:65%" type='button' onclick="submitForm()">登录</button> -->
      </form>
      <i class="icon-user"></i>
      <a href="${ctx }/user/login">会员登陆页面</a>
    </div> 
</body>
</html>