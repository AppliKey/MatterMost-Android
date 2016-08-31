/**
 * Copyright (c) 2014 CoderKiss
 * <p>
 * CoderKiss[AT]gmail.com
 */

package com.applikey.mattermost.utils.kissUtils.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;

public class CommonUtil {

    public static final String TAG = CommonUtil.class.getSimpleName();

    private CommonUtil() {
    }

    public static void showIME(Context context) {
        final InputMethodManager imm = (InputMethodManager) context
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,
                InputMethodManager.HIDE_NOT_ALWAYS);
    }

    public static void hideIME(Context context, @Nullable View view) {
        if (view == null) {
            return;
        }
        final InputMethodManager imm = (InputMethodManager) context
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static String getMimeType(String url) {
        final String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        //noinspection UnnecessaryLocalVariable
        final String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(
                extension);
        return mimeType;
    }

    public static int getUid(Context context) {
        try {
            final PackageManager pm = context.getPackageManager();
            //noinspection WrongConstant // TODO Resolve
            final ApplicationInfo ai = pm.getApplicationInfo(
                    context.getPackageName(), PackageManager.GET_ACTIVITIES);
            return ai.uid;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
