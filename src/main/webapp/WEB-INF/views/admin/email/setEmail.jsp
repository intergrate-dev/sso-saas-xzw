<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html>
	<head>
	    <title></title>
	    <meta charset="utf-8">
	    <meta http-equiv="X-UA-Compatible" content="IE=edge">
	    <script type="text/javascript">
		    $(document).ready(function(){
				//激活菜单
			    $("#email_tab").addClass("active");
	    	});
	    </script>
	</head>
	<body>
		<tags:blockDiv type="success" hide="true" info="操作成功！"/>
		<tags:blockDiv type="error" hide="true" info="操作失败，请稍后重试！"/>
		<tags:blockDiv type="block" hide="true" info="原始密码输入错误,请重新输入！"/>
		<div class="row">
		    <div class="span11">
				<ul class="nav nav-pills">
					<li>
					  <a href="${ctx }/admin/initSendEmail">发送邮件/通知</a>
					</li>
					<li class="active">
						<a href="${ctx }/admin/initSetEmail">发送账号设置</a>
					</li>
				</ul>
		    </div>
	    </div>
		<form id="">
			<table class="table table-bordered table-condensed">
				<tr>
					<td style="text-align:right;width: 20%">
			     		账 号：
					</td>
					<td>
			     		<input id="oldPassword" type="text" name="oldPassword" placeholder="请输入账号"  required autofocus>     
			     		<select style="width: 120px;">
			     			<option>@163.com</option>
			     			<option>@126.com</option>
			     			<option>@139.com</option>
			     			<option>@qq.com</option>
			     			<option>@sina.com</option>
			     			<option>@yeah.com</option>
			     			<option>@sohu.com</option>
			     		</select>     
					</td>
				</tr>
				<tr>
					<td style="text-align:right">
			     		密 码：
					</td>
					<td>
			     		<input id="newPassword" type="password" name="newPassword" placeholder="请输入密码" required>  
					</td>
				</tr>
			</table>
			<button class="btn btn-primary offset4" type="submit">保 存</button>
		</form>
	</body>
</html>