package com.founder.sso.util.http;

import com.founder.sso.util.json.JSONException;
import com.founder.sso.util.json.JSONObject;

public class HttpclientException extends Exception {
    private int statusCode = -1;
    private int errorCode = -1;
    private String request;
    private String error;
    private static final long serialVersionUID = -2623309261327598087L;

    public HttpclientException(String msg) {
        super(msg);
    }

    public HttpclientException(Exception cause) {
        super(cause);
    }
    
    public HttpclientException(String msg , int statusCode) throws JSONException {
        super(msg);
        this.statusCode = statusCode;
    }

    public HttpclientException(String msg , JSONObject json, int statusCode) throws JSONException {
        super(msg + "\n error:" + json.getString("error") +" error_code:" + json.getInt("error_code") + json.getString("request"));
        this.statusCode = statusCode;
        this.errorCode = json.getInt("error_code");
        this.error = json.getString("error");
        this.request = json.getString("request");

    }

    public HttpclientException(String msg, Exception cause) {
        super(msg, cause);
    }

    public HttpclientException(String msg, Exception cause, int statusCode) {
        super(msg, cause);
        this.statusCode = statusCode;

    }

    public int getStatusCode() {
        return this.statusCode;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public String getRequest() {
        return request;
    }

    public String getError() {
        return error;
    }
}
