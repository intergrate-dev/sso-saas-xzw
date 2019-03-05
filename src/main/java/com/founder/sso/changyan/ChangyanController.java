package com.founder.sso.changyan;

import java.nio.charset.Charset;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.authc.AuthenticationToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.founder.sso.entity.LoginInfo;
import com.founder.sso.entity.User;
import com.founder.sso.service.UserService;
import com.founder.sso.service.oauth.entity.OauthProviders;
import com.founder.sso.service.oauth.entity.OauthToken;
import com.founder.sso.service.oauth.entity.UserOauthBinding;
import com.founder.sso.util.Clock;
import com.founder.sso.util.json.JSONObject;
import com.founder.sso.web.BaseController;

@Controller
@RequestMapping("/user/changyan")
public class ChangyanController  extends BaseController{
	
	@Autowired
	private UserService userService;
	
	//该接口只有当畅言已登录，getUserInfo返回未登录时，才会被调用用来登录自身网站
    @RequestMapping("login")
    
    public ResponseEntity<String> loginByCy(@RequestParam(value = "callback") String callback,
            @RequestParam(value = "cy_user_id") String cy_user_id, 
            @RequestParam(value = "user_id",required=false) String user_id, 
			@RequestParam(value = "nickname") String nickname,
            @RequestParam(value = "img_url") String img_url,
            HttpServletRequest request,HttpServletResponse resp) {
		User user = getCurrentUser();
		//用户已登录直接返回该用户userid
		if(user != null){
			 return returnResponse(callback, user); 
		}else{//用户未登录
			//先查找畅言账户的自动创建用户
			User localUser = userService.findByUsername(OauthProviders.SOHU_CY.getPrefix()+"-"+cy_user_id);
			//不存在，创建一个
			if(localUser==null){
				localUser = new User();
				localUser.setActived(true);
				localUser.setOauthUid(cy_user_id);
				localUser.setProvider(OauthProviders.SOHU_CY.getValue());
				localUser.setRegisterDate(Clock.DEFAULT.getCurrentDate());
				UserService.generateFace(localUser, img_url);
				localUser.setUsername(OauthProviders.SOHU_CY.getPrefix()+"-"+cy_user_id);
				localUser.setNickname(nickname);
				localUser = userService.save(localUser);
			}else{//存在自动创建用户
				//查询畅言账户在sso中的绑定用户，不用管user_id,只需要关注cy_user_id
				UserOauthBinding bindUser = userService.findBinding(cy_user_id, OauthProviders.SOHU_CY.getValue());
				//存在绑定用户
				if(bindUser!=null){
					//返回绑定用户的userid
					localUser = bindUser.getUser();
				}
			}
			//执行用户登录,返回js进行ssoLogin
			LoginInfo info = new LoginInfo(localUser, request.getRemoteHost(), OauthToken.LOGIN_TYPE.OTHER_SYSTEM.toString());
			AuthenticationToken token = new OauthToken(info);
			getSubject().login(token);
			return returnResponse(callback, localUser); 
		}
		
    }

	private ResponseEntity<String> returnResponse(String callback, User autoUser) {
		return new ResponseEntity<String>(callback+ "({\"user_id\":"+autoUser.getId()+",reload_page:0})",HttpStatus.OK);
	}
    
    @RequestMapping("loginout")
    public ResponseEntity<String> logoutBySite(@RequestParam(value = "callback") String callback,HttpServletResponse resp) {
        //执行用户退出，返回js进行ssoLogout
    	int code = getCurrentUser()==null?0:1;
    	if(code==1){
    		getSubject().logout();
    	}
    	return new ResponseEntity<String>(callback+ "({\"code\":"+code+",reload_page:0})",HttpStatus.OK);
    }

    //该接口在页面每一次加载时都会被调用，用来判断用户在自己网站是否登录
    @RequestMapping("getUserInfo")
    @ResponseBody
    public ResponseEntity<String> getUserInfo(@RequestParam(value = "callback") String callback) {
        UserInfo userinfo = new UserInfo();
        User ssoUser = getCurrentUser();
        if(ssoUser == null){//此处为模拟逻辑，具体实现可以变化
            userinfo.setIs_login(0);//用户未登录
        }else{
            userinfo.setIs_login(1);//用户已登录
            com.founder.sso.changyan.User user = new com.founder.sso.changyan.User();
            user.setUser_id(ssoUser.getId().intValue()); //该值具体根据自己用户系统设定
            user.setNickname(ssoUser.getConvertNickname()); //该值具体根据自己用户系统设定
            user.setImg_url(ssoUser.getAvatarLarge()); //该值具体根据自己用户系统设定，可以为空
            user.setSign(""); //签名已弃用，任意赋值即可
            userinfo.setUser(user);
        }
        return new ResponseEntity<String>(callback+"("+new JSONObject(userinfo).toString()+")", HttpStatus.OK);
    }

	
	@RequestMapping(value = "success", method = RequestMethod.GET)
	public String login(HttpServletRequest request, HttpServletResponse response) {
		return "/user/cySuccess";
	}
}
