/**
 * Copyright (c) 2014 CoderKiss
 * <p>
 * CoderKiss[AT]gmail.com
 */

package com.applikey.mattermost.utils.kissUtils.utils;

import android.content.Context;

public class ClipboardUtil {

    public static final String TAG = "ClipboardHelper";

    @SuppressWarnings("deprecation")
    public static boolean setClipboard(Context context, String text) {
        android.text.ClipboardManager clipboard = (android.text.ClipboardManager) context
                .getSystemService(Context.CLIPBOARD_SERVICE);
        clipboard.setText(text);
        return true;
    }

    @SuppressWarnings("deprecation")
    public static String getClipboard(Context context) {
        android.text.ClipboardManager clipboard = (android.text.ClipboardManager) context
                .getSystemService(Context.CLIPBOARD_SERVICE);
        return clipboard.getText().toString();
    }
}
