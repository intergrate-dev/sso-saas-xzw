<!DOCTYPE html>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<% 
response.setHeader("Cache-Control","no-store"); 
response.setHeader("Pragrma","no-cache"); 
response.setDateHeader("Expires",0); 
%>
	    
		<style type="text/css">
			.wrapper{
				margin:0 10% 0 auto;
				width: 80%;
				height: 700px;
				text-align:center;
			} 
			.logo{
				margin:100px 0 50px 0;
				/* text-align:center; */
			}
			.nav{
				/* text-align: center; */
			}
			
			.echecked{
				color: red;
				font-size: 1em;
				font-weight: bold;
				margin-left: 0;
				margin-right: 0;
				text-decoration: none;
			}
			
			.checked{
				color: white;
				font-size: 1.4em;
				font-weight: bold;
				margin-left: 0;
				margin-right: 0;
				text-decoration: none;
			}
			.center{
				/* text-align: center; */
			}
			.infoDiv{
				text-align: center;
				margin-top:40px;
				color: white;
			}
			.inputc{
				margin-top:50px;
				color: white;
				font-weight: bold;
				/* margin-left: 390px; */
			}
			.inputc2{
				margin-top:25px;
				color: white;
				font-weight: bold;
				/* margin-left: 420px; */
			}
			
			.text250{
				width: 250px;
				height: 25px;
			}
			.text300{
				width: 300px;
				height: 25px;
			}
			
			.buttons{
				text-align: center;
			}
			.top50{
				margin-top:50px;
			}
			.top40{
				margin-top:40px;
			}
			.top30{
				margin-top:30px;
			}
			.top20{
				margin-top:20px;
			}
			a:HOVER {
				color: white;	
			}
			.info_div_info{
				text-align:left;
				margin-left:38%;
				margin-right:auto;
				width: 40%;
			}
		</style>
		<script type="text/javascript">
            $(function(){
                var avaLarge = '${avatarLarge}', avaMiddle = '${avatarMiddle}';

                window.ctx = '${ctx}';
                setImgSrc(avaLarge, "cut");
                setImgSrc(avaMiddle, "preview");
            });
			//切换手机和邮箱注册
	    	/*function changeType(registerType,theOther) {
	    		$("#byType").val(registerType);
	    		$("#A"+theOther).removeClass();
	    		$("#A"+theOther).addClass("echecked");
	    		$("#A"+registerType).removeClass();
	    		
	    		$("#A"+registerType).addClass("checked");
	    		
	    		$("._"+registerType).show();
	    		$("._"+theOther).hide();
	    	}*/
           		
		</script>
			<div class="logo">
				<a href="${ctx }/user/login" title="进入登录首页">
					<img src="${ctx}/static/images/login/logo.png">
				</a>
			</div>
			
