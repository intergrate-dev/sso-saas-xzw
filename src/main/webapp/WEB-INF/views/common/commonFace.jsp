<!DOCTYPE html>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix='fmt' uri="http://java.sun.com/jsp/jstl/fmt" %> 
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
		<script type="text/javascript">
			//见 withTopNavForUser.jsp
			$(function(){
                var avaLarge = '${avatarLarge}', avaMiddle = '${avatarMiddle}';
                
                  window.ctx = '${ctx}';
                  setImgSrc(avaLarge, "cut");
                  setImgSrc(avaMiddle, "preview");
			});

            function extracted() {
				//同步到会员（使端上保持一致）
                var appIfApiUrl = "${appIfApiUrl}";
				//var prefix = appIfApiUrl.indexOf("https://") != -1 ? appIfApiUrl : window.ctx;
                var form = new FormData(document.getElementById("uploadform"));
                $.ajax({
                    // 内网接口地址
                    url: appIfApiUrl + "/amuc/api/member/uploadImage",
                    type: "post",
                    data: form,
                    processData: false,
                    contentType: false,
                    success: function (data) {
                        data = JSON.parse(data);
                        console.log("上传头像成功！" + JSON.stringify(data, null, "\t"));
                        if (data.code == 1) {
                            $("#uploadform").submit();
                        } else {
                            alert("后台同步上传失败！");
                        }
                    },
                    error: function (e) {
                        alert("请求错误！！");
                    }
                });
            }

            function setImagePreview(ele) {
				var docObj = document.getElementById("doc");

				var isSupport = changeType(docObj.value);

				//如果支持该类型
				if (isSupport) {
					// TODO
                    extracted();
                    //$("#uploadform").submit();
				} else {
					alert("不支持选择的图片类型！");
				}
			}

			//判断文件类型

			function changeType(objFile) {

				var objtype = objFile.substring(objFile.lastIndexOf(".")).toLowerCase();
				
				$("#picType").val(objtype);
				var fileType = new Array(".jpg", ".png", ".gif", ".jpeg",".JPG", ".PNG", ".GIF", ".JPEG");

				for ( var i = 0; i < fileType.length; i++) {

					if (objtype == fileType[i]) {
						return true;
					}
				}

				return false;
			}
			
			//跟随鼠标移动“浏览”按钮，兼容IE下单击上传图片	
			$(document).on("mousemove", function( event ) {
				var leftRegion = $("#cut").offset().left;
				var rightRegion = leftRegion + 322;
				var xPosition = event.pageX;
				
				if(xPosition < leftRegion + 30){
					$("#doc").css("left", leftRegion +"px");
				}else if(xPosition + 30 > rightRegion){
					$("#doc").css("left", rightRegion -60 +"px");
				}else {
					$("#doc").css("left", xPosition - 30 +"px");
				}
			});			
		</script>
<%-- 	    <c:if test="${info == 'success' }"> --%>
<%-- 			<tags:blockDiv type="success" hide="false" info="头像修改成功!"/> --%>
<%-- 	    </c:if> --%>
<%-- 	    <c:if test="${info == 'fail' }"> --%>
<%-- 			<tags:blockDiv type="error" hide="false" info="头像修改失败,请稍后重试!"/> --%>
<%-- 	    </c:if> --%>
		<c:if test="${info == 'failpic' }">
 			<tags:blockDiv type="error" hide="false" info="头像格式不符合(jpg/png/gif/jpeg/JPG/PNG/GIF/JPEG)"/>
 	    </c:if>
	   	<div class="row" id="con_epf_2" >
			<form class="upimg" action="${ctx }/${module }/initCutFace" method="post" enctype="multipart/form-data" id="uploadform">
			<%--<form class="upimg" id="uploadform">--%>
				<input type="hidden" name="picType" id="picType">
				<input type="hidden" name="module" value="${module }">
				<input type="hidden" name="userId" value="${userId }">
				<div class="span1">
				</div>

				<div class="span5" id="actualup" style="height: 400px;">
					<c:if test="${user.provider == null }">
						<img id="cut" src="" title="点击修改头像" style="cursor:pointer;width: 322px; height: 322px; margin-bottom: -322px;position:absolute;"class="img-polaroid"/>
						<input type="file" name="file" id="doc" onchange="setImagePreview(this);" style="position:absolute;width:60px; height: 324px; margin-bottom: -322px; opacity:0;
							filter:alpha(opacity=0);cursor:pointer;" title="点击修改头像"/>
					</c:if>

					<c:if test="${user.provider != null }">
						<img id="cut" src="" title="第三方账号不支持修改头像" style="width: 322px; height: 322px; margin-bottom: -322px;position:absolute;"class="img-polaroid"/>
					</c:if>
				</div>

				<%--<div class="span3">
					<input type="submit" name="upload" id="upload" value="测试" onclick="testApi();"/>
				</div>--%>

				<div class="span3" id="preview-container">
					<div style="margin-bottom: 10px;">头像预览：</div>
					<div id="preview-pane">
						<div class="preview-container" id="localImag">
							<img id="preview" src="" style="width: 122px; height: 122px;" class="img-polaroid"/>
						</div>
					</div>
				</div>
			</form>
		</div>
