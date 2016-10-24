package com.applikey.mattermost.models.channel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RequestUserId {

    @SerializedName("user_id")
    @Expose
    String userId;

    public RequestUserId(String userId) {
        this.userId = userId;
    }
}
