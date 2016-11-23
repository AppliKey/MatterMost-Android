package com.applikey.mattermost.web;

public final class MattermostErrorIds {

    public static final String CHANNEL_ALREADY_CREATED = "store.sql_channel.save_channel.exists.app_error";
    public static final String CHANNEL_URL_EXISTED = "store.sql_channel.save_channel.previously.app_error";

    public static final String USERNAME_INVALID = "store.sql_user.update.username_taken.app_error";
    public static final String EMAIL_INVALID = "store.sql_user.update.email_taken.app_error";

    private MattermostErrorIds() {
    }
}
