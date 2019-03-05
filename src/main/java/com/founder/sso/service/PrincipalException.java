package com.founder.sso.service;

import com.google.common.base.Objects;

public class PrincipalException extends Exception {
    private static final long serialVersionUID = -3771121679702311920L;

    private String errorCode;
    private String error;
    private String explainMsg;
    private Throwable cause;

    public PrincipalException() {
    }

    public PrincipalException(String explainMsg) {
        this(null, null, explainMsg, null);
    }

    public PrincipalException(String explainMsg, Throwable cause) {
        this(null, null, explainMsg, cause);
    }

    public PrincipalException(String errorCode, String error, String explainMsg) {
        this(errorCode, error, explainMsg, null);
    }

    public PrincipalException(String errorCode, String error, String explainMsg, Throwable cause) {
        this.errorCode = errorCode;
        this.error = error;
        this.explainMsg = explainMsg;
        this.cause = cause;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getError() {
        return error;
    }

    public String getExplainMsg() {
        return explainMsg;
    }

    public Throwable getCause() {
        return cause;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public void setError(String error) {
        this.error = error;
    }

    public void setExplainMsg(String explainMsg) {
        this.explainMsg = explainMsg;
    }

    public void setCause(Throwable cause) {
        this.cause = cause;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(PrincipalException.class).toString();
    }

}
