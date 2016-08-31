/**
 * Copyright (c) 2014 CoderKiss
 * <p>
 * CoderKiss[AT]gmail.com
 */

package com.applikey.mattermost.utils.kissUtils.utils;

import android.os.Bundle;
import android.text.TextUtils;

import java.util.Set;

public class BundleUtil {

    public static final String TAG = BundleUtil.class.getSimpleName();

    private BundleUtil() {
    }

    public static String getString(Bundle bundle, String key) {
        return getString(bundle, key, "");
    }

    public static boolean getBoolean(Bundle bundle, String key, boolean df) {
        return getValue(bundle, key, df);
    }

    public static int getInt(Bundle bundle, String key) {
        return getInt(bundle, key, 0);
    }

    public static int getInt(Bundle bundle, String key, int df) {
        return getValue(bundle, key, df);
    }

    public static double getDouble(Bundle bundle, String key) {
        return getDouble(bundle, key, 0.0);
    }

    public static double getDouble(Bundle bundle, String key, double df) {
        return getValue(bundle, key, df);
    }

    @SuppressWarnings("unchecked")
    public static <T> T getValue(Bundle bundle, String key, T df) {
        if (bundle == null || TextUtils.isEmpty(key)) {
            return df;
        }

        if (df == null) {
            return df;
        }

        if (!bundle.containsKey(key)) {
            return df;
        }
        T value = df;
        final Object obj = bundle.get(key);
        if (obj != null && value.getClass().isAssignableFrom(obj.getClass())) {
            value = (T) obj;
        } else {
            LogUtil.w(TAG, "[key] " + key + " [value] " + obj);
        }
        return value;
    }

    public static void showContent(Bundle bundle) {
        if (bundle == null) {
            return;
        }

        final StringBuilder builder = new StringBuilder();
        builder.append("bundle content\n");
        final Set<String> keys = bundle.keySet();
        for (String key : keys) {
            builder.append("[").append(key).append("]<->[").append(bundle.get(key)).append("]\n");
        }
        final String content = builder.toString();
        LogUtil.d(TAG, content);
    }

    private static String getString(Bundle bundle, String key, String df) {
        if (df == null) {
            df = "";
        }
        return getValue(bundle, key, df);
    }
}
