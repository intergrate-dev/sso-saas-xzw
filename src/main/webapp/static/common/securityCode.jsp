<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>

    <script type="text/javascript">
    	//换一张验证码
    	function changeImg() { 
	        var imgSrc = $("#imgObj"); 
	        var src = imgSrc.attr("src"); 
	        imgSrc.attr("src", chgUrl(src));
	    } 
	    //为了使每次生成图片不一致，即不让浏览器读缓存，所以需要加上时间戳     
	    function chgUrl(url) { 
	        var timestamp = (new Date()).valueOf(); 
	        if ((url.indexOf("?") >= 0)) { 
	            url = url.split("?")[0];
	        }
			url = url + "?timestamp=" + timestamp; 
	        return url;
	    } 
	    
	    /** 检查验证码是否输入正确 ，返回true/false */
	    function checkValidateCode1(){
	    	var checkResult = false;
	    	var inputCode = $("#securityCode").val();
	    	if(inputCode==''){
	    		showError("请输入验证码!");
	    	}else {
	    		$.ajax({
	    			async:false,
	    			type:"post",
	    			url:"${ctx}/securityCode/check?inputCode=" + inputCode + "&random=" + new Date().getTime() ,
	    			success: function(msg){
	    				var json = eval('(' + msg + ')');
	   					if(json.result == 'true'){
	    					$("#errorInfo").hide();
	    					checkResult = true;
	   					}else if(json.result == 'false'){
	   						$("#securityCode").focus();
	   						showError("验证码错误!");
	    				}else {
	   						showError("请刷新重试!");
	    				}
					},
				 	error: function() {
				 		showError("请刷新重试!");
// 				 		$("#errorDiv").show("slow");
				    }
	    		});
	    	}
	    	return checkResult;
    	}
	    
	    function showError(info){
	    	var validator = $("#errorInfo");
	    	validator.show();
    		validator.html(info);
	    }
	    function hideError(){
	    	$("#errorInfo").hide();
	    }
   	</script>

	<%--/securityCode--%>
	<input name="securityCode" id="securityCode" type="text" onchange="hideError()" style="width:70px;margin-bottom: 1px;" placeholder='验证码' required/>
	<img id="imgObj" src="${ctx }/securityCode/get" alt="验证码" style="width: 80px;height: 30px;" onclick="changeImg()"/>
	<a href="javascript:void(0)" onclick="changeImg();" style="font-size: 0.9em;">换一张</a>
	<span id="errorInfo" class="error" style="font-size:12px;font-weight: normal;margin-left: 1px;" for="securityCode" hidden="hidden"></span>



