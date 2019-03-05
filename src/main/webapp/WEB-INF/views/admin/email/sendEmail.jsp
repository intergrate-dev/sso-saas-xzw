<!DOCTYPE html>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
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
	    <style type="text/css">
	    	.heightTable40 tr {
	    		height: 40px;
	    	}
	    </style>
	</head>
	<body>
		<tags:blockDiv type="success" hide="true" info="操作成功！"/>
		<tags:blockDiv type="error" hide="true" info="操作失败，请稍后重试！"/>
		<tags:blockDiv type="block" hide="true" info="原始密码输入错误,请重新输入！"/>
		<div class="row">
		    <div class="span11">
				<ul class="nav nav-pills">
					<li class="active">
					  <a href="${ctx }/admin/initSendEmail">发送邮件/通知</a>
					</li>
					<li><a href="${ctx }/admin/initSetEmail">发送账号设置</a></li>
				</ul>
		    </div>
	    </div>
		<form id="">
			<table class="heightTable40 table table-bordered table-condensed">
				<tr>
					<td style="text-align:right;width: 20%">
			     		发送账号：
					</td>
					<td>
			     		zhangmichao@163.com     
					</td>
				</tr>
				<tr>
					<td style="text-align:right">
			     		收件人：
					</td>
					<td>
			     		张三；李四  
					</td>
				</tr>
				<tr>
					<td style="text-align:right;width: 20%">
			     		主题：
					</td>
					<td>
			     		<input id="oldPassword" type="text" name="oldPassword"  placeholder='请输入主题' required autofocus>     
					</td>
				</tr>
				<tr>
					<td style="text-align:right">
			     		选择内容模板：
					</td>
					<td>
			     		<select>
			     			<option>中奖通知</option>
			     			<option>积分兑换通知</option>
			     		</select>  
			     		<a style="cursor: pointer;">
			     			模板预览
			     		</a>
			     		&nbsp;
			     		<a style="cursor: pointer;">
			     			模板管理
			     		</a>
					</td>
				</tr>
				<tr>
					<td style="text-align:right">
			     		选择附件：
					</td>
					<td>
			     		  
					</td>
				</tr>
			</table>
			<button class="btn btn-primary offset4" type="submit">发 送</button>
		</form>
	</body>
</html>