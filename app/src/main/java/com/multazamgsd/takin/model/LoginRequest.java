package com.multazamgsd.takin.model;

public class LoginRequest {
    private String uid;
    private String last_login;

    public LoginRequest() {}

    public LoginRequest(String uid, String last_login) {
        this.uid = uid;
        this.last_login = last_login;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getLast_login() {
        return last_login;
    }

    public void setLast_login(String last_login) {
        this.last_login = last_login;
    }
}
