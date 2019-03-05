<!DOCTYPE html>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="com.founder.sso.util.SystemConfigHolder" %>
<%@page import="com.founder.sso.entity.LoginInfo" %>
<%@page import="org.apache.shiro.SecurityUtils" %>
<%@page import="com.founder.sso.entity.User" %>
<%@ taglib prefix="sitemesh" uri="http://www.opensymphony.com/sitemesh/decorator" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<%
    response.setHeader("Cache-Control", "no-store");
    response.setHeader("Pragrma", "no-cache");
    response.setDateHeader("Expires", 0);

    User u = null;
    String userImg = "";
    //String oauthUid = "";
    if(SecurityUtils.getSubject().getPrincipal() != null){
        u = ((LoginInfo) SecurityUtils.getSubject().getPrincipal()).getUser();
        if (u != null) {
            userImg = u.getAvatarSmall();
            //oauthUid = u.getOauthUid();
        }
    }

    /*Object accessToken = request.getSession().getAttribute("access_token");
    Object attribute = request.getSession().getAttribute("login_status");
    String status = attribute != null ? (String) attribute : "";
    String loginAccessToken = provider != null ? (String) accessToken : "";*/

    Object provider = request.getSession().getAttribute("provider");
    String loginProvider = provider != null ? (String) provider : "";
    String fbAppID = (String) request.getSession().getAttribute("fb_appid");
    String googleClientId = (String) request.getSession().getAttribute("gp_clientid");
    String twitterAppID = (String) request.getSession().getAttribute("twitter_appid");
    String wechatAppID = (String) request.getSession().getAttribute("wechat_appid");
%>
<html>
<head>
    <%--<title>
        <%= SystemConfigHolder.getConfig("systemNameForUser")%>
        <sitemesh:title/>
    </title>--%>
    <title>个人主页</title>
    <%@ include file="commonHead.jsp" %>
    <style type="text/css">
        body {
            width: 100%;
            font: 14px/22px "宋体", "Arial Narrow", HELVETICA;
            background-color: white;
        }

        .container_content {
            width: 70%;
            *width: auto;
            height: auto;
            margin-left: 15%;
            margin-right: 15%;
            margin-top: 0;
            margin-bottom: 0;
            border: 5px solid #ECECEC;
        }

        .banner {
            background-color: #FE7F54;
            width: 100%;
            height: 50px;
            *height: 6%;
            margin: 0;
            padding: 0;
            /* 				border: 1px solid #00FF00; */
        }

        .content {
            width: 100%;
            /* 				height:87%; */

            height: auto;
            /* 				min-height:87%; */
            margin: 0;
            padding: 0;
            overflow: hidden;
            /* 				border: 1px solid #0000FF; */
        }

        .contend_div {
            width: 98%;
            height: 89.5%;
            margin: 0;
            padding: 10px 10px 10px 10px;
            /* 				border: 1px solid red; */
        }

        .nav {
            width: 17%;

            height: 630px;
            *height: 610px;
            margin: 0;
            padding: 0;
            float: left;
            /* 				border: 1px solid #FFFF00; */
        }

        .nav_portrait {
            background-color: #F3D9D0;
            width: 100%;
            height: 70px;
            margin: 0;
            padding: 0;
            color: black;
            font-weight: bold;
            border-right: 1px solid #FE7F54;
            text-align: center;
        }

        .nav_menu {
            background-color: #474747;
            width: 100%;
            height: 100%;

            margin: 0;
            padding: 0;
            /* 				border: 1px solid #00FFFF; */
        }

        ul {
            list-style-type: none;
            width: 100%;
            margin: 0;
            padding: 0;
            vertical-align: middle;
        }

        .nav_menu ul li {
            line-height: 40px;
            padding: 5px 10px;
            border-bottom: 1px solid #787878;
            border-right: 0px;
        }

        .nav_menu ul li img {
            *line-height: 40px;
            width: 20px;
            height: 20px;
            margin: 12px;
        }

        .nav_menu ul li.checked {
            background: black url("${ctx}/static/images/page/selected-nav.png") right no-repeat;
            font-weight: bold;
            font-size: 1.1em;
            color: white;
        }

        .nav_menu ul li a {
            color: #C8C8C8;
            font-size: 1em;
        }

        .nav_menu ul li.checked a {
            font-weight: bold;
            font-size: 1.1em;
            color: white;
        }

        .right {
            width: 83%;
            height: 100%;
            margin: 0;
            padding: 0;
            float: left;
            /* 				border: 1px solid #FF00FF; */
        }

        .menu {
            width: 100%;
            height: 60px;
            margin: 0;
            padding: 0;
            border-bottom: 2px solid #ECECEC;
            /* 				border: 1px solid black; */
        }

        .menu ul {
            margin-left: 50px;
        }

        .menu ul li {
            line-height: 30px;
            color: #0088cc;
            margin: 20px 20px 10px 20px;
            font-size: 1em;
            float: left;
            cursor: pointer;
        }

        .menu ul li.checked {
            text-align: center;
            width: 90px;
            background: url("${ctx}/static/images/page/selected-menu.png") no-repeat;
        }

        .menu ul li.checked a {
            font-weight: bold;
            font-size: 1em;
            color: white;
        }

        .footer {
            clear: both;
            text-align: center;
            background-color: #ECECEC;
            height: 7%;
            /*  			border: 1px solid #400000;  */
        }

        .infoDiv {
            width: 96.5%;
            margin-left: 13px;
            min-height: 5px;
            float: left;
        }

        .else {
            /* 				float: right; */
            text-align: right;
        }

        .content_info {
            clear: both;
            margin: 10px 10px 10px 10px;
            *margin: 10px 10px;
        }

        .table input {
            margin-bottom: 0;
        }

        select, textarea, input[type="text"], input[type="password"], input[type="datetime"], input[type="datetime-local"], input[type="date"], input[type="month"], input[type="time"], input[type="week"], input[type="number"], input[type="email"], input[type="url"], input[type="search"], input[type="tel"], input[type="color"], .uneditable-input {
            margin-bottom: 2px;
        }

        #no_decoration {
            text-decoration: none;
        }
    </style>
    <sitemesh:head/>
