package com.applikey.mattermost.utils;

import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StyleSpan;

/**
 * @author Anatoliy Chub
 */

public class SpanUtils {

    private SpanUtils() {
    }

    public static Spannable createSpannableBoldString(String text, String search) {
        final int searchLength = search.length();
        final Spannable spannableString = new SpannableString(text);
        int index = text.indexOf(search);
        if(index < 0) {
            return spannableString;
        }
        spannableString.setSpan(new StyleSpan(Typeface.BOLD), index, index + searchLength, 0);
        while(index >= 0) {
            spannableString.setSpan(new StyleSpan(Typeface.BOLD), index, index + searchLength, 0);
            index = text.indexOf(search, index + 1);
        }
        return spannableString;

    }
}
