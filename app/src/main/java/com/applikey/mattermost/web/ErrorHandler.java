package com.applikey.mattermost.web;

import android.content.Context;
import android.util.Log;

public class ErrorHandler {

    private static final String TAG = ErrorHandler.class.getSimpleName();

    private ErrorHandler() {
    }

    public static void handleError(Context context, Throwable throwable) {
        // TODO
        throwable.printStackTrace();
        Log.e(TAG, throwable.getMessage());
    }

    public static void handleError(Throwable throwable) {
        Log.e(TAG, throwable.getMessage());
    }

    public static void handleError(String message) {
        Log.e(TAG, message);
    }
}
