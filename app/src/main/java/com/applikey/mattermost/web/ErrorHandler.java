package com.applikey.mattermost.web;

import android.content.Context;

public class ErrorHandler {

    private ErrorHandler() {
    }

    public static void handleError(Context context, Throwable throwable) {
        // TODO
        throwable.printStackTrace();
    }
}
