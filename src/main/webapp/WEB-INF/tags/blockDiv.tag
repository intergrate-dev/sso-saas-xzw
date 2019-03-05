<%@tag pageEncoding="UTF-8"%>
<%@ attribute name="type" type="java.lang.String" required="true"%>
<%@ attribute name="info" type="java.lang.String" required="true"%>
<%@ attribute name="hide" type="java.lang.Boolean" required="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<%
// 	String divClass = "";
// 	if(type.equals("block")){
// 		divClass = "alert-block";
// 	}else if(type.equals("success")){
// 		divClass = "alert-success";
// 	}else if(type.equals("error")){
// 		divClass = "alert-error";
// 	}
%>
	<style>
		.alert{
			margin-bottom:2px;
		}
		.alert-block {
		    padding-bottom: 9px;
		    padding-top: 10px;
		}
	</style>

	<div id="${type }Div" <c:if test="${hide}">style="display:none;"</c:if> class="alert alert-${type }">
		<button type="button" class="close" onclick="hideDiv('${type }Div')">&times;</button>
	  	<span id="${type }InfoSpan">${info }</span>
	</div>
