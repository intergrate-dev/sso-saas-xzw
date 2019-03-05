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
			    $("#system_tab").addClass("active");
	    	});
	    	
	    	function saveSystemConfig(){
	    		//当开发者Id或者开发者密钥有一个为空时，都不能被启用
	    		if($("#sname").val()=="" || $("#sstatus").val()==""){
	    			hideBlockDiv();
	    			$("#blockDiv").show("slow");
	    			return false;
	    		}
	    		$.ajax({ 
	    			async:false,
	    			type: "post", 
	    			url: "${ctx}/admin/updateSystemConfig", 
	    			data: $('#updateSystemConfigForm').serialize(),//提交表单，相当于CheckCorpID.ashx?ID=XXX 
	    			success: function(msg){
	    				hideBlockDiv();
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
		<tags:blockDiv type="block" hide="true" info="名称和状态不能为空！"/>
		<ul class="breadcrumb">
		  <li><a href="${ctx }/admin/findAllOauthConfig">系统配置</a> <span class="divider">/</span></li>
		  <li class="active"><a href="#">修改配置信息</a></li>
		</ul>
		<form id="updateSystemConfigForm" class='form-signin' method="post" >
			<input type="hidden" name="id" value="${systemConfig.id}">  
			<table class="table table-bordered table-condensed">
				<tr>
					<td style="text-align:right;" >
			     		名称：
					</td>
					<td>
						<input type="text" id="sname" name="sname" value="${systemConfig.sname}">
					</td>
				</tr>
				<tr>
					<td style="text-align:right">
			     		编码：
					</td>
					<td>
			     		${systemConfig.scode} 
					</td>
				</tr>
				<tr>
					<td style="text-align:right">
			     		状态：
					</td>
					<td>
			     		<input type="text" id="sstatus" name="sstatus" value="${systemConfig.sstatus}"style="width: 60%;"> 
					</td>
				</tr>
				<tr>
					<td style="text-align:right;width: 20%">
			     		描述：
					</td>
					<td>
						<input type="text" name="sdescribe" value="${systemConfig.sdescribe}" style="width: 60%;"/> 
					</td>
				</tr>
			</table>
			<button class="btn btn-primary offset4" type="button" onclick="saveSystemConfig()">保存</button>
			<button class="btn btn-primary" type="reset">重置</button>
		</form>
	</body>
</html>