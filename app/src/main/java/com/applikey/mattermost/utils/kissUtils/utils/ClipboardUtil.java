/**
 * Copyright (c) 2014 CoderKiss
 * <p>
 * CoderKiss[AT]gmail.com
 */

package com.applikey.mattermost.utils.kissUtils.utils;

import android.content.Context;
import android.text.ClipboardManager;

public class ClipboardUtil {

    public static final String TAG = ClipboardUtil.class.getSimpleName();

    private ClipboardUtil() {
    }

    @SuppressWarnings("deprecation")
    public static boolean setClipboard(Context context, String text) {
        final ClipboardManager clipboard = (ClipboardManager) context
                .getSystemService(Context.CLIPBOARD_SERVICE);
        clipboard.setText(text);
        return true;
    }

    @SuppressWarnings("deprecation")
    public static String getClipboard(Context context) {
        final ClipboardManager clipboard = (ClipboardManager) context
                .getSystemService(Context.CLIPBOARD_SERVICE);
        return clipboard.getText().toString();
    }
}
