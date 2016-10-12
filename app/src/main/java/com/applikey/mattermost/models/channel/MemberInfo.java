package com.applikey.mattermost.models.channel;

import com.google.gson.annotations.SerializedName;

public class MemberInfo {

    @SerializedName("id")
    private String id;

    @SerializedName("nickname")
    private String nickname;

    @SerializedName("email")
    private String email;

    @SerializedName("username")
    private String userName;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
