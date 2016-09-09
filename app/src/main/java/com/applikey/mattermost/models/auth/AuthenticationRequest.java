package com.applikey.mattermost.models.auth;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

// TODO Try playing around with this class
@SuppressWarnings("WeakerAccess")
public class AuthenticationRequest {

    @SerializedName("team_id")
    @Expose
    private String teamId;

    @SerializedName("login_id")
    @Expose
    private String login;

    @SerializedName("password")
    @Expose
    private String password;

    public AuthenticationRequest() {
    }

    public AuthenticationRequest(String teamId, String login, String password) {
        this.teamId = teamId;
        this.login = login;
        this.password = password;
    }

    public String getTeamId() {
        return teamId;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }
}
