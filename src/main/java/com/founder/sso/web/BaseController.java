package com.founder.sso.web;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.UnavailableSecurityManagerException;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;

import com.founder.sso.entity.LoginInfo;
import com.founder.sso.entity.User;

/**
 * 公共API类 
 * @author zhangmc
 */
public class BaseController {

	protected Long getCurrentUserId() {
    	Long userId = null;
    	User user = getCurrentUser();
    	if(user != null){
    		userId = user.getId();
    	}
        return userId;
    }

    protected User getCurrentUser() {
    	User user = null;
    	LoginInfo loginInfo;
		try {
			loginInfo = getLoginInfo();
		} catch (UnavailableSecurityManagerException e) {
			loginInfo = null;
		}
    	if(loginInfo != null){
    		user = loginInfo.getUser();
    	}
        return user;
    }

    /**
     * 从Shiro中获取当前用户登录信息
     * 
     * @return
     */
    protected LoginInfo getLoginInfo() {
//    	SecurityUtils.getSubject().login(;)
        return (LoginInfo) getSubject().getPrincipal();
    }
    
    /**
     * 得到session会话，不同于HttpSession
     */
    protected Session getSession() {
    	return getSubject().getSession();
    }
   
    
    /**
     * 得到session
     */
    protected Subject getSubject() {
    	return  SecurityUtils.getSubject();
    }
    
    

}
