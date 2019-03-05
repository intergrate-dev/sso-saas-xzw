<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="com.founder.sso.util.SystemConfigHolder" %>
<%@ taglib prefix="sitemesh" uri="http://www.opensymphony.com/sitemesh/decorator" %>  
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}" />

<!DOCTYPE html>
<html>
	<head>
		<%--<title>
			<%/*= SystemConfigHolder.getConfig("systemNameForAdmin")*/%>
			<sitemesh:title/>
		</title>--%>
        <title>个人主页</title>
		<%@ include file="commonHead.jsp" %>
		<style type="text/css">
			#content{
				min-height: 400px;
			}
		</style>
		<sitemesh:head/>
	</head>

<body>
	<div class="container">
		<%@ include file="/WEB-INF/layouts/header.jsp"%>
		<%@ include file="/WEB-INF/layouts/topnav.jsp"%>
		<div id="content">
			<sitemesh:body/>
		</div>
		<%@ include file="/WEB-INF/layouts/footer.jsp"%>
	</div>
	<script src="${ctx}/static/bootstrap2_3_2/js/bootstrap.min.js" type="text/javascript"></script>
</body>
</html>