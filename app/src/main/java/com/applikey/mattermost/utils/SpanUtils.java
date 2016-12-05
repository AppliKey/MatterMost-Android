package com.applikey.mattermost.utils;

import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StyleSpan;

public class SpanUtils {

    private static final String TAG = SpanUtils.class.getSimpleName();

    private SpanUtils() {
    }

    public static Spannable createSpannableBoldString(@NonNull String fullText, @NonNull String searchText) {
        final String text = fullText.toLowerCase();
        final String search = searchText.toLowerCase();
        final int searchLength = search.length();
        final Spannable spannableString = new SpannableString(fullText);
        int index = text.indexOf(search);
        if (index < 0) {
            return spannableString;
        }
        spannableString.setSpan(new StyleSpan(Typeface.BOLD), index, index + searchLength, 0);
        while (index >= 0) {
            spannableString.setSpan(new StyleSpan(Typeface.BOLD), index, index + searchLength, 0);
            index = text.indexOf(search, index + 1);
        }
        return spannableString;
    }
}