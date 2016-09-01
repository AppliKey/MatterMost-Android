/**
 * Copyright (c) 2014 CoderKiss
 * <p>
 * CoderKiss[AT]gmail.com
 */

package com.applikey.mattermost.utils.kissUtils.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.admin.DevicePolicyManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Build.VERSION_CODES;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

import com.applikey.mattermost.utils.kissUtils.KissTools;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;


public class SystemUtil {

    public static final String TAG = "SystemUtil";
    public static final int MAX_BRIGHTNESS = 255;
    public static final int MIN_BRIGHTNESS = 0;

    public static int getActionBarHeight(Context context) {
        int height = 0;
        final TypedValue tv = new TypedValue();
        final int resId = android.R.attr.actionBarSize;
        if (context.getTheme().resolveAttribute(resId, tv, true)) {
            DisplayMetrics dm = context.getResources().getDisplayMetrics();
            height = TypedValue.complexToDimensionPixelSize(tv.data, dm);
        }
        return height;
    }

    public static int getStatusBarHeight(Context context) {
        int height = 0;
        if (context == null) {
            return height;
        }
        final Resources resources = context.getResources();
        final int resId = resources.getIdentifier("status_bar_height", "dimen",
                "android");
        if (resId > 0) {
            height = resources.getDimensionPixelSize(resId);
        }
        return height;
    }

