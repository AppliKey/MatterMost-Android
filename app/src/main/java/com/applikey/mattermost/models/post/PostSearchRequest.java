package com.applikey.mattermost.models.post;

import com.google.gson.annotations.SerializedName;

/**
 * @author Anatoliy Chub
 */

public class PostSearchRequest {

    @SerializedName("terms")
    private String terms;
    @SerializedName("is_or_search")
    private boolean isOrSearch = true;

    public PostSearchRequest(String terms) {
        this.terms = terms;
    }
}
