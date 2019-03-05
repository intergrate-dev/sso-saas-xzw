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
		<script type="text/javascript" src="${ctx}/static/app/jquery.similar.msgbox.js"></script>
		<script type="text/javascript">
	    	$(document).ready(function(){
	    		
		    	//验证表单是否正确
		    	$("#findpwdForm").validate({ 
		    		//在失去焦点时验证
		         	onfocusout: function(element){
		         		$(element).valid();
		            }
		         });
		    	<%--<c:if test="${registerByMobileEnabled==1}">
					changeType('phone','email');
				</c:if>
		    	<c:if test="${byType=='email' }">
					changeType('email','phone');
				</c:if>--%>
		    });
	    	
	    	function sendCode(){
	    		$("#sendCodeButton").attr("disable",true);
	    		var typeValue="邮箱", byType = "email";
           		var value = $("#"+byType).val();
           		var validator = $("#findpwdForm").validate();
           		
           		if(validator.valid()){
	           		$.ajax({
	           			async: false,
	           			type: "post",
	           			url: "${ctx}/user/identities/isLegal?value=" + value + "&field=" + byType + "&random=" + new Date().getTime() , 
		    			success: function(msg){
		    				var json = eval('(' + msg + ')');
		    				//返回true 表示此凭证没有被注册，非法；false表示已被注册，合法
		    				if(json.result=='true'){
								validator.showErrors({"email":"该"+typeValue+"还未注册"});
		    					return false;
		    				}else {
		    					//发送验证码
		    					$.ajax({ 
		    		    			async: false,
		    		    			type: "post", 
		    		    			url: "${ctx}/user/sendCode?type=" + byType + "&value=" + $("#"+byType).val() + "&useType=1&random=" + new Date().getTime() ,
		    		    			success: function(msg){
                                        var ss = JSON.parse(msg);
                                        /*if(ss.code == 'fail'){
                                            $.MsgBox.Alert("温馨提示", decodeURIComponent(ss.msg));
                                            return;
                                        }*/
                                        $.MsgBox.Alert("温馨提示", decodeURIComponent(ss.msg));
				    					$("#findpwdForm").submit();
		    						},
		    					 	error: function() {
// 		    					 		$("#errorDiv").show("slow");
		    					    }
		    		    		});
		    				}
						},
					 	error: function() {
// 					 		$("#errorDiv").show("slow");
					    }
	           		});
           		}
	    	}
	    	
		</script>
	</head>
	<body>
		<div class="wrapper">
			<%@include file="common_findpwd.jsp" %>
			<%--<div class="nav">
				<a class="echecked" id="Aemail" onclick="changeType('email','phone')">通过邮箱找回密码</a>
			</div>--%>
			<div class="center">
				<img class="_email" src="${ctx}/static/images/findpwd/email_s1.png">
			</div>
			<form id="findpwdForm" action="${ctx }/user/password/find/inputCode" method="post">
				<div class="inputc">
					<input id="byType" name="byType" type="hidden" value="email">
					<span class="_email">
						邮 箱： 
						<input id="email" class="email text250" style="height: 25px;" name="email" type='text'placeholder="请输入邮箱" required>
					</span>
				</div>
				<div class="buttons top50">
					<input id="sendCodeButton" type="button" value="发送验证码 " class="btn btn-warning" style="width: 150px" onclick="sendCode();"/> 
				</div>
			</form>
		</div>
	</body>
</html>