    public static String getVersion() {
        String version = Build.VERSION.RELEASE;
        final Field[] fields = VERSION_CODES.class.getFields();
        for (Field field : fields) {
            final String fieldName = field.getName();
            int fieldValue = -1;
            try {
                fieldValue = field.getInt(new Object());
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (fieldValue == Build.VERSION.SDK_INT) {
                version = version + " : " + fieldName + " : " + fieldValue;
            }
        }
        return version;
    }

    public static boolean installedApp(String packageName) {
        final Context context = KissTools.getApplicationContext();
        PackageInfo packageInfo;
        if (TextUtils.isEmpty(packageName)) {
            return false;
        }
        final PackageManager packageManager = context.getPackageManager();
        final List<PackageInfo> packageInfos = packageManager.getInstalledPackages(0);
        if (packageInfos == null) {
            return false;
        }
        for (int index = 0; index < packageInfos.size(); index++) {
            packageInfo = packageInfos.get(index);
            final String name = packageInfo.packageName;
            if (packageName.equals(name)) {
                return true;
            }
        }
        return false;
    }

    public static void uninstallApp(String packageName) {
        final Context context = KissTools.getApplicationContext();
        final boolean installed = installedApp(packageName);
        if (!installed) {
            ToastUtil.show("package_not_installed");
            return;
        }

        final boolean isRooted = isRooted();
        if (isRooted) {
            runRootCmd("pm uninstall " + packageName);
        } else {
            final Uri uri = UrlUtil.parse("package:" + packageName);
            final Intent intent = new Intent(Intent.ACTION_DELETE, uri);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
    }

    public static int getBrightness() {
        final Context context = KissTools.getApplicationContext();
        int screenBrightness = 255;
        try {
            screenBrightness = Settings.System.getInt(
                    context.getContentResolver(),
                    Settings.System.SCREEN_BRIGHTNESS);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return screenBrightness;
    }

    public static void setBrightness(int brightness) {
        final Context context = KissTools.getApplicationContext();
        try {
            if (brightness < MIN_BRIGHTNESS) {
                brightness = MIN_BRIGHTNESS;
            }
            if (brightness > MAX_BRIGHTNESS) {
                brightness = MAX_BRIGHTNESS;
            }
            final ContentResolver resolver = context.getContentResolver();
            final Uri uri = Settings.System
                    .getUriFor(Settings.System.SCREEN_BRIGHTNESS);
            Settings.System.putInt(resolver, Settings.System.SCREEN_BRIGHTNESS,
                    brightness);
            resolver.notifyChange(uri, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static int getBrightnessMode() {
        final Context context = KissTools.getApplicationContext();
        int brightnessMode = Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL;
        try {
            brightnessMode = Settings.System.getInt(
                    context.getContentResolver(),
                    Settings.System.SCREEN_BRIGHTNESS_MODE);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return brightnessMode;
    }

    // 1 auto, 0 manual
    public static void setBrightnessMode(int brightnessMode) {
        final Context context = KissTools.getApplicationContext();
        try {
            Settings.System.putInt(context.getContentResolver(),
                    Settings.System.SCREEN_BRIGHTNESS_MODE, brightnessMode);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean isWifiEnabled() {
        final Context context = KissTools.getApplicationContext();
        boolean enabled = false;
        try {
            final WifiManager wifiManager = (WifiManager) context
                    .getSystemService(Context.WIFI_SERVICE);
            enabled = wifiManager.isWifiEnabled();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return enabled;
    }

    @SuppressWarnings("MissingPermission")
    public static void setWifiEnabled(boolean enable) {
        final Context context = KissTools.getApplicationContext();
        try {
            final WifiManager wifiManager = (WifiManager) context
                    .getSystemService(Context.WIFI_SERVICE);
            wifiManager.setWifiEnabled(enable);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean isRooted() {
        final String binaryName = "su";
        boolean rooted = false;
        final String[] places = {"/sbin/", "/system/bin/", "/system/xbin/",
                "/data/local/xbin/", "/data/local/bin/", "/system/sd/xbin/",
                "/system/bin/failsafe/", "/data/local/"};
        for (String where : places) {
            if (new File(where + binaryName).exists()) {
                rooted = true;
                break;
            }
        }
        return rooted;
    }

    public static boolean isSimulator() {
        return (Build.FINGERPRINT != null && Build.FINGERPRINT
                .contains("generic"))
                || "google_sdk".equals(Build.PRODUCT)
                || "google_sdk".equals(Build.MODEL)
                || "goldfish".equals(Build.HARDWARE);
    }

    public static boolean hasPermission(String permission) {
        final Context context = KissTools.getApplicationContext();
        final int result = context.checkCallingOrSelfPermission(permission);
        return (result == PackageManager.PERMISSION_GRANTED);
    }

    public static void lockScreen() {
        final Context context = KissTools.getApplicationContext();
        final DevicePolicyManager deviceManager = (DevicePolicyManager) context
                .getSystemService(Context.DEVICE_POLICY_SERVICE);
        deviceManager.lockNow();
    }

    // <uses-permission android:name="android.permission.INJECT_EVENTS" />
    public static void inputKeyEvent(int keyCode) {
        try {
            runRootCmd("input keyevent " + keyCode);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String runCmd(String cmd) {
        if (TextUtils.isEmpty(cmd)) {
            return null;
        }
        Process process;
        String result = null;

        final String[] commands = {"/system/bin/sh", "-c", cmd};

        try {
            process = Runtime.getRuntime().exec(commands);
            final ByteArrayOutputStream baos = new ByteArrayOutputStream();
            int read;
            final InputStream errIs = process.getErrorStream();
            while ((read = errIs.read()) != -1) {
                baos.write(read);
            }
            baos.write('\n');

            final InputStream inIs = process.getInputStream();
            while ((read = inIs.read()) != -1) {
                baos.write(read);
            }

            final byte[] data = baos.toByteArray();
            result = new String(data);

            LogUtil.d(TAG, "runCmd result " + result);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static String runRootCmd(String cmd) {
        if (TextUtils.isEmpty(cmd)) {
            return null;
        }

        Process process;
        String result = null;

        try {
            final String[] commands = {"su", "-c", cmd};
            process = Runtime.getRuntime().exec(commands);

            final ByteArrayOutputStream baos = new ByteArrayOutputStream();
            int read;
            final InputStream errIs = process.getErrorStream();
            while ((read = errIs.read()) != -1) {
                baos.write(read);
            }
            baos.write('\n');

            final InputStream inIs = process.getInputStream();
            while ((read = inIs.read()) != -1) {
                baos.write(read);
            }

            final byte[] data = baos.toByteArray();
            result = new String(data);

            LogUtil.d(TAG, "runRootCmd result " + result);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static int getDistance(MotionEvent e1, MotionEvent e2) {
        final float x = e1.getX() - e2.getX();
        final float y = e1.getY() - e2.getY();
        return (int) Math.sqrt(x * x + y * y);
    }

    public static long getMaxMemory() {
        final Runtime runtime = Runtime.getRuntime();
        final long maxMemory = runtime.maxMemory();
        LogUtil.d(TAG, "application max memory " + maxMemory);
        return maxMemory;
    }

    public static void restartApplication(Class<?> clazz) {
        final Context context = KissTools.getApplicationContext();
        final Intent intent = new Intent(context, clazz);
        final int pendingIntentId = 198964;
        final PendingIntent pendingIntent = PendingIntent.getActivity(context,
                pendingIntentId, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        final AlarmManager am = (AlarmManager) context
                .getSystemService(Context.ALARM_SERVICE);
        am.set(AlarmManager.RTC, System.currentTimeMillis() + 500,
                pendingIntent);
        System.exit(0);
    }

    public static void killApplication(String packageName) {
        try {
            final Context context = KissTools.getApplicationContext();
            final ActivityManager activityManager = (ActivityManager) context
                    .getSystemService(Context.ACTIVITY_SERVICE);
            activityManager.killBackgroundProcesses(packageName);
            final Method forceStopPackage = activityManager.getClass()
                    .getDeclaredMethod("forceStopPackage", String.class);
            forceStopPackage.setAccessible(true);
            forceStopPackage.invoke(activityManager, packageName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean isMainThread() {
        return Looper.getMainLooper() == Looper.myLooper();
    }

    public static void runOnMain(Runnable runnable) {
        if (runnable == null) {
            return;
        }

        if (isMainThread()) {
            runnable.run();
        } else {
            final Handler handler = new Handler(Looper.getMainLooper());
            handler.post(runnable);
        }
    }

    public static void runOnMain(Runnable runnable, long delay) {
        if (runnable == null) {
            return;
        }
        final Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(runnable, delay);
    }

    @TargetApi(VERSION_CODES.KITKAT)
    public static void hideSystemUI(Activity activity) {
        final View decorView = activity.getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE);
    }

    @TargetApi(VERSION_CODES.KITKAT)
    public static void showSystemUI(Activity activity) {
        final View decorView = activity.getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }
}
