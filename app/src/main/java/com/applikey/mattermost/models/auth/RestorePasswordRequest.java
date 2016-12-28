package com.applikey.mattermost.models.auth;

import com.google.gson.annotations.SerializedName;

public class RestorePasswordRequest {

    @SerializedName("email")
    private String email;

    public RestorePasswordRequest(String email) {
        this.email = email;
    }
}
