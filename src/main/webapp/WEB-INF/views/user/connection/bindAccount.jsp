<!DOCTYPE html>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@page import="com.founder.sso.service.oauth.OauthClientManager"%>
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
			//激活菜单
			$(document).ready(function() {
			     $("#binding_tab").addClass("checked");
			});
			
			//解除绑定
			//function unBind(forbidUnbind, url){
			//	if(forbidUnbind){
			//		$("#blockDiv").show("slow");
			//	}else if(confirm("您确定解除绑定此账号？")){
			//		location.href=url;
			//	}
			//}
			
			function unBind(url){
				if(confirm("您确定解除绑定此账号？")){
					location.href=url;
				}
			}

		</script>
		<style type="text/css">
			.info {
				font-size:12px;
			}
		</style>
	</head>
	<body>
		<div class="menu">
			<ul>
				<li class="checked">
					<a id="no_decoration" href="#">
				  		账号绑定
					</a>
				</li>
			</ul>
		</div>
		<div class="contend_div">
			<div class="infoDiv" style="height: 50px;">
				<c:if test="${beBounded }">
					<!-- 新浪微博会记住登录状态,所以要特殊提醒 -->
					<c:if test="${provider=='sina_weibo' }">
						<tags:blockDiv type="error" hide="false" info="对不起，${nickname }已经被绑定。 新浪记住了${nickname }的登录状态，如果想绑定其它账号，请重启浏览器。"/>
					</c:if>
					<c:if test="${provider!='sina_weibo' }">
						<tags:blockDiv type="error" hide="false" info="对不起，${nickname }已经被绑定，请选择其他账号。"/>
					</c:if>
				</c:if>
				<c:if test="${doBounded }">
					<tags:blockDiv type="info" hide="false" info="已成功绑定${nickname }！"/>
				</c:if>
				<tags:blockDiv type="block" hide="true" info="为了确保您解绑后，帐号的内容不会丢失，请您<a href='${ctx }/user/password/initUpdatePassword'>设置密码</a>后重试。"/>
			</div>
			<div class="content_info">
				<div class="" style="height: 300px;">
					<c:forEach items="${bindMap }" var="bindingSet" varStatus="i">
						<!-- 默认为true -->
						<c:set var="beBounded" value="true"></c:set>
						<div class="span2" style="margin-bottom: 30px;">
							<img src="${ctx }/static/images/bindAccount/${bindingSet.key}.jpg" class="img-rounded" style="width: 56px;">
								
							<div class="info" style="margin-top: 5px;">
								<c:set value="${bindingSet.value}" var="binding"></c:set>
									<c:if test="${ binding.oauthUid != null}">
										<i class="icon-ok"></i> ${ binding.nickname} 
										<a onclick="unBind('${ctx }/user/connection/unBind?bindingId=${ binding.id}')" title="" class="info">
		 									 解绑
		 								</a>
										<br>
									</c:if>
									<c:if test="${ binding.oauthUid == null}">
										<c:set var="beBounded" value="false"></c:set>
										<%--<i class="icon-remove"></i> 未绑定 --%>
									</c:if>
							</div>
							<div class="row" style="margin-top: 5px;">
								<div class="span2">
									<c:set var="thisProvider" scope="request" value="${bindingSet.key}"/>
									<%--<a href="<%=OauthClientManager.getBindingUrl(request.getAttribute("thisProvider").toString())%>" class="info">--%>
									<a class="info" onclick="bindOperate('${binding.provider}');">

										<%-- TODO --%>
										<c:if test="${ !beBounded}">
											[ 绑定账号 ]
										</c:if>
									</a>
								</div>
							</div>
						</div>
					</c:forEach>
				</div>
			</div>
		</div>
	</body>
</html>