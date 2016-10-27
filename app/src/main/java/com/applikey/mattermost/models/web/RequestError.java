package com.applikey.mattermost.models.web;

import com.applikey.mattermost.web.GsonFactory;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

public class RequestError {

    @SerializedName("id")
    private String id;

    @SerializedName("message")
    private String message;

    @SerializedName("status_code")
    private int statusCode;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public static RequestError fromJson(String json) {
        final Gson gson = GsonFactory.INSTANCE.getGson();
        return gson.fromJson(json, RequestError.class);
    }
}
