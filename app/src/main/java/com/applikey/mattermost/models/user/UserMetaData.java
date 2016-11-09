package com.applikey.mattermost.models.user;


import java.util.HashSet;
import java.util.Set;

public class UserMetaData {

    private String serverUrl;
    private String teamId;
    private String userId;

    private Set<String> favoriteChannels;

    public UserMetaData() {
    }

    public UserMetaData(String serverUrl, String teamId, String userId) {
        this.serverUrl = serverUrl;
        this.teamId = teamId;
        this.userId = userId;
        favoriteChannels = new HashSet<>();
    }

    public String getServerUrl() {
        return serverUrl;
    }

    public void setServerUrl(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    public String getTeamId() {
        return teamId;
    }

    public void setTeamId(String teamId) {
        this.teamId = teamId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Set<String> getFavoriteChannels() {
        return favoriteChannels;
    }

    public void setFavoriteChannels(Set<String> favoriteChannels) {
        this.favoriteChannels = favoriteChannels;
    }

    @Override
    public String toString() {
        return "UserMetaData{" +
                "serverUrl='" + serverUrl + '\'' +
                ", teamId='" + teamId + '\'' +
                ", userId='" + userId + '\'' +
                ", favoriteChannels=" + favoriteChannels +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        UserMetaData that = (UserMetaData) o;

        if (!getServerUrl().equals(that.getServerUrl()))
            return false;
        if (!getTeamId().equals(that.getTeamId()))
            return false;
        return getUserId().equals(that.getUserId());

    }

    @Override
    public int hashCode() {
        int result = getServerUrl().hashCode();
        result = 31 * result + getTeamId().hashCode();
        result = 31 * result + getUserId().hashCode();
        return result;
    }
}
