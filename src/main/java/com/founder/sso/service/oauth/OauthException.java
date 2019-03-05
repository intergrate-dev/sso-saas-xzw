package com.founder.sso.service.oauth;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class OauthException extends RuntimeException {
    private static final long serialVersionUID = 3051251769023578496L;

    private String provider;
    //可以将此转换成请求相关的集合
    private String sourceUrl;
    
    private String errorCode;

    public String getProvider() {
        return provider;
    }

    public String getSourceUrl() {
        return sourceUrl;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public void setSourceUrl(String sourceUrl) {
        this.sourceUrl = sourceUrl;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

}
