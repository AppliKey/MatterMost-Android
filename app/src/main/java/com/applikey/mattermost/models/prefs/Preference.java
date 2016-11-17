package com.applikey.mattermost.models.prefs;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Preference extends RealmObject {

    @PrimaryKey private String id;

    @SerializedName("user_id") private String userId;
    @SerializedName("category") private String category;
    @SerializedName("name") private String name;
    @SerializedName("value") private String value;

    public Preference(String userId, String category, String name, String value) {
        this.userId = userId;
        this.category = category;
        this.name = name;
        this.value = value;
    }

    public Preference() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
