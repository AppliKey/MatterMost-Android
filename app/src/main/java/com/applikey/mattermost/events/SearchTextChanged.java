package com.applikey.mattermost.events;

public class SearchTextChanged {

    private String text;

    public SearchTextChanged(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }
}
