$(function(){
//点击复选框
$('.login-xy label input').click(function() {
	$(this).toggleClass('checked');
     })
	 
//点击复选框
$('.account-check dd label input').click(function() {
    $(this).toggleClass('checked')
})	
//收藏tab切换
$('.my-tab li').click(function(){
	$(this).addClass('cur').siblings('li').removeClass('cur');
	$('.my-cont').hide();
	var index=$(this).index();
	$('.my-cont').eq(index).show();
	}) 
})



var tel = /^1[3,4,5,7,8]\d{9}$/;
//点击发送验证码
var clock = '';
var nums = 60;
var btn;

function sendCode(thisBtn) {
	if ($('#fPhone').val() == "") {
        $('.error-text').show().text("邮箱不能为空");
        return false;
    } else if (!tel.test($('#fPhone').val())) {
        $('.error-text').show().text('请输入正确的邮箱');
        return false;
    }else{
    btn = thisBtn;
    btn.disabled = true; //将按钮置为不可点击
    btn.value = nums + '秒';
    clock = setInterval(doLoop, 1000); //一秒执行一次
	}
}
function doLoop() {
    nums--;
    if (nums > 0) {
        btn.value = nums + '秒';
    } else {
        clearInterval(clock); //清除js定时器
        btn.disabled = false;
        btn.value = '获取';
        nums = 60; //重置时间
    }
}

