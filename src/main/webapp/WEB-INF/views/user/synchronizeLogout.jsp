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

	/*Object provider = request.getSession().getAttribute("provider");
	String loginProvider = provider != null ? (String) provider : "";*/

	String fbAppID = (String) request.getSession().getAttribute("fb_appid");
	String googleClientId = (String) request.getSession().getAttribute("gp_clientid");
	String twitterAppID = (String) request.getSession().getAttribute("twitter_appid");
	String wechatAppID = (String) request.getSession().getAttribute("wechat_appid");
%>
<html>
	<head>
	    <title>跳转页面</title>
	    <meta charset="utf-8">
	    <meta http-equiv="X-UA-Compatible" content="IE=edge">
	    <script type="text/javascript" src="${ctx}/static/jquery1_11_1/jquery.min.js"></script>
	    <script type="text/javascript" src="${ctx}/static/js/loading.js"></script>
		<script type="text/javascript" src="${ctx}/static/js/thirdAuthLogin.js"></script>
        <script type="text/javascript" src="${ctx}/static/js/Base64.js"></script>
   	</head>
	<body>
		<c:forEach items="${subsystemList}" var="subsystem" varStatus="i">
			<iframe src="${subsystem.logoutUrl}?isAppType=${isAppType}&anyUrl=${anyUrl}" style="display:none;width: 0px;height: 0px;" ></iframe>
		</c:forEach>
		
		<div id="block_hint" style="visibility : hidden">
			如果您看到这个页面，说明您的网速缓慢或者浏览器阻止了跳转。<br />
			请您点击<a href='${toPage}'><strong><font color=red>这里</font></strong></a>继续。
		</div>
	<script>
	loadingHtml("正在退出，请稍等...","${ctx}");
	//alert("logout1");
	$(window).load(function(){
		loadingHtml("正在退出，请稍等...","${ctx}");

        var uid = '${uid}', provider = '${provider}', oauthId = '${oauthId}', sso_token = '${sso_token}';
        if(uid && provider && sso_token) {
			//子系统第三方平台登出处理

            var fbAppID = '<%=fbAppID %>', googleClientId = '<%=googleClientId %>',
                twitterAppID = '<%=twitterAppID %>', wechatAppID = '<%=wechatAppID %>',
                config = {contextPath: '${ctx}', status:status, fbAppID: fbAppID, googleClientId: googleClientId,
                    twitterAppID: twitterAppID, wechatAppID: wechatAppID};
			config.isLogin = true;
			loadThirdJSSDK(config);

            setTimeout(function(){
				if(provider == 'facebook'){
					loginOutFB(function (res) {
						if(res) {
							postHandle();
						}
					});
				}
				if(provider == 'googlePlus'){
					loginOutGP(function (res) {
						if(res) {
							postHandle();
						}
					});
				}
                if(provider == 'twitter'){
                    loginOutTwitter(function (res) {
                        if(res) {
                            postHandle();
                        }
                    });
                }
			},2000)
        } else {
			postHandle();
        }
	});

    function postHandle() {
        setTimeout(function(){
            var isAppType = "${isAppType}";
            var anyUrl="${anyUrl}";//到任意网址去的参数
            if(anyUrl!=null && anyUrl!= undefined && anyUrl!=""){
                if(window.opener){
                    var doc = window.opener.document;
                    console.log("uid: " + doc.getElementById("uname")[0].href)
                    window.opener.location.href=anyUrl;
                    window.close();
                }else{
                    location.replace(anyUrl);
                }
            }else if(isAppType == "app"){
                var toAmucCenter = "${toAmucCenter}";
                location.replace(toAmucCenter);
            }else{
                location.replace('${toPage}');
            }
        },500);   //延迟跳转页面
        setTimeout(function(){
            document.getElementById("block_hint").style.visibility = 'visible';
        },2000);
    }
	</script>
	</body>
	
</html>