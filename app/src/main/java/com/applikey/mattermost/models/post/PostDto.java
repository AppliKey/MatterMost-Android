package com.applikey.mattermost.models.post;

import com.applikey.mattermost.models.user.User;

import java.util.Comparator;

public class PostDto {

    public static final Comparator<PostDto> COMPARATOR_BY_POST_TIMESTAMP = (o1, o2)
            -> (int) (o1.getPost().getCreatedAt() - o2.getPost().getCreatedAt());
    private Post post;
    private String authorName;
    private String authorAvatar;
    private User.Status status;

    public PostDto(Post post,
            String authorName,
            String authorAvatar,
            User.Status status) {
        this.post = post;
        this.authorName = authorName;
        this.authorAvatar = authorAvatar;
        this.status = status;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getAuthorAvatar() {
        return authorAvatar;
    }

    public void setAuthorAvatar(String authorAvatar) {
        this.authorAvatar = authorAvatar;
    }

    public User.Status getStatus() {
        return status;
    }

    public void setStatus(User.Status status) {
        this.status = status;
    }
}
