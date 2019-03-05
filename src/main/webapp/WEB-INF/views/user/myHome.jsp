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
	    <title>个人主页</title>
	    <meta charset="utf-8">
	    <meta http-equiv="cache-control" content="no-cache">
	    <meta http-equiv="X-UA-Compatible" content="IE=edge">
	    <script type="text/javascript">
		    $(document).ready(function(){
				//激活菜单
			    $("#myHome_tab").addClass("checked");
		    });
		    function unBind(url){
				if(confirm("您确定解除绑定此账号？")){
					location.href=url;
				}
			}
		</script>
	    <style type="text/css">
	    	.thumbnails{
	    		margin-left:5px;
	    	}
	    	
	    	.thumbnails > li {
	    		margin-left: 50px;
	    	}
	    	a:HOVER {
				text-decoration: none;	
			}
	    	a h5:HOVER {
				text-decoration: underline;	
			}
	    </style>
	</head>
	<body>
		<div class="menu">
			<ul>
				<li class="checked">
					<a id="no_decoration" href="#">
				  		合作网站
					</a>
				</li>
			</ul>
		</div>
		<div class="contend_div">
			<div class="infoDiv">
			</div>
			<div class="content_info">
				<ul class="thumbnails">
					<c:forEach items="${subsystemList }" var="subsystem">
						<li class="span4">
						  	<a href="${subsystem.homePage }" class="thumbnail" target="_blank">
						   		<div class="caption">
						   			<h5 title="${subsystem.name }">${subsystem.omitName }</h5>
									<p style="font-size: 12px;height: 40px;" title="${subsystem.description }">
										${subsystem.omitDescription }
									</p>
						   		</div>
						 	</a>
						 	<%-- <button class="btn btn-primary " style="width: 100px;margin-top:10px;margin-left: 30px;" type="button" onclick="location.href='${ctx}/user/initBindThirdAccount/${subsystem.id }'"> 绑定</button> --%>
							
							<c:if test="${subsystem.subaccount  == 1 }">
								<c:if test="${subsystem.username  != null }">
									<i class="icon-ok"></i> ${subsystem.username}
									<a onclick="unBind('${ctx }/user/unBindThirdAccount?userThirdBindId=${subsystem.userthirdbindid}')" title="" class="info">
				 									[  解绑  ]
				 					</a>
								</c:if>
								<c:if test="${subsystem.username  == null }">
									<a href="initBindThirdAccount/${subsystem.id}" class="info">
											[ 绑定账号 ]
									</a>
								</c:if>
							</c:if>
						</li>
					</c:forEach>
				</ul>
			</div>
		</div>
	</body>
</html>