package com.applikey.mattermost.web.adapter;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;


public abstract class BaseTypeAdapter<T> extends TypeAdapter<T> {

    private Gson mGson;

    public void setGson(Gson gson) {
        mGson = gson;
    }

    public Gson getGson() {
        return mGson;
    }

}
