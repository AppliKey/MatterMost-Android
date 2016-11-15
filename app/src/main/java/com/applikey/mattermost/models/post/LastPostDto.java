package com.applikey.mattermost.models.post;

public class LastPostDto {

    private Post post;
    private String channelId;

    public LastPostDto(Post post, String channelId) {
        this.post = post;
        this.channelId = channelId;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }
}
