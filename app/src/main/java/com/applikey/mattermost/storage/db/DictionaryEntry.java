package com.applikey.mattermost.storage.db;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class DictionaryEntry extends RealmObject {

    @PrimaryKey
    @SerializedName("key")
    private String key;

    @SerializedName("value")
    private String value;

    public DictionaryEntry() {
    }

    public DictionaryEntry(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}