package com.applikey.skeletonproject.utils;


public class DateUtils {

    public static CharSequence getRelatedDate(long timeMs) {
        return android.text.format.DateUtils.getRelativeTimeSpanString(timeMs,
                System.currentTimeMillis(), 0,
                android.text.format.DateUtils.FORMAT_ABBREV_ALL);
    }

}
