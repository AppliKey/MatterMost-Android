package com.applikey.mattermost.utils;

import android.os.Build;
import android.text.Html;
import android.text.Spanned;
import android.widget.TextView;

public final class TextViewUtils {

    private TextViewUtils() {
    }

    public static void setHtmlText(TextView tv, String text) {
        final Spanned spanned = fromHtml(text);
        tv.setText(spanned);
    }

    @SuppressWarnings("deprecation")
    private static Spanned fromHtml(String source) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return Html.fromHtml(source, Html.FROM_HTML_MODE_LEGACY);
        } else {
            return Html.fromHtml(source);
        }
    }
}
