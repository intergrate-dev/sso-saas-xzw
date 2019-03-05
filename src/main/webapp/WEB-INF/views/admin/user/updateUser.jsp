<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix='fmt' uri="http://java.sun.com/jsp/jstl/fmt" %> 
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html>
	<head>
	    <title></title>
	    <meta charset="utf-8">
	    <meta http-equiv="X-UA-Compatible" content="IE=edge">
	<%--     <script type="text/javascript">
			//调用省市级联的方法
			$(function(){
				$("#users_tab").addClass("active");
				if('${user.livedProvince }'==''){
					$("#province").cityCasCade("#city").cityCasCade('select','选择省','选择市');
				}else {
					$("#province").cityCasCade("#city").cityCasCade('select','${user.livedProvince }','${user.livedCity }');
				}
			});
			
	    </script>   --%>
	</head>
	<body>
		<tags:blockDiv type="success" hide="true" info="修改成功！"/>
		<tags:blockDiv type="error" hide="true" info="修改失败，请稍后重试！"/>
		<div class="row">
		    <div class="span11">
				<ul class="nav nav-pills">
					<li class="active">
					  <a href="${ctx }/admin/profile?userId=${user.id}">基本资料</a>
					</li>
					<li><a href="${ctx }/admin/face?userId=${user.id}">用户头像</a></li>
				</ul>
		    </div>
		    <div class="span1" style="margin-top:25px;">
<%-- 			    <a href="${ctx }/admin/profile?userId=${user.id}" style="cursor:pointer;color:#006000;font-size: 12px;">预 览</a> --%>
		    </div> 
	    </div>
	    <form id="updateProfileForm" action="${ctx}/admin/updateProfile">
	   		<input type="hidden" name="id" value="${user.id}">  
			<table class="table table-bordered table-condensed">
				<tr>
					<td style="text-align:right;width: 20%">
			     		用户名：
					</td>
					<td>
			     		${user.username }  
					</td>
				</tr>

				<tr>
					<td style="text-align:right">
			     		邮箱：
					</td>
					<td>
						<c:choose>
							<c:when test="${user.email=='' || user.email==null}">
								未填写
							</c:when>
							<c:otherwise>
								${user.email}
							</c:otherwise>
						</c:choose>
					</td>
				</tr>
				<tr>
					<td style="text-align:right;width: 20%">
			     		昵称：
					</td>
					<td>
			     		<input name="nickname" value="${user.nickname }" type="text" required="required" maxlength="36" placeholder='请输入昵称'/>  
					</td>
				</tr>
			</table>
			<button class="btn btn-primary offset4" style="width: 100px;" type="submit"> 保 存 </button>
			<input class="btn btn-primary" style="width: 80px;margin-left: 50px;" value="返 回 " onclick="location.href='${ctx}/admin/profile?userId=${user.id }'"/> 
		</form>
	<%-- 	<script type="text/javascript">
		    $('#birthdayPicker').datetimepicker({
				format: 'yyyy-MM-dd',
				language: 'en',
				pickDate: true,
				pickTime: true,
				hourStep: 1,
				minuteStep: 15,
				secondStep: 30,
				inputMask: true
			});
	    </script>  --%>
	</body>
</html>