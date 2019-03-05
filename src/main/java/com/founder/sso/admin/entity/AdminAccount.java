package com.founder.sso.admin.entity;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.founder.sso.entity.IdEntity;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;

@Entity
@Table(name = "admins")
public class AdminAccount extends IdEntity implements Serializable {

    private static final long serialVersionUID = 7799014137597925530L;
    /*
     * 首席超级管理员的id为1
     * 首席超级管理员拥有最大的权限，别人不能看不到首席超管的信息
     * 只有首席超管能修改自己的信息，并且只能修改密码、电话、邮箱三个信息
     */
    private static final long SUPER_SUPER_ADMIN_ID = 1;

    private String loginname;
    private String realname;
    @Transient
    private String plainPassword;
    private String password;
    private String slat;

    private String email;
    private String mobile;

    private String roles;
    private String permissions;
    private boolean enabled;
    
    //判断当前用户是否是首席超级管理员
    public boolean isSuperAdmin(){
    	return this.getId() == SUPER_SUPER_ADMIN_ID;
    }
    
    //判断当前用户是否包含管理后台管理员的权限
    public boolean isAccountAdmin(){
    	return this.getRoles().contains(Roles.ACCOUNT_ADMIN.getValue());
    }
    
    //判断当前用户是否包含系统配置的权限
//    public boolean isConfigAdmin(){
//    	return this.getRoles().contains(Roles.CONFIG_ADMIN.getValue());
//    }
    
    //判断当前用户是否包含外网用户的管理权限
    public boolean isUserAdmin(){
    	return this.getRoles().contains(Roles.USER_ADMIN.getValue());
    }
    
    //判断当前用户是否包含日志的管理权限
//    public boolean isLogAdmin(){
//    	return this.getRoles().contains(Roles.LOG_ADMIN.getValue());
//    }
    
    //判断当前用户是否包含子系统的管理权限
    public boolean isChileConfigAdmin(){
    	return this.getRoles().contains(Roles.CHILD_CONFIG_ADMIN.getValue());
    }
    
    //判断当前用户是否包含api的管理权限
//    public boolean isApiAdmin(){
//    	return this.getRoles().contains(Roles.API_ADMIN.getValue());
//    }

    @Column
    @org.hibernate.annotations.Type(type = "yes_no")
    public boolean isEnabled() {
        return enabled;
    }
    
    // --Getter&Setter Start--//
    public String getLoginname() {
        return loginname;
    }

    public String getRealname() {
        return realname;
    }

    public String getPlainPassword() {
        return plainPassword;
    }

    public String getPassword() {
        return password;
    }

    public String getSlat() {
        return slat;
    }

    public String getEmail() {
        return email;
    }

    public String getMobile() {
        return mobile;
    }

    public String getRoles() {
        return roles;
    }

    public String getPermissions() {
        return permissions;
    }

    public void setLoginname(String loginname) {
        this.loginname = loginname;
    }

    public void setRealname(String realname) {
        this.realname = realname;
    }

    public void setPlainPassword(String plainPassword) {
        this.plainPassword = plainPassword;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setSlat(String slat) {
        this.slat = slat;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public void setRoles(String roles) {
        this.roles = roles;
    }

    public void setPermissions(String permissions) {
        this.permissions = permissions;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public List<String> getRoleList() {
        return ImmutableList.copyOf(Splitter.on(",").omitEmptyStrings().split(roles));
    }

    public List<String> getPermissionList() {
        return ImmutableList.copyOf(Splitter.on(",").omitEmptyStrings().split(permissions));
    }
    // --Getter&Setter End--//

}
