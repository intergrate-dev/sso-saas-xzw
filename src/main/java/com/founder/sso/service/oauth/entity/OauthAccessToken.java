package com.founder.sso.service.oauth.entity;


import java.util.Date;

import javax.persistence.Entity;

import org.joda.time.DateTime;

import com.founder.sso.entity.IdEntity;
import com.founder.sso.util.Clock;
import com.founder.sso.util.json.JSONException;
import com.founder.sso.util.json.JSONObject;

@Entity
public class OauthAccessToken  extends IdEntity {

    private String accessToken;
    private String uid;
    private Date tokenExpiresTime;
    private String provider;

    public OauthAccessToken(String accessToken, String expireIn, String provider, String uid) {
        super();
        this.accessToken = accessToken;
        this.uid = uid;
        this.provider = provider;
        this.tokenExpiresTime=new DateTime(Clock.DEFAULT.getCurrentDate()).plusSeconds(Integer.parseInt(expireIn)).toDate();
    }
    
    public OauthAccessToken(JSONObject jsonObject,String[] attrNames) throws JSONException {
        this.accessToken = jsonObject.getString(attrNames[0]);
        this.uid =  jsonObject.getString(attrNames[3]);
        int expireIn = jsonObject.getInt(attrNames[1]);
        this.tokenExpiresTime=new DateTime(Clock.DEFAULT.getCurrentDate()).plusSeconds(expireIn).toDate();
    }
    
    /**
     * 腾讯QQ的accessToken的返回值不是json格式，需要特殊处理
     * @param returnValues
     * @param type
     * @throws JSONException
     */
    public OauthAccessToken(String returnValues,String type) {
    	if(OauthProviders.TENCENT_QQ.getValue().equals(type)){
    		String expireIn = "0";
    		String split[] = returnValues.split("&");
    		//返回的格式为access_token=FC47FD0965468E72CC43891F355C6097&expires_in=7776000&refresh_token=3E71D84AA3EDA46F2109EEB82BF17773
    		//下面就是解析出access_token、expireIn、refreshToken
    		if(split.length>=1){
    			String accessTokenStr = split[0].toString();
    			this.accessToken = accessTokenStr.substring(accessTokenStr.indexOf("=")+1);
    		}
    		if(split.length>=2){
    			String accessTokenStr = split[1].toString();
    			expireIn =  accessTokenStr.substring(accessTokenStr.indexOf("=")+1);
    		}
    		this.uid = "";
    		this.tokenExpiresTime=new DateTime(Clock.DEFAULT.getCurrentDate()).plusSeconds(Integer.parseInt(expireIn)).toDate();
    	}	
    	
    	if(OauthProviders.FACEBOOK.getValue().equals(type)){
    		JSONObject fbAccessToken;
    		try {
				fbAccessToken = new JSONObject(returnValues);
				this.accessToken = fbAccessToken.getString("access_token");
				this.uid = "";
				this.provider = type;
				this.tokenExpiresTime=new DateTime(
						Clock.DEFAULT.getCurrentDate())
						.plusSeconds(fbAccessToken.getInt("expires_in"))
						.toDate();
			} catch (JSONException e) {
				e.printStackTrace();
			}
    	}	
    }

    public OauthAccessToken() {
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getUid() {
        return uid;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }


    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getProvider() {
		return provider;
	}

	public void setProvider(String provider) {
		this.provider = provider;
	}

	public Date getTokenExpiresTime() {
        return tokenExpiresTime;
    }

    public void setTokenExpiresTime(Date tokenExpiresTime) {
        this.tokenExpiresTime = tokenExpiresTime;
    }
    
    

}
