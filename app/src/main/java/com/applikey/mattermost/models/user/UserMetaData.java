package com.applikey.mattermost.models.user;


import java.util.HashSet;
import java.util.Set;

public class UserMetaData {

    private String key;

    private Set<String> favoriteChannels;

    public UserMetaData() {
    }

    public UserMetaData(String key) {
        this.key = key;
        favoriteChannels = new HashSet<>();
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
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
                "key='" + key + '\'' +
                ", favoriteChannels=" + favoriteChannels +
                '}';
    }

    public boolean isSame(String key) {
        return this.key.equals(key);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        final UserMetaData that = (UserMetaData) o;

        return getKey() != null ? getKey().equals(that.getKey()) : that.getKey() == null;

    }

    @Override
    public int hashCode() {
        return getKey() != null ? getKey().hashCode() : 0;
    }
}
