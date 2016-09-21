package com.applikey.mattermost.web;

import com.google.gson.Gson;

public enum GsonFactory {

    INSTANCE;

    private Gson gson;

    public Gson getGson() {
        if (gson == null) {
            gson = new Gson();
        }
        return gson;
    }
}
