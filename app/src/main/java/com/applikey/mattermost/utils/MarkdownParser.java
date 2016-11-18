package com.applikey.mattermost.utils;

import android.content.Context;

import in.uncod.android.bypass.Bypass;

public final class MarkdownParser {

    private static Bypass mBypass;

    public static CharSequence parseToSpannable(Context context, String markdown) {
        if (mBypass == null) {
            mBypass = new Bypass(context);
        }
        return mBypass.markdownToSpannable(markdown);
    }
}
