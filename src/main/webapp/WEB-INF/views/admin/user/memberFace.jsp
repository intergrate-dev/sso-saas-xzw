<!DOCTYPE html>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix='fmt' uri="http://java.sun.com/jsp/jstl/fmt" %> 
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<html>
	<head>
		<title>用户头像</title>
		<script type="text/javascript">
			//激活菜单
			$(document).ready(function() {
				$("#users_tab").addClass("active");
			});
			
			function checkSubmit(){
				if(confirm("您确定重置此用户的头像？")){
					$("#uploadform").submit();
				}
			}
		</script>
	</head>
	<body>
		<div class="row">
		    <div class="span11">
				<ul class="nav nav-pills">
					<li>
					  <a href="${ctx }/admin/profile?userId=${user.id}">基本资料</a>
					</li>
					<li class="active">
						<a href="${ctx }/admin/face?userId=${user.id}">用户头像</a>
					</li>
				</ul>
		    </div>
	    </div>
	   	<div class="row" >
			<form class="upimg" action="${ctx }/admin/resetFace" method="post" enctype="multipart/form-data" id="uploadform">
				<input type="hidden" name="userId" value="${user.id}"> 
				<div class="span1">
				</div>
				<div class="span6" id="actualup" style="height: 400px;">
					<img id="cut" style="width: 322px; height: 322px; margin-bottom: -322px;position:absolute;"class="img-polaroid"/> 
				</div>
				<div class="span5" id="preview-container">
					<span>头像预览：</span>
					<div id="preview-pane">
						<div class="preview-container" id="localImag">
							<img id="preview" style="width: 122px; height: 122px;" class="img-polaroid"/>
						</div>
					</div>
				</div>
				
				<button class="btn btn-primary offset4" type="button" onclick="checkSubmit()">重置头像 </button>
			</form>
		</div>
		<script type="text/javascript">
		$(function(){
			//选择头像
            window.ctx = '${ctx}';
            var userImg_cut = '${avatarLarge}'
            var userImg_pre = '${avatarMiddle}';

            setImgSrc(userImg_cut, "cut");
            setImgSrc(userImg_pre, "preview");
		});
		</script>
	</body>
</html>