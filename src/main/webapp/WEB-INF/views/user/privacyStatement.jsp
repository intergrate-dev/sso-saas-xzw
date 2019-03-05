<!DOCTYPE html>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="org.apache.shiro.web.filter.authc.FormAuthenticationFilter"%>
<%@page import="com.founder.sso.entity.LoginInfo"%>
<%@page import="org.apache.shiro.SecurityUtils"%>
<%@page import="com.founder.sso.entity.User"%>
<%@ page import="com.founder.sso.service.oauth.entity.SystemConfig" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<%--<c:set var="ctx" value="${pageContext.request.contextPath}"/>--%>
<% 
	response.setHeader("Cache-Control","no-store");
	response.setHeader("Pragrma","no-cache");
	response.setDateHeader("Expires",0);
%>
<html>
<head>
	<meta name="renderer" content="webkit">
    <title>隐私策略</title>
	<link rel="stylesheet" type="text/css" href="${ctx}/static/app/css/basic.css"/>
	<link rel="stylesheet" type="text/css" href="${ctx}/static/app/css/ext.css"/>

    <style type="text/css">
		
		.container_s{
			width: 100%;
			height: 580px;
		} 
		
		.content{
			margin-top:70px;
			/* margin-left:500px; */
			margin-left:auto;
			margin-right:auto;
			height: 80%;
			width: 373px;
			text-align: center; 
		}
		.logo{
			text-align: left; 
		}
		input.text{
			width: 330px;
			height: 25px;
			/*margin-top:20px;*/

		}
		.login-content{
			width: 350px;
			margin-right: auto;
			margin-left: auto;
			color: white;
		}

		span{
			margin:5px 5px;
		}

		.sign{
			width: 350px;
		}
		
		.left{
			float: left;
			margin: 10px 5px;
		}
		.right{
			float: right;
			margin: 10px 5px;
		}
		a{
			margin: 10px 5px;
			font-size: 1.1em;
			color: white;
			text-decoration: underline;
		}
		a:hover   {color:red;}
		.securityCode a:hover   {color:red;}
		.left a:hover   {color:red;}
		a:visited {color:white;}
		a:active  {color:yellow;}
		img.left{
			margin-left: 50px;
		}
		.content_left{
			margin-left: 20px;
		}
	</style>
    
</head>
<body>
<div class="m-header">
	<div class="set-seat">
	</div>
	<div class="mobile-header">
		<%--<div class="top-bar">
			<div class="center-bar">
				<a href="${ctx}">
					<img src="${ctx}/static/images/login/logo.png">
				</a>
			</div>
		</div>--%>
		<div class="navigator">
			<ul>
				<li>
					<a href="${ctx}">首页</a>
					<img src="">
				</li>
			</ul>
		</div>
	</div>
</div>
<!-- wap端header end-->
<header class="head">

	<style>
		.al{ text-align:right; color:#999; padding:15px; font-size:14px; position:absolute; top:0; right:0; z-index:10}
		.al a{ padding:0 10px; color:#999}
		.al a:hover{color:#f50}
	</style>
	<div class="wrap relative" style="z-index:9">
		<nav class="navigation">
			<a href="${ctx}">首页</a>
			<%--<a href="${ctx}/user/about">关于我们
			</a>--%>
		</nav>
	</div>
</header>
<section class="wrap">
	<div class="caption">
		<h1>隐私策略</h1>
		<hr>
		<%--<em></em>--%>
	</div>
	<article class="zyText statement">
		<p>
		<br/>
		<p>1、用户同意，个人隐私信息是指那些能够对用户进行个人辨识或涉及个人通信的信息，包括下列信息：用户真实姓名，身份证号，手机号码，IP地址。而非个人隐私信息是指用户对本服务的操作状态以及使用习惯等一些明确且客观反映在本公司服务器端的基本记录信息和其他一切个人隐私信息范围外的普通信息，以及用户同意公开的上述隐私信息；</p>
		<p>2、保护用户(特别是未成年人)的隐私是星洲网的一项基本政策，星洲网将对用户所提供的资料进行严格的管理及保护，并使用相应的技术，防止用户的个人资料丢失、被盗用或遭篡改，保证不对外公开或向第三方提供单个用户的注册资料及用户在使用网络服务时存储在星洲网的非公开内容，但下列情况除外：<br>
			2.1 事先获得用户的明确授权；<br>
			2.2 根据有关的法律法规要求；<br>
			2.3 按照相关政府主管部门的要求；<br>
			2.4 为维护社会公众的利益；<br>
			2.5 为维护星洲网的合法权益。<br>
		</p>
		<p>3、任何时候如果您对我们的隐私策略有疑问，请利用电子邮件<a href="mailto:privacy@zhangyue.com">xxx</a> 联系我们，我们会尽一切努力，请合理适当的范围内立即改善这个问题。</p>
		<p>4、星洲网提供的网络服务中包含的任何文本、图片、图形、音频和/或视频资料均受版权、商标和/或其它财产所有权法律的保护，未经相关权利人同意，上述资料均不得在任何媒体直接或间接发布、播放、出于播放或发布目的而改写或再发行，或者被用于其他任何商业目的。所有这些资料或资料的任何部分仅可作为私人和非商业用途而保存在用户终端内。星洲网不就由上述资料产生或在传送或递交全部或部分上述资料过程中产生的延误、不准确、错误和遗漏或从中产生或由此产生的任何损害赔偿，以任何形式，向用户或任何第三方负责。</p>
		<p>5、星洲网所有作品内容仅代表作者自己的立场和观点，与星洲网无关，由作者本人承担一切法律责任。</p></p>
	</article>
</section>
</body>
</html>