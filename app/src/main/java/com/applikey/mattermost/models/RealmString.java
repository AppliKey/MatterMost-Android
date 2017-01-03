package com.applikey.mattermost.models;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class RealmString extends RealmObject {

    @PrimaryKey
    private String val;

    public String getValue() {
        return val;
    }

    public void setValue(String value) {
        this.val = value;
    }

    public RealmString() {
    }

    public RealmString(String val) {
        this.val = val;
    }
}