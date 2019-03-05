<!DOCTYPE html>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix='fmt' uri="http://java.sun.com/jsp/jstl/fmt" %> 
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<% 
response.setHeader("Cache-Control","no-store"); 
response.setHeader("Pragrma","no-cache"); 
response.setDateHeader("Expires",0); 
%>
<html>
	<head>
	<title>个人资料</title>
			
		<script type="text/javascript">
			
			//调用省市级联的方法
			$(function(){
				 $("#profile_tab").addClass("checked");
			     $("#menu_profile").addClass("checked");
			});
			
			
			$(document).ready(function(){
				$("#profile_tab").addClass("active");

				//检查敏感词
				jQuery.validator.addMethod("isnickname", function(value, element) {
					var result = false;
					if(value!=""){
						$.ajax({ 
			    			async:false,
			    			type: "get", 
			    			url: "${ctx}/user/identities/isSensitiveword?value=" + encodeURIComponent(value) + "&random=" + new Date().getTime() , 
			    			success: function(msg){
			    				var json = eval('(' + msg + ')');
			    				if(json.result=='false'){
			    					result = true;
			    				}
			    				if(json.result=='true'){
			    					result = false;
			    				}
							},
						 	error: function() {
						 		$("#errorDiv").show("slow");
						    }
			    		});
					}
					return result;
				}, "昵称含有敏感词");	
				
				//使用ajax检查邮箱是是否存在
				jQuery.validator.addMethod("soleEmail", function(value, element) {
					var result = true;
					if(value!=""){
						$.ajax({ 
			    			async:false,
			    			type: "post", 
			    			url: "${ctx}/user/identities/isLegal?value=" + value + "&field=email&random=" + new Date().getTime() , 
			    			success: function(msg){
			    				var json = eval('(' + msg + ')');
			    				if(json.result=='false'){
			    					result = false;
			    				}
							},
						 	error: function() {
						 		$("#errorDiv").show("slow");
						    }
			    		});
					}
					return result;
				}, "邮箱已注册");	
				
				
		    	//验证
		    	$("#updateProfileForm").validate({ 
		    		//在失去焦点时验证
		         	onfocusout: function(element){
		                $(element).valid();
		            },
		          	//表单通过验证之后
	            	submitHandler: function(form) {  
	            		//检查输入的验证码是否正确
	            		if($("#update_phone").val() == "yes" && $("#original_phone").val() != $("#phone").val()){
		            		checkValidateCode("phone","updateProfileForm");
	            		}
	            		if($("#update_email").val() == "yes" && $("#original_email").val() != $("#email").val()){
		            		checkValidateCode("email","updateProfileForm");
	            		}
	            		//如果手机号和邮箱验证结果都是正确的
	            		if($("#checkResult_email").val() == "correct"){
	    					form.submit();
	    				}
	            	}
		         });
		    });
			

	    	//检查输入的手机或邮箱验证码是否正确
	    	function checkValidateCode(type,formId){
	    		//如果用户取消了修改，那么没必要继续验证
	    		var inputCode = "inputCode_" + type;
	 			var validator = $("#"+formId).validate();
	    		$.ajax({
	    			async:false,
	    			type:"post",
	    			url:"${ctx}/user/checkCode?type=" + type + "&value=" + $("#email").val() + "&inputCode=" + $("#" + inputCode).val() + "&random=" + new Date().getTime() ,
	    			success: function(msg){
	    				var json = eval('(' + msg + ')');
	    				if(type=="phone"){
		   					if(json.result == "codeIsNull"){
		   						validator.showErrors({"inputCode_phone": "请获取验证码!"});
		   					}else if(json.result == "diffrent"){
		    					validator.showErrors({"inputCode_phone": "验证码不正确!"});
		    				}else {
		    				}
	   					}else{
		   					if(json.result == "codeIsNull"){
		   						$("#checkResult_email").val("incorrect");
		   						validator.showErrors({"inputCode_email": "请获取验证码!"});
		   					}else if(json.result == "diffrent"){
		   						$("#checkResult_email").val("incorrect");
		    					validator.showErrors({"inputCode_email": "验证码不正确!"});
		    				}else {
		    					$("#checkResult_email").val("correct");
		    				}
	   					}
					},
				 	error: function() {
				 		$("#errorDiv").show("slow");
   						$("#checkResult_email").val("incorrect");
				    }
	    		});
	    	}

			//点击按钮， 发送手机或邮箱验证码，但手机、邮箱及验证码的input的id及name应遵循规范
	    	function sendValidateCode(type,formId){
	    		hideBlockDiv();
				//在发送验证码之前，首先检查,如果和原来手机号(邮箱)一致，没必要发送验证码；
				if($("#original_"+type).val() == $("#"+type).val()){
					var info = "邮箱并未更改，不需要发送验证码!";
					$("#blockDiv").show();
					$("#blockDiv").html(info);
					return ;
				}else {
		    		//邮箱验证通过后
			    	if($("#"+formId).validate().element("#" + type)){
						timerEmail();
			    		$.ajax({
			    			async:true,
			    			type: "post", 
			    			url: "${ctx}/user/sendCode?type=" + type + "&value=" + $("#"+type).val() + "&useType=0&random=" + new Date().getTime() ,
			    			success: function(msg){
                                var ss = JSON.parse(msg);
                                if(ss.code == 'success') return;
                                $('#errorInfoSpan').text(ss.msg)
                                $("#errorDiv").show("slow");
                                $(".submit-btn").attr("disabled","disabled");
                                $(".submit-btn").removeAttr("disabled");
							},
						 	error: function() {
						 		$("#errorDiv").show("slow");
						 		$("#button_" + type).attr("disabled",false);
						    }
			    		});
			    	}
				}
	    	}
	    	

	    	//phone和email的计数器要分开写，不然会相互影响
	    	var timePhone = 60;
	    	function timerPhone(){
	    		var butt = $("#button_phone");
	    		if(timePhone<=0){
	    			butt.attr("disabled",false);
	    			butt.html("重新发送");
	    			timePhone = 60;
	    		}else {
	    			butt.attr("disabled",true);
		    		window.setTimeout("timerPhone()",1000); 
		    		butt.html(timePhone+"秒后重新发送");
		    		timePhone--;
	    		}
	    	}
	    	
	    	var timeEmail = 60;
	    	function timerEmail(){
	    		var butt = $("#button_email");
	    		if(timeEmail<=0){
	    			butt.attr("disabled",false);
	    			butt.html("重新发送");
	    			timeEmail = 60;
                    $(".submit-btn").removeAttr("disabled");
                }else {
	    			butt.attr("disabled",true);
		    		window.setTimeout("timerEmail()",1000); 
		    		butt.html(timeEmail+"秒后重新发送");
		    		timeEmail--;
	    		}
	    	}
	    	
	    	
	    	function showInput(type){
	    		$("#update_"+type).val("yes");
	    		$(".input_"+type).show();		
	    		$(".info_"+type).hide();		
	    	}
	    	function hideInput(type){
	    		hideBlockDiv();
	    		$("#checkResult_"+type).val("correct");
	    		$("#update_"+type).val("no");
	    		$(".input_"+type).hide();		
	    		$(".info_"+type).show();		
	    	}
		</script>
		<style type="text/css">
			.input_phone{
				display: none;
			}
			.input_email{
				display: none;
			}
			.heightTable40 tr {
	    		height: 40px;
	    	}
		</style>
	</head>
	<body>
		<%@include file="common_profile.jsp" %>
				
		<div class="contend_div">
			<div class="infoDiv">
				<tags:blockDiv type="success" hide="true" info="修改成功！"/>
				<tags:blockDiv type="error" hide="true" info="修改失败，请稍后重试！"/>
				<tags:blockDiv type="block" hide="true" info=""/>
			</div>
			
			<div class="content_info">
			    <input id="checkResult_email" type="hidden" value="correct">
			    <input id="original_phone" type="hidden" value="${user.phone }">
			    <input id="original_email" type="hidden" value="${user.email }">
			    <form id="updateProfileForm" action="${ctx}/user/updateProfile">
				    <input id="update_phone" name="updatePhone" type="hidden" value="no">
				    <input id="update_email" name="updateEmail" type="hidden" value="no">
					<table class="table table-bordered ">
						<tr>
							<td style="text-align:right;width: 20%">
					     		用户名：
							</td>
							<td>
					     		${user.username }  
							</td>
						</tr>
						<tr>
							<td style="text-align:right;width: 20%">
					     		昵称：
							</td>
							<td>
					     		<input name="nickname" value="${user.nickname }" type="text" class="isnickname" required="required" maxlength="12" placeholder='请输入昵称'/>  
							</td>
						</tr>
						<tr>
							<td style="text-align:right">
					     		邮箱：
							</td>
							<td class="info_email">
					     		${user.email }
					     		<a onclick="showInput('email')">更改邮箱</a>  
							</td>
							<td class="input_email">
					     		<input id="email" name="email" value="${user.email }" class="email soleEmail" type="text" placeholder='请输入邮箱'>  
					     		<a onclick="hideInput('email')">取消更改</a>
							</td>
						</tr>
						<tr class="input_email">
							<td style="text-align:right">
					     		邮箱验证码：
							</td>
							<td>
					     		<input id="inputCode_email" name="inputCode_email" style="width:50px;" type="text" maxlength="6" placeholder="验证码">  
					     		<button id="button_email" class="btn btn-primary" type="button" onclick="sendValidateCode('email','updateProfileForm')">获取邮箱验证码</button>
							</td>
						</tr>
					</table>
<!-- 					<button class="btn btn-primary offset4" type="submit">保存</button> -->
						<button class="btn btn-primary submit-btn" style="width: 100px;margin-left: 200px;" type="submit"> 保 存 </button>
						<input type="button" class="btn btn-warning" style="width: 100px;margin-left: 50px;" value="返 回 " onclick="location.href='${ctx}/user/profile'"/> 
						
				</form>
			</div>
		</div>

	</body>
</html>