<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="com.founder.sso.admin.entity.Roles"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
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
			    $("#oAuth_tab").addClass("active");
	    	});
	    	
	    	function saveOauthConfig(){
	    		//当开发者Id或者开发者密钥有一个为空时，都不能被启用
	    		if($("#using").is(":checked") && ($("#appId").val()=="" || $("#secretKey").val()=="")){
	    			hideBlockDiv();
	    			$("#blockDiv").show("slow");
	    			return false;
	    		}
	    		$.ajax({ 
	    			async:false,
	    			type: "post", 
	    			url: "${ctx}/admin/updateOauthConfig", 
	    			data: $('#updateOauthConfigForm').serialize(),//提交表单，相当于CheckCorpID.ashx?ID=XXX 
	    			success: function(msg){
	    				hideBlockDiv();
// 	    				$(".btn").prop("disabled","disabled"); 
						$("#successDiv").show("slow");
					},
				 	error: function() {
				 		hideBlockDiv();
				 		$("#errorDiv").show("slow");
				    }
	    		});
	    	}
	    </script>
	</head>
	<body>
		<tags:blockDiv type="success" hide="true" info="操作成功！"/>
		<tags:blockDiv type="error" hide="true" info="操作失败，请稍后重试！"/>
		<tags:blockDiv type="block" hide="true" info="开发者Id或密钥为空时，不能被启用！"/>
		<ul class="breadcrumb">
		  <li><a href="${ctx }/admin/findAllOauthConfig">第三方登录</a> <span class="divider">/</span></li>
		  <li class="active"><a href="#">修改配置信息</a></li>
		</ul>
		<form id="updateOauthConfigForm" class='form-signin' method="post" >
			<input type="hidden" name="id" value="${oauthConfig.id}">  
			<table class="table table-bordered table-condensed">
				<tr>
					<td style="text-align:right;" >
			     		名称：
					</td>
					<td>
						${oauthConfig.providerName}
					</td>
				</tr>
				<tr>
					<td style="text-align:right">
			     		开发者Id：
					</td>
					<td>
			     		<input type="text" id="appId" name="appId" value="${oauthConfig.appId}"> 
					</td>
				</tr>
				<tr>
					<td style="text-align:right">
			     		开发者密钥：
					</td>
					<td>
			     		<input type="text" id="secretKey" name="secretKey" value="${oauthConfig.secretKey}"style="width: 60%;"> 
					</td>
				</tr>
				<tr>
					<td style="text-align:right;width: 20%">
			     		说明信息：
					</td>
					<td>
						<input type="text" name="description" value="${oauthConfig.description}" style="width: 60%;"/> 
					</td>
				</tr>
				<tr>
					<td style="text-align:right;width: 20%">
			     		OAuth版本：
					</td>
					<td>
						${oauthConfig.oauthVersion}
					</td>
				</tr>
				<tr>
					<td style="text-align:right">
			     		是否启用：
					</td>
					<td>
						<input id="using" type="radio" name="enabled" value="true" <c:if test="${oauthConfig.enabled}">checked</c:if>> 
						<label for="using" style="display:inline;">启用</label>
						&nbsp;&nbsp;
						<input id="unUsing" type="radio" name="enabled" value="false"<c:if test="${!oauthConfig.enabled}">checked</c:if>> 
						<label for="unUsing"style="display:inline;">不启用</label>
					</td>
				</tr>
			</table>
			<button class="btn btn-primary offset4" type="button" onclick="saveOauthConfig()">保存</button>
			<button class="btn btn-primary" type="reset">重置</button>
		</form>
	</body>
</html>