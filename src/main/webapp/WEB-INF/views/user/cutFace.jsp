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
		<link href="${ctx }/static/Jcrop/jquery.Jcrop.min.css" rel="stylesheet" type="text/css" />
		
		<script type="text/javascript" src="${ctx }/static/Jcrop/jquery.Jcrop.js"></script>
		<style type="text/css">

			/* Apply these styles only when #preview-pane has
			   been placed within the Jcrop widget */
			.jcrop-holder #preview-pane {
			  display: block;
			  position: absolute;
			  z-index: 2000;
			  top: 30px;
			  right: -210px;
			  padding: 6px;
			  border: 1px rgba(0,0,0,.4) solid;
			  background-color: white;
			
			  -webkit-border-radius: 6px;
			  -moz-border-radius: 6px;
			  border-radius: 6px;
			
			  -webkit-box-shadow: 1px 1px 5px 2px rgba(0, 0, 0, 0.2);
			  -moz-box-shadow: 1px 1px 5px 2px rgba(0, 0, 0, 0.2);
			  box-shadow: 1px 1px 5px 2px rgba(0, 0, 0, 0.2);
			}
			
			/* The Javascript code will set the aspect ratio of the crop
			   area based on the size of the thumbnail preview,
			   specified here */
			#preview-pane .preview-container {
			  width: 122px;
			  height: 122px;
			  overflow: hidden;
			}
		
		</style>
		<script type="text/javascript">
			//激活菜单
			$(document).ready(function() {
				$("#profile_tab").addClass("checked");
				$("#menu_face").addClass("checked");
				var jcrop_api, boundx, boundy,

				$preview = $('#preview-pane'), $pcnt = $('#preview-pane .preview-container'), $pimg = $('#preview-pane .preview-container img'),

				xsize = $pcnt.width(), ysize = $pcnt.height();

				if (navigator.userAgent.indexOf("MSIE") > 0) {
					var api = $.Jcrop("#cut", {
						onChange : updatePreview,
						onSelect : updatePreview,
						aspectRatio : xsize / ysize
					});

					var bounds = api.getBounds();
					boundx = bounds[0];
					boundy = bounds[1];
					jcrop_api = api;

					$preview.appendTo(jcrop_api.ui.holder);
				} else {
					$('#cut').Jcrop({
						onChange : updatePreview,
						onSelect : updatePreview,
						aspectRatio : xsize / ysize
					}, function() {
						var bounds = this.getBounds();
						boundx = bounds[0];
						boundy = bounds[1];
						jcrop_api = this;

						$preview.appendTo(jcrop_api.ui.holder);
					});
				}

				function updatePreview(c) {
					if (parseInt(c.w) > 0) {
						var rx = xsize / c.w;
						var ry = ysize / c.h;

						$pimg.css({
							width : Math.round(rx * boundx) + 'px',
							height : Math.round(ry * boundy) + 'px',
							marginLeft : '-' + Math.round(rx * c.x) + 'px',
							marginTop : '-' + Math.round(ry * c.y) + 'px'
						});

						$('#x1').val(c.x);
						$('#y1').val(c.y);
						$('#x2').val(c.x2);
						$('#y2').val(c.y2);
					}
				}
				;
			});
		</script>
	</head>
	<body>
		
		<%@include file="common_profile.jsp" %>
		
		<div class="contend_div">
			<div class="infoDiv">
				<div class="alert alert-error "style="text-align: center;padding-bottom: 20px;">
					如果要修改头像，请双击左侧的大图。
				</div>
<%-- 				<tags:blockDiv type="success" hide="false" info="点击修改头像"/> --%>
			</div>
			
			<div class="content_info">
				<form class="upimg" action="${ctx }/${module}/cutFace" method="post" style="height: 400px;" enctype="multipart/form-data" id="uploadform">
					<input type="hidden" id="x1" name="x1" value="0" /> 
					<input type="hidden" id="y1" name="y1" value="0" /> 
					<input type="hidden" id="x2" name="x2" value="0" /> 
					<input type="hidden" id="y2" name="y2" value="0" /> 
					<input type="hidden" name="cutFace" value="${cutFace }" /> 
					<input type="hidden" name="avatarSmall" value="${avatarSmall }" /> 
					<input type="hidden" name="avatarMiddle" value="${avatarMiddle }" /> 
					<input type="hidden" name="avatarLarge" value="${avatarLarge }" /> 
					<div class="row" style="margin-bottom: 40px;">
						<div class="span1">
						</div>
						<div class="span5" id="actualup" style="padding:3px 0 0 3px;height: 325px;width: 325px;background-color: white;border: 1px solid rgba(0, 0, 0, 0.4);
    							border-radius: 6px;box-shadow: 1px 1px 5px 2px rgba(0, 0, 0, 0.2);border:1px solid #474747;" class="img-polaroid">
							<img id="cut" style="width: 322px; height: 322px;margin-bottom: -322px;position:absolute;" /> 
							<button id="re_chioce" style="display: none;">重新选择</button>
		<!-- 					<img alt="重新上传" src="/mp/sso/images/re_choice.png" id="re_chioce_img" style="display: none; margin-left: 100px" onclick="$('#re_chioce').click();"> -->
						</div>
						<div class="span3" id="preview-container">
							<div style="margin-bottom: 10px;margin-left: 50px;">头像预览：</div>
							<div id="preview-pane">
								<div class="preview-container" id="localImag">
									<img id="preview" style="width: 120px; height: 120px;*width: 115px; *height: 115px;*border:1px solid #474747; *padding: 2px;"/>
								</div>
							</div>
						</div>
					</div>
					<button class="btn btn-primary" style="width: 200px;margin-left: 300px;" type="submit"> 保 存  </button>
				</form>
			</div>
		</div>
	</body>
<script type="text/javascript">
$(function(){
	

    window.ctx = '${ctx}';
    var userImg_cut = '${avatarLarge}'
    var userImg_pre = '${avatarLarge}';

    setImgSrc(userImg_cut, "cut");
    setImgSrc(userImg_pre, "preview");
});
</script>
</html>