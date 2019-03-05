package com.founder.sso.service.oauth.entity;

import org.apache.shiro.authc.AuthenticationException;

import com.google.common.base.Objects;

public class OauthErrorMsg extends AuthenticationException {
    private static final long serialVersionUID = 769529885850014143L;

    private String provider;
    private String providerName;
    private String sourceUrl;
    private String error_code;
    private String error;
    private String originalMsg;
    private Throwable cause;

    public String getProvider() {
        return provider;
    }

    public String getProviderName() {
        return providerName;
    }

    public String getSourceUrl() {
        return sourceUrl;
    }

    public String getError_code() {
        return error_code;
    }

    public String getError() {
        return error;
    }

    public String getOriginalMsg() {
        return originalMsg;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public void setProviderName(String providerName) {
        this.providerName = providerName;
    }

    public void setSourceUrl(String sourceUrl) {
        this.sourceUrl = sourceUrl;
    }

    public void setError_code(String error_code) {
        this.error_code = error_code;
    }

    public void setError(String error) {
        this.error = error;
    }

    public void setOriginalMsg(String originalMsg) {
        this.originalMsg = originalMsg;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this).toString();
    }

    public static long getSerialversionuid() {
        return serialVersionUID;
    }

    public Throwable getCause() {
        return cause;
    }

    public void setCause(Throwable cause) {
        this.cause = cause;
    }
    
    

}
