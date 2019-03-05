package com.founder.sso.admin.entity;

/**
 * 基础管理员角色
 * 
 * SUPER_ADMIN拥有最高权限
 * ACCOUNT_ADMIN：拥有管理员设置的权限
 * CONFIG_ADMIN拥有系统配置权限
 * USER_ADMIN拥有管理外网用户权限
 * LOG_ADMIN拥有日志管理权限
 * SYSTEM_STATE_ADMIN拥有系统状态的权限
 * @author fengdd
 *
 */
public enum Roles {
    SUPER_ADMIN("超级管理员","super_admin"),
    USER_ADMIN("外网用户管理员","user_admin"),
    ACCOUNT_ADMIN("后台用户管理员","account_admin"),
    CHILD_CONFIG_ADMIN("子系统管理员","child_config_admin"),
    OAUTH_CONFIG_ADMIN("第三方登录账号管理员","oauth_config_admin"),
    SYSTEM_CONFIG_ADMIN("系统配置","system_config_admin"),
//    CONFIG_ADMIN("系统配置管理员","config_admin"),
//    LOG_ADMIN("日志管理员","log_admin"),
//    SYSTEM_STATE_ADMIN("系统状态管理员","system_state_admin"),
//    API_ADMIN("API管理员","api_admin"),
//    EMAIL_ADMIN("邮件管理员","email_admin")
    ;
    
	private String name;
    private String value;
    private Roles(String name,String val) {
    	this.name=name;
    	this.value=val;
	}
    public String getValue() {
		return value;
	}
    public String getName(){
    	return name;
    }
    
}
