package com.founder.sso.admin.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang3.StringUtils;

import com.founder.sso.entity.IdEntity;

/**
 * 子系统的实体类
 * @author zhangmc
 */
@Entity
@Table(name = "subsystem")
public class Subsystem extends IdEntity implements Serializable{

	private static final long serialVersionUID = 6990917414326635885L;
	//子系统代码，唯一标识；子系统进行登录认证时的身份代码
	private String code;
	private String name;
	//子系统的入口，首页地址
    private String homePage;
    //子系统加密类型
    private String encryptType;
    //子系统同步退出的url
    private String logoutUrl;
    //子系统登陆重定向的url
    private String redirectUrl;
    //子系统安全码
    private String secretKey;
	private String description;
	//是否启用子系统
    private boolean enabled;
    //子系统授权地址
    private String authcUrl;
    //是否启用子系统账号
    private int subaccount;
    @Transient
    //子系统关联的用户名称
    private String username;
    //绑定子系统账号的id标识 userThirdBind id
    @Transient
    private Long userthirdbindid;
    public int getSubaccount() {
		return subaccount;
	}

	public void setSubaccount(int subaccount) {
		this.subaccount = subaccount;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public Long getUserthirdbindid() {
		return userthirdbindid;
	}

	public void setUserthirdbindid(Long userthirdbindid) {
		this.userthirdbindid = userthirdbindid;
	}

	public String getAuthcUrl() {
		return authcUrl;
	}

	public void setAuthcUrl(String authcUrl) {
		this.authcUrl = authcUrl;
	}

    public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}
    public String getName() {
		return name;
	}
    
    public String getOmitName() {
    	if(name.length()>17){
    		return name.substring(0,15)+"...";
    	}
    	return name;
    }
    
    public String getConvertName() {
    	return name==null?"":name;
    }

	public void setName(String name) {
		this.name = name;
	}

	public String getHomePage() {
		return homePage;
	}
	
	public String getConvertHomePage() {
		return homePage==null?"":homePage;
	}

	public void setHomePage(String homePage) {
		this.homePage = homePage;
	}
	
	public String getLogoutUrl() {
		return logoutUrl;
	}
	
	public String getConvertLogoutUrl() {
		return logoutUrl==null?"":logoutUrl;
	}

	public void setLogoutUrl(String logoutUrl) {
		this.logoutUrl = logoutUrl;
	}

	public String getEncryptType() {
		return encryptType;
	}
	
	public String getConvertEncryptType() {
		return encryptType==null?"":encryptType;
	}

	public void setEncryptType(String encryptType) {
		this.encryptType = encryptType;
	}
	
	public String getRedirectUrl() {
		return redirectUrl;
	}
	
	public String getConvertRedirectUrl() {
		return redirectUrl==null?"":redirectUrl;
	}

	public void setRedirectUrl(String redirectUrl) {
		this.redirectUrl = redirectUrl;
	}

	public String getDescription() {
		return description;
	}
	
	
	public String getOmitDescription() {
		if(StringUtils.isBlank(description)){
			return "暂无描述信息";
		}
		if(description.length()>43){
			return description.substring(0,42)+"...";
		}
		return description;
	}
	
	public String getConvertDescription() {
		return description==null?"":description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

	public String getSecretKey() {
		return secretKey;
	}

	public String getConvertSecretKey() {
		return secretKey==null?"":secretKey;
	}
	
	public void setSecretKey(String secretKey) {
		this.secretKey = secretKey;
	}

}
