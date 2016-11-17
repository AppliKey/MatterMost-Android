package com.applikey.mattermost.models.post;

import com.google.gson.annotations.SerializedName;

public class PostSearchRequest {

    @SerializedName("terms")
    private String terms;

    @SerializedName("is_or_search")
    //Set to true if an Or search should be performed vs an And search.
    private boolean isOrSearch = true;

    public PostSearchRequest(String terms) {
        this.terms = terms;
    }
}
