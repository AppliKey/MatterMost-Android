/**
 * @author dawson dong
 */

package com.applikey.mattermost.utils.kissUtils.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.view.View;

import java.util.List;

public class ActivityUtil {

    private ActivityUtil() {
    }

    public static void chooseImage(Activity activity, String title,
            int requestCode) {
        final Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_PICK);
        final Intent chooser = Intent.createChooser(intent, title);
        startActivityForResult(activity, chooser, requestCode);
    }

    public static boolean startActivity(Context context, Class<?> clazz) {
        if (context == null || clazz == null) {
            return false;
        }
        final Intent intent = new Intent(context, clazz);
        return startActivity(context, intent);
    }

    public static boolean startActivity(Context context, Intent intent) {
        if (context == null || intent == null) {
            return false;
        }

        if (!(context instanceof Activity)) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }

        try {
            context.startActivity(intent);
        } catch (Exception globalException) {
            // catch all exception here
            globalException.printStackTrace();
            return false;
        }
        return true;
    }

    public static boolean startActivityForResult(Activity activity,
            Intent intent, int requestCode) {
        if (activity == null || intent == null) {
            return false;
        }

        try {
            activity.startActivityForResult(intent, requestCode);
        } catch (Exception globalException) {
            globalException.printStackTrace();
            return false;
        }
        return true;
    }

    public static boolean isIntentResolved(Context context, Intent intent) {
        final PackageManager packageManager = context.getPackageManager();
        final List<ResolveInfo> activities = packageManager.queryIntentActivities(
                intent, 0);
        return (activities != null && activities.size() > 0);
    }

    public static Bitmap captureActivity(Activity activity) {
        if (activity == null) {
            return null;
        }
        final View view = activity.getWindow().getDecorView().getRootView();
        return ViewUtil.capture(view);
    }
}
