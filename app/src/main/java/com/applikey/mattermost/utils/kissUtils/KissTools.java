/**
 * Copyright (c) 2014 CoderKiss
 * <p>
 * CoderKiss[AT]gmail.com
 */

package com.applikey.mattermost.utils.kissUtils;

import android.content.Context;

import java.lang.ref.WeakReference;
import java.security.InvalidParameterException;

public class KissTools {

    public static final String TAG = "KissTools";
    private static WeakReference<Context> contextRef;

    private KissTools() {
    }

    public static void setContext(Context context) {
        if (context == null) {
            throw new InvalidParameterException("Invalid context parameter!");
        }

        final Context appContext = context.getApplicationContext();
        contextRef = new WeakReference<>(appContext);
    }

    public static Context getApplicationContext() {
        final Context context = contextRef.get();
        if (context == null) {
            throw new InvalidParameterException("Context parameter not set!");
        } else {
            return context;
        }
    }
}
