package com.applikey.mattermost.models.post;

import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Map;

public class PostResponse {

    @SerializedName("order")
    private List<String> order;

    @SerializedName("posts")
    private Map<String, Post> posts;

    public List<String> getOrder() {
        return order;
    }

    public void setOrder(List<String> order) {
        this.order = order;
    }

    public Map<String, Post> getPosts() {
        return posts;
    }

    public void setPosts(Map<String, Post> posts) {
        this.posts = posts;
    }
}
