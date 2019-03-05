package com.founder.sso.entity;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static org.apache.commons.lang3.StringUtils.isNoneEmpty;

import java.io.Serializable;
import java.util.Date;

import com.founder.sso.service.oauth.entity.RemoteUser;
import com.founder.sso.util.Clock;

/**
 * 登录信息类用于封装登录时间、IP等信息
 * 
 * 同时直接封装登录名和用户名简化获取操作
 * 
 * @author zhangmc
 * 
 */
public class LoginInfo implements Serializable {

    private static final long serialVersionUID = -8923280317871304920L;
    private long userId;
    private String username;
    private String nickname;
    private Date loginTime;
    private String loginIp;
    private User user;
    private String loginType;
    private RemoteUser remoteUser;

	public LoginInfo(User user, String ip, String loginType, RemoteUser remoteUser) {
        checkArgument(isNoneEmpty(ip), "LoginIp Must NOT be Blank");
        this.user = user;
        if(user != null){
        	Long userId = user.getId();
        	if(userId != null){
        		this.userId = userId;
        	}
        }
        this.username = user.getUsername();
        this.nickname = user.getNickname();
        this.loginTime = Clock.DEFAULT.getCurrentDate();
        this.loginIp = ip;
        this.loginType = loginType;
        this.remoteUser = remoteUser;
    }
    
    public LoginInfo(User user, String ip, String loginType) {
        checkNotNull(user, "user Must NOT be null");
    	checkArgument(isNoneEmpty(ip), "LoginIp Must NOT be Blank");
    	this.user = user;
    	if(user != null){
    		Long userId = user.getId();
    		if(userId != null){
    			this.userId = userId;
    		}
    	}
    	this.username = user.getUsername();
    	this.nickname = user.getNickname();
    	this.loginTime = Clock.DEFAULT.getCurrentDate();
    	this.loginIp = ip;
    	this.loginType = loginType;
    }

    public static long getSerialversionuid() {
        return serialVersionUID;
    }

    public long getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getNickname() {
        return nickname;
    }

    public Date getLoginTime() {
        return loginTime;
    }

    public String getLoginIp() {
        return loginIp;
    }

    public User getUser() {
        return user;
    }

    public String getLoginType() {
        return loginType;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void setLoginTime(Date loginTime) {
        this.loginTime = loginTime;
    }

    public void setLoginIp(String loginIp) {
        this.loginIp = loginIp;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setLoginType(String loginType) {
        this.loginType = loginType;
    }

    public RemoteUser getRemoteUser() {
		return remoteUser;
	}

	public void setRemoteUser(RemoteUser remoteUser) {
		this.remoteUser = remoteUser;
	}
}