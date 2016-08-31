/**
 * Copyright (c) 2014 CoderKiss
 * <p>
 * CoderKiss[AT]gmail.com
 */

package com.applikey.mattermost.utils.kissUtils.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.widget.Toast;

import com.applikey.mattermost.utils.kissUtils.KissTools;


public class ToastUtil {

    public static Toast mToast;

    public static void show(int resId) {
        initToast();
        if (resId < 0 || mToast == null) {
            return;
        }

        mToast.setText(resId);
        mToast.show();
    }

    public static void show(String message) {
        initToast();
        if (TextUtils.isEmpty(message) || mToast == null) {
            return;
        }

        mToast.setText(message);
        mToast.show();
    }

    public static void dismiss() {
        if (mToast == null) {
            return;
        }
        mToast.cancel();
    }

    @SuppressLint("ShowToast")
    private static void initToast() {
        if (mToast != null) {
            return;
        }
        Context context = KissTools.getApplicationContext();
        if (context == null) {
            return;
        }
        mToast = Toast.makeText(context, "", Toast.LENGTH_SHORT);
    }
}
