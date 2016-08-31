/**
 * Copyright (c) 2014 CoderKiss
 * <p>
 * CoderKiss[AT]gmail.com
 */

package com.applikey.mattermost.utils.kissUtils.utils;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;

import com.applikey.mattermost.utils.kissUtils.KissTools;

import java.io.File;


public class MediaUtil {

    public static final String TAG = "MediaUtil";

    public static String getAppDir() {
        String appDir = null;
        Context context = KissTools.getApplicationContext();
        if (mediaMounted()) {
            File fileDir = context.getExternalFilesDir("");
            if (fileDir.exists()) {
                appDir = FileUtil.getParent(fileDir);
            }
        }

        if (appDir == null) {
            File fileDir = context.getFilesDir();
            if (fileDir.exists()) {
                appDir = FileUtil.getParent(fileDir);
            }
        }
        return appDir;
    }

    public static String getFileDir(String dirName) {
        String appDir = getAppDir();
        if (TextUtils.isEmpty(appDir)) {
            return null;
        }

        if (TextUtils.isEmpty(dirName)) {
            return appDir;
        }
        String fileDir = appDir + "/" + dirName;
        return fileDir;
    }

    public static boolean mediaMounted() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        } else {
            return false;
        }
    }
}
