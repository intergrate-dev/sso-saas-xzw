<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix='fmt' uri="http://java.sun.com/jsp/jstl/fmt" %> 
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html>
<head>
	<title>用户资料</title>
	<script type="text/javascript">
		//激活菜单
		$(document).ready(function() {
			$("#users_tab").addClass("active");
		});
	</script>
</head>
<body>
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
		    <a href="initUpdateProfile?userId=${user.id}" style="cursor:pointer;color:#006000;font-size: 12px;">修 改</a>
	    </div> 
    </div>
	<table class="table table-bordered table-condensed">
		<tr>
			<td style="text-align:right;width: 20%">
	     		用户名：
			</td>
			<td>
	     		${user.username} 
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
	     		${user.nickname } 
			</td>
		</tr>
		
		<%-- <tr>
			<td style="text-align:right">
	     		性别：
			</td>
			<td>
				<c:choose>
					<c:when test="${user.sex=='' || user.sex==null}">
						未填写
					</c:when>
					<c:otherwise>
						${user.sex}
					</c:otherwise>
				</c:choose>
			</td>
		</tr>
		<tr>
			<td style="text-align:right">
	     		生日：
			</td>
			<td>
				<c:choose>
					<c:when test="${user.birthday=='' || user.birthday==null}">
						未填写
					</c:when>
					<c:otherwise>
						<fmt:formatDate value="${user.birthday}" pattern="yyyy-MM-dd"/>
					</c:otherwise>
				</c:choose>
			</td>
		</tr>
		<tr>
			<td style="text-align:right">
	     		血型：
			</td>
			<td>
				<c:choose>
					<c:when test="${user.bloodType=='' || user.bloodType==null}">
						未填写
					</c:when>
					<c:otherwise>
						${user.bloodType}
					</c:otherwise>
				</c:choose>
			</td>
		</tr>
		<tr>
			<td style="text-align:right">
	     		感情状况：
			</td>
			<td>
				<c:choose>
					<c:when test="${user.emotion=='' || user.emotion==null}">
						未填写
					</c:when>
					<c:otherwise>
						${user.emotion}
					</c:otherwise>
				</c:choose>
			</td>
		</tr>
		
		<tr>
			<td style="text-align:right">
	     		现居地：
			</td>
			<td>
				<c:choose>
					<c:when test="${user.livedProvince=='' || user.livedProvince==null || user.livedProvince=='选择省'}">
						未填写
					</c:when>
					<c:otherwise>
						${user.livedProvince}
					</c:otherwise>
				</c:choose>
				<c:choose>
					<c:when test="${user.livedCity=='' || user.livedCity==null || user.livedCity=='选择市'}">
					</c:when>
					<c:otherwise>
						${user.livedCity}
					</c:otherwise>
				</c:choose>
			</td>
		</tr>
		<tr>
			<td style="text-align:right">
	     		职业：
			</td>
			<td>
				<c:choose>
					<c:when test="${user.work=='' || user.work==null}">
						未填写
					</c:when>
					<c:otherwise>
						${user.work}
					</c:otherwise>
				</c:choose>
			</td>
		</tr>
		<tr>
			<td style="text-align:right">
	     		所在公司：
			</td>
			<td>
				<c:choose>
					<c:when test="${user.company=='' || user.company==null}">
						未填写
					</c:when>
					<c:otherwise>
						${user.company}
					</c:otherwise>
				</c:choose>
			</td>
		</tr>
		<tr>
			<td style="text-align:right">
	     		公司地址：
			</td>
			<td>
				<c:choose>
					<c:when test="${user.companyAddress=='' || user.companyAddress==null}">
						未填写
					</c:when>
					<c:otherwise>
						${user.companyAddress}
					</c:otherwise>
				</c:choose>
				
			</td>
		</tr> --%>
	</table>
</body>
</html>