package com.founder.sso.auth.wechat.entity;

/**
 * Created by yuan-pc on 2019/3/1.
 */
public class ResultMessage {
    public static final String SUCCESS = "SUCCESS";
    public static final String ERROR = "ERROR";
    private String status;
    private String message;
    private Object data;

    public ResultMessage() {
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return this.data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
