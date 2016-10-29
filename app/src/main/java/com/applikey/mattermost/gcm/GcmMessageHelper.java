package com.applikey.mattermost.gcm;

import android.os.Bundle;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

class GcmMessageHelper {

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

    static RawPostDto extractRawPost(Bundle message) {
        final String rawString = message.getString(ARG_MESSAGE);

        final Matcher matcher = MESSAGE_PATTERN.matcher(rawString);
        if (!matcher.find()) {
            throw new IllegalArgumentException("Illegal GCM message format");
        }
        final String group0 = matcher.group(1);
        final String group1 = matcher.group(2);

        return new RawPostDto(group0, group1);
    }

    static class RawPostDto {
        private final String authorName;
        private final String message;

        RawPostDto(String authorName, String message) {
            this.authorName = authorName;
            this.message = message;
        }

        String getAuthorName() {
            return authorName;
        }

        String getMessage() {
            return message;
        }
    }
}
