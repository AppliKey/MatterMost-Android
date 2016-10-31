package com.applikey.mattermost.gcm;

import android.os.Bundle;

import java.util.regex.Pattern;

final class GcmMessageHelper {

    private static final String ARG_CHANNEL_ID = "channel_id";
    private static final String ARG_GOOGLE_SENT_TIME = "google.sent_time";
    private static final String ARG_CHANNEL_NAME = "channel_name";
    private static final String ARG_TYPE = "type";
    private static final String ARG_GOOGLE_MESSAGE_ID = "google.message_id";
    private static final String ARG_MESSAGE = "message";
    private static final String ARG_COLLAPSE_KEY = "collapse_key";

    private static final String MESSAGE_PATTERN_STRING = "[@](.*?)[:][\\s](.*)";
    private static final Pattern MESSAGE_PATTERN = Pattern.compile(MESSAGE_PATTERN_STRING,
            Pattern.DOTALL);

    private GcmMessageHelper() {
    }

    static String extractType(Bundle message) {
        return message.getString(ARG_TYPE);
    }

    static NotificationDto extractNotification(Bundle message) {
        return new NotificationDto(message.getString(ARG_CHANNEL_ID), message.getString(ARG_MESSAGE));
    }

    static class NotificationDto {
        private final String chanelId;

        private final String message;

        NotificationDto(String chanelId, String message) {
            this.chanelId = chanelId;
            this.message = message;
        }

        String getMessage() {
            return message;
        }

        String getChanelId() {
            return chanelId;
        }
    }
}
