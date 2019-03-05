<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html>
	<head>
	    <title>首页</title>
	    <meta charset="utf-8">
	    <meta http-equiv="X-UA-Compatible" content="IE=edge">
	       <meta property="qc:admins" content="324375102745346657112347546375" />
	    <script>
			// 激活菜单
			$(document).ready(function() {
			    $("#homepage_tab").addClass("active");
			});
		</script>
	</head>
	<body>
		<div style="height: 200px;">
			<div class="alert alert-success" style="text-align: center;margin: 50px 10px;padding:50px 0;">
			  <h4>欢迎您，<shiro:principal property="realname"/>!</h4>
			  <br>
			 
			  	A man is not old as long as he is seeking something. A man is not old until regrets take the place of dreams. (J. Barrymore)
			  <br>
			  <br>
			  	只要一个人还有追求，他就没有老。直到后悔取代了梦想，一个人才算老。（巴里摩尔）
			</div>
		</div>
	</body>
</html>