package com.applikey.mattermost.models.auth;

import com.google.gson.annotations.SerializedName;

@SuppressWarnings("WeakerAccess")
public class AuthenticationRequest {

    @SerializedName("login_id")
    private String login;

    @SerializedName("password")
    private String password;

    public AuthenticationRequest() {
    }

    public AuthenticationRequest(String login, String password) {
        this.login = login;
        this.password = password;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }
}
