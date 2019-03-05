package com.founder.sso.changyan;

import java.io.Serializable;

public class UserInfo implements Serializable{
	   
	private static final long serialVersionUID = 1L;

	public int is_login;//是否登录，0表示未登录，1表示已登录
 
    public User user; //用户信息

    public int getIs_login() {
       return is_login;
    }

    public void setIs_login(int is_login) {
       this.is_login = is_login;
    }

    public User getUser() {
       return user;
    }

    public void setUser(User user) {
       this.user = user;
    }
 
 }
	