package com.founder.sso.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.servlet.http.HttpServletRequest;

import com.founder.sso.util.json.JSONException;
import com.founder.sso.util.json.JSONObject;
import org.apache.commons.lang3.StringUtils;

import com.founder.sso.service.oauth.entity.UserOauthBinding;
import com.founder.sso.util.DateUtil;
import com.founder.sso.util.FaceUtil;
import com.founder.sso.util.SystemConfigHolder;
import com.google.common.collect.Maps;

@Entity
@Table(name = "users")
public class User extends IdEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    private static String application = SystemConfigHolder.getConfig("application");
    private static final boolean DEFAULT_ACTIVE = true;
    private String username;
    private String nickname;
    // 注册日期
    private Date registerDate;
    // 是否是活动状态
    private boolean actived = DEFAULT_ACTIVE;
    // 手机号
    private String phone;
    // 邮箱
    private String email;
    // oauth的id
    private String oauthUid;
    // oauth的供应商
    private String provider;
    // 会员中心id
   /* private int uid;
    public int getUid() {
		return uid;
	}

	public void setUid(int uid) {
		this.uid = uid;
	}*/
    private Integer uid;

    public Integer getUid() {
        return uid;
    }

    public void setUid(Integer uid) {
        this.uid = uid;
    }

    //小头像50*50
    /*private String avatarSmall = FaceUtil.default_avatarSmall;
    //中头像100*100
    private String avatarMiddle = FaceUtil.default_avatarMiddle;
    //大头像322*322
    private String avatarLarge = FaceUtil.default_avatarLarge;*/

    private String avatarSmall;
    //中头像100*100
    private String avatarMiddle;
    //大头像322*322
    private String avatarLarge;

    @OneToOne(cascade = {CascadeType.ALL})
    @JoinColumn(name = "principal_id")
    private LocalPrincipal localPrincipal;

    @OneToMany(cascade = {CascadeType.REMOVE}, fetch = FetchType.LAZY, mappedBy = "user")
    private Set<UserOauthBinding> userOauthBindings = new HashSet<UserOauthBinding>();

    public LocalPrincipal getLocalPrincipal() {
        return localPrincipal;
    }

    public void setLocalPrincipal(LocalPrincipal localPrincipal) {
        this.localPrincipal = localPrincipal;
    }


    public Set<UserOauthBinding> getUserOauthBindings() {
        return userOauthBindings;
    }

    public void setUserOauthBindings(Set<UserOauthBinding> userOauthBindings) {
        this.userOauthBindings = userOauthBindings;
    }


    @Column
    @org.hibernate.annotations.Type(type = "yes_no")
    public boolean isActived() {
        return actived;
    }

    public void setActived(boolean actived) {
        this.actived = actived;
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

    public String getConvertNickname() {
        return nickname == null ? "" : nickname;
    }


    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public Date getRegisterDate() {
        return registerDate;
    }

    public String getConvertRegisterDate() {
        return registerDate == null ? "" : DateUtil.getTimeStr(registerDate);
    }

    public void setRegisterDate(Date registerDate) {
        this.registerDate = registerDate;
    }

    public String getPhone() {
        return phone;
    }

    public String getConvertPhone() {
        return phone == null ? "" : phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public String getConvertEmail() {
        return email == null ? "" : email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getOauthUid() {
        return oauthUid;
    }

    public String getConvertOauthUid() {
        return oauthUid == null ? "" : oauthUid;
    }

    public void setOauthUid(String oauthUid) {
        this.oauthUid = oauthUid;
    }

    public String getProvider() {
        return provider;
    }

    public String getConvertProvider() {
        return provider == null ? "" : provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getAvatarSmall() {
        return avatarSmall;
    }

    public String getFullAvatarSmall(HttpServletRequest request) {
        return getFullFace(getAvatarSmall(), request);
    }

    public void setAvatarSmall(String avatarSmall) {
        this.avatarSmall = avatarSmall;
    }

    public String getAvatarMiddle() {
        return avatarMiddle;
    }

    public String getFullAvatarMiddle(HttpServletRequest request) {
        return getFullFace(getAvatarMiddle(), request);
    }

    public void setAvatarMiddle(String avatarMiddle) {
        this.avatarMiddle = avatarMiddle;
    }

    public String getAvatarLarge() {
        return avatarLarge;
    }

    public String getFullAvatarLarge(HttpServletRequest request) {
        return getFullFace(getAvatarLarge(), request);
    }

    public void setAvatarLarge(String avatarLarge) {
        this.avatarLarge = avatarLarge;
    }

    public String getFullFace(String face, HttpServletRequest request) {
        if (face.contains("http")) {
            return face;
        } else {
            //return application + face;
            return request.getScheme() + "://" + request.getServerName() + request.getContextPath() + face;
        }
    }

    public void resetFace(String path) {
        this.avatarSmall = path;
        this.avatarMiddle = path;
        this.avatarLarge = path;
    }

    public Map<String, String> getIdentities() {
        HashMap<String, String> identities = Maps.newHashMap();
        identities.put("uid", getId().toString());
        String uid = getUid() == null ? "" : Integer.toString(getUid());
        identities.put("mid", uid);

        if (StringUtils.isNotBlank(username)) {
            identities.put("username", username);
        }
        if (StringUtils.isNotBlank(nickname)) {
            identities.put("nickname", nickname);
        }
        if (StringUtils.isNotBlank(phone)) {
            identities.put("phone", phone);
        }
        if (StringUtils.isNotBlank(email)) {
            identities.put("email", email);
        }
        return identities;
    }

    public void asJson(JSONObject json) throws JSONException {
        Map<String, Object> map = Maps.newHashMap();
        map.putAll(getIdentities());
        json.put("username", username == null ? "" : username);
        json.put("nickname", nickname == null ? "" : nickname);
        json.put("isOpen", getLocalPrincipal() == null ? 1 : 0);//0是主账号登录，1是三方登录
        if (!org.springframework.util.StringUtils.isEmpty(provider)){
            json.put("provider", provider);
        }
    }
}