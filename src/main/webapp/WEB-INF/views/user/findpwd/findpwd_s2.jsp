<!DOCTYPE html>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="org.apache.shiro.web.filter.authc.FormAuthenticationFilter"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<% 
response.setHeader("Cache-Control","no-store"); 
response.setHeader("Pragrma","no-cache"); 
response.setDateHeader("Expires",0); 
%>
<html>
	<head>
	    <title>找回密码</title>
		<script type="text/javascript">
			$(document).ready(function(){
	    		
		    	//验证表单是否正确
		    	$("#findpwdForm").validate({ 
		    		//在失去焦点时验证
		         	onfocusout: function(element){
		         		$(element).valid();
		            }
		         });
		    });
			
	    	function checkCode(){
	    		$("#sendCodeButton").attr("disable",true);
	    		var type = $("#byType").val();
	    		var inputCode = $("#inputCode").val();
	    		var form = $("#findpwdForm");
	    		var validator = form.validate();
	    		var value =  $("#value").val();
	    		//如果输入的验证码不为空
	    		if(inputCode != null && inputCode != ""){
                    form.submit();
	    		}
	    	}
		</script>
		<style type="text/css">
			a:HOVER {
				text-decoration: none;	
			}
		</style>
	</head>
	<body>
		<div class="wrapper">
			<%@include file="common_findpwd.jsp" %>
			<div class="nav">
				<a class="checked">通过邮箱找回密码</a>
			</div>
			<div class="center">
				<img src="${ctx}/static/images/findpwd/${byType}_s2.png">
			</div>
			<form id="findpwdForm" action="${ctx }/user/password/find/authentication" method="post">
				<input id="byType" name="byType" type="hidden" value="${byType }">
				<input id="value" name="value" type="hidden" value="${value }">
				<div class="infoDiv">
					<%--<div class="info_div_info">
                        验证邮件已经发送到您的邮箱上${value }，
					</div>
					<div class="info_div_info">
						请在下框输入验证码：
					</div>--%>
				</div>
				<div class="inputc2">
					<span>
						<input id="inputCode" name="inputCode" class="text300" style="height: 25px;" type='text' maxlength="6" placeholder="请输入验证码" required>
					</span>
				</div>
				<%--<div class="center top20">
					<a href="${ctx }/user/password/find?byType=${byType}" style="color: white;text-decoration: underline;">
						重新发送验证码
					</a>
				</div>--%>
				<div class="buttons top20" >
					<input id="sendCodeButton" type="button" value="提 交 " class="btn btn-warning" style="width: 150px" onclick="checkCode();"/> 
					<input id="backButton" type="button" value="返 回 " class="btn btn-warning" style="width: 150px" onclick="javascript:history.back(-1);"/>
				</div>
			</form>
		</div>
	</body>
</html>