package com.applikey.mattermost.models.team;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Team extends RealmObject {

    @PrimaryKey
    @SerializedName("id")
    private String id;

    @SerializedName("name")
    private String name;

    @SerializedName("display_name")
    private String displayName;

    public Team() {
    }

    public Team(String id, String name, String displayName) {
        this.id = id;
        this.name = name;
        this.displayName = displayName;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDisplayName() {
        return displayName;
    }
}