</head>

<body>
<div class="container_content">
    <div class="banner">
        <img style="margin: 10px 30px;width: 200px;float: left;" src="${ctx}/static/images/page/logo.png">
        <a onclick="loginOut('<%=loginProvider %>');">
            <img style="margin: 15px 30px 10px 0;float: right;width: 15px;height: 16px;"
                 src="${ctx}/static/images/page/offline.png">
        </a>
        <span style="float:right;margin: 0px 30px 0 0;width: 400px;color: white;text-align: right;">
					<a href="${ctx }/user/face" id="no_decoration">
						<img id="userImg" class="img-polaroid"
                             style="width: 37px;height: 37px;margin: 2px; margin-top: 3px;padding:2px;"/>
					</a>
					<a href="${ctx }/user/profile"
                       style="color:white;font-size: 1.1em;font-weight: bold;"><shiro:principal
                            property="nickname"/></a>
				</span>
    </div>

    <div class="content">
        <!-- 左侧的菜单栏 -->
        <div class="nav" style="margin-top:5px;*margin-top:7px;">

            <!-- 头像和用户名 -->
            <!-- 					<div class="nav_portrait"> -->
            <%-- 						<a href="${ctx }/user/face" id="no_decoration"> --%>
            <%-- 							<img class="img-polaroid" style="width: 50px;height: 50px;margin: 5px; " src='<%=u.getFullAvatarSmall() %>' > --%>
            <!-- 						</a> -->
            <!-- 					</div> -->

            <div class="nav_menu">
                <ul>
                    <li id="myHome_tab">
                        <a href="${ctx}/user/myHome">
                            <img src="${ctx}/static/images/page/shop.png">
                            我的应用
                        </a>
                    </li>
                    <li id="profile_tab">
                        <a href="${ctx}/user/profile">
                            <img src="${ctx}/static/images/page/profile.png">
                            个人资料
                        </a>
                    </li>
                    <li id="binding_tab">
                        <a href="${ctx }/user/connection/initBind">
                            <img src="${ctx}/static/images/page/link.png">
                            账号绑定
                        </a>
                    </li>
                </ul>
            </div>
        </div>

        <!-- 右侧的内容 -->
        <div class="right">
            <sitemesh:body/>
        </div>
    </div>
    <div class="footer">
        <br><span>
					Copyright@2014-2020 
					<a href="http://www.founder.com.cn/zh-cn/" style="color: red;">Founder.com.cn</a>
				</span>
    </div>

</div>
</body>

<script type="text/javascript" src="${ctx}/static/js/thirdAuthLogin.js"></script>
<script type="text/javascript" src="${ctx}/static/js/Base64.js"></script>
<script type="text/javascript">
    var oauthUid;
    /*function initGooglePlusInstance(googleClientId) {
        // gapi.auth2 init
        gapi.load('auth2', function () {
            auth2 = gapi.auth2.init({
                client_id: googleClientId, //客户端ID
                cookiepolicy: 'single_host_origin',
                scope: 'profile'
            });
        });
    }*/

    /*window.fbAsyncInit = function () {
        FB.getLoginStatus(function (response) {
            if (!response.authResponse) return;
            resp = response;
            userID = response.authResponse.userID;
        });
    }*/
    
    $(function () {
        //第三方平台登录
        var fbAppID = '<%=fbAppID %>', googleClientId = '<%=googleClientId %>',
            config = {contextPath: '${ctx}', fbAppID: fbAppID, googleClientId: googleClientId};
        config.isLogin = true;
        loadThirdJSSDK(config);

        /*setTimeout(function () {
            initGooglePlusInstance(googleClientId);
        }, 2000)*/

        

        var userImg = '<%=userImg %>';
        var userImg_cut = '${avatarLarge}'
        var userImg_pre = '${avatarLarge}';

        window.ctx = '${ctx}';
        setImgSrc(userImg, "userImg");
        setImgSrc(userImg_cut, "cut");
        setImgSrc(userImg_pre, "preview");

        /*var username = $('#no_decoration').next().text();
        setCookie("U_username", username)*/
    });

    /*function setCookie(sName, sValue){
        if (sValue == "") sValue = "";
        //cookie过期时间为关闭浏览器时就过期
        document.cookie = sName + "=" + escape(sValue) + ";path=/";
    }*/

</script>
</html>