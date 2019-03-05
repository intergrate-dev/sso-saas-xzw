/**
 * Created by Administrator on 2017/5/11.
 */
$(function () {
    $('.inputIcon').each(function () {
            $(this).css({opacity:'0'})
    });
    var confiPwdVal="";
    var pwdValud="";
    // 支持和邮箱
    function isPhoneNo(phone) {
        // var pattern = /^1[34578]\d{9}$/;
        var pattern_1 = /^1[34578]\d{9}$/,
            pattern_2 = /^([a-zA-Z0-9_\-\.]+)@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.)|(([a-zA-Z0-9\-]+\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\]?)$/;
        return pattern_1.test(phone) || pattern_2.test(phone);
    }

    // 验证中文名称
    function isChinaName(password) {
        //var pattern = /^([a-z0-9\.\@\!\#\$\%\^\&\*\(\)]){6,20}$/;
        //return pattern.test(name);
    	 var str = password;
    	    if (str == null || str.length < 6 || str.length > 20) {
    	        return false;
    	    }else if(getStrength(str) >= 2){
    	    	return true;
    	    }
    }
    
    function getStrength(passwd){  
        var intScore = 0;  
        if (passwd.match(/[a-z]/) || passwd.match(/[A-Z]/)) // [验证]至少一个字母  
        {  
            intScore = (intScore+1)  
        } 
        if (passwd.match(/\d/)) // [验证]至少一个数字  
        {  
            intScore = (intScore+1)  
        } 
    	// 特殊字符验证  
        if (passwd.match(/[!,@#$%^&*?_~ ]/)) // [验证]至少一个特殊字符  
        {  
            intScore = (intScore+1)  
        }
        return intScore;  
    }

    //密码验证
    $('#password_hid').blur(function () {
    	pwdValud=$('#password_hid').val();
    	console.log("pwd-->"+pwdValud);
    	var regEn = /[`~!@#$%^&*()_+<>?:"{},.\/;'[\]]/im,
	    regCn = /[·！#￥（——）：；“”‘、，|《。》？、【】[\]]/im;
		if(regEn.test(pwdValud) || regCn.test(pwdValud)){
			$('#password').parent().find('.promptMsg').css({display:'none'})
			$('#password').parent().find('.erroMsg').html('<img src="../static/app/img/42.png" >密码不能包含特殊字符')
		}else if(isChinaName($('#password_hid').val())){
        }else{
            $('#password').parent().find('.promptMsg').css({display:'none'})
            $('#password').parent().find('.erroMsg').html('<img src="../static/app/img/42.png" >6到20位英文字母、数字、字符组成,至少包含其中两种')
        }
        $('#password').parent().find('.inputIcon').css({opacity:"0"})
    });
    $('#password_hid').focus(function () {
        $('#password').parent().find('.erroMsg').text("");
        $('#password').parent().find('.promptMsg').css({display:'block'});
        $('#password').parent().find('.inputIcon').css({opacity:"1"});
        $("#submitBtn").attr("disabled",false);
    });

    //确认密码
    $('#confirm_password').blur(function () {
         confiPwdVal=$('#confirm_password').val();
         console.log("ckpwd-->"+pwdValud);
        if(!!confiPwdVal && confiPwdVal==pwdValud){

        }else{
            $('#confirm_password').parent().find('.erroMsg').html('<img src="../static/app/img/42.png" >两次密码不一致');
            $("#submitBtn").attr("disabled",true);
        }
        $('#confirm_password').parent().find('.inputIcon').css({opacity:"0"})
    });
    $('#confirm_password').focus(function () {
        $('#confirm_password').parent().find('.erroMsg').text("");
        $('#confirm_password').parent().find('.inputIcon').css({opacity:"1"})
        $("#submitBtn").attr("disabled",false);
    });

    $('.inputIcon').click(function () {
        $(this).siblings("input").val("");
        $(this).siblings(".erroMsg").text("");
    });





    //注册提交表单
    $('#submitBtn').click (function (event) {
        if(isPhoneNo($('#telphone').val())&&isChinaName($('#password').val())&&isChinaName(confiPwdVal)&&confiPwdVal==pwdValud){
            alert('验证通过')

        }else{
            var event = event || window.event;
            event.preventDefault();
            window.event.returnValue = false;
        }

    });
    //忘记密码表单提交
    $('#pwdRemdcCrtainBtn').click (function (event) {
        if(isPhoneNo($('#telphone').val())&&isChinaName($('#password').val())&&isChinaName(confiPwdVal)&&confiPwdVal==pwdValud){
            alert('验证通过')

        }else{
            var event = event || window.event;
            event.preventDefault();
            window.event.returnValue = false;
        }

    });


});
