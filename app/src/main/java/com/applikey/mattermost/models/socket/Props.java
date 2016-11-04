package com.applikey.mattermost.models.socket;

import com.google.gson.annotations.SerializedName;

/**
 * Mattermost v3.2 API backport
 */
public class Props {

    @SerializedName("post")
    private String post;

    public String getPost() {
        return post;
    }
}
