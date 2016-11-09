package com.applikey.mattermost.models.channel;

import com.google.gson.annotations.SerializedName;

/**
 * @author Anatoliy Chub
 */

public class DirectChannelRequest {

    @SerializedName("user_id")
    private String userId;

    public DirectChannelRequest(String userId) {
        this.userId = userId;
    }

    public String getUserId() {

        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
