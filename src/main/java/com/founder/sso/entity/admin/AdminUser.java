package com.founder.sso.entity.admin;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.founder.sso.entity.IdEntity;
@Entity
@Table(name="admin_user")
public class AdminUser extends IdEntity {
    private String loginName;
    private String userName;

    private String password;
    private String plainPassword;
    private String slat;
    private String email;
    private String mobile;
    public String getLoginName() {
        return loginName;
    }
    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }
    
    @Transient
    public String getPlainPassword() {
        return plainPassword;
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
    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }
    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    public void setPlainPassword(String plainPassword) {
        this.plainPassword = plainPassword;
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

    
    

 }
