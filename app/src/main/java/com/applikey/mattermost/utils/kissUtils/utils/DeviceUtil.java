/**
 * Copyright (c) 2014 CoderKiss
 * <p>
 * CoderKiss[AT]gmail.com
 */

package com.applikey.mattermost.utils.kissUtils.utils;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.SensorManager;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
import android.view.WindowManager;

import com.applikey.mattermost.utils.kissUtils.KissTools;
import com.applikey.mattermost.utils.kissUtils.shell.ShellResult;
import com.applikey.mattermost.utils.kissUtils.shell.ShellUtil;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

//hardware related functions
public class DeviceUtil {

    public static final String TAG = "DeviceUtil";

    private DeviceUtil() {
    }

    @SuppressWarnings("deprecation")
    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    public static String getBuildInfo() {
        final StringBuilder sb = new StringBuilder();
        // alpha sort
        sb.append("board: ").append(Build.BOARD).append("\nbrand: ").append(Build.BRAND)
                .append("\ncpu_abi: ").append(Build.CPU_ABI)
                .append("\ncpu_abi2: ").append(Build.CPU_ABI2)
                .append("\ndevice: ").append(Build.DEVICE)
                .append("\ndisplay: ").append(Build.DISPLAY)
                .append("\nfingerprint: ").append(Build.FINGERPRINT)
                .append("\nhardware: ").append(Build.HARDWARE)
                .append("\nid: ").append(Build.ID)
                .append("\nmanufacture: ").append(Build.MANUFACTURER)
                .append("\nmodel: ").append(Build.MODEL)
                .append("\nproduct: ").append(Build.PRODUCT)
                .append("\nradio: ").append(Build.RADIO)
                .append("\nsdk_int: ").append(Build.VERSION.SDK_INT);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            sb.append("\nserial: ").append(Build.SERIAL);
        }
        sb.append("\ntype: ").append(Build.TYPE).append("\ntags: ").append(Build.TAGS);

        return sb.toString();
    }

    public static String getProductInfo() {
        final String brand = Build.BRAND;
        final String model = Build.MODEL;
        final String manufacture = Build.MANUFACTURER;
        //noinspection UnnecessaryLocalVariable
        final String finalInfo = brand + " " + model + "/" + manufacture;
        return finalInfo;
    }

    public static Point getScreenSize() {
        final Context context = KissTools.getApplicationContext();
        final WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        final Display display = wm.getDefaultDisplay();

        final DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        // since SDK_INT = 1;
        int widthPixels = metrics.widthPixels;
        int heightPixels = metrics.heightPixels;
        // includes window decorations (status bar bar/menu bar)
        if (Build.VERSION.SDK_INT >= 14 && Build.VERSION.SDK_INT < 17) {
            try {
                widthPixels = (Integer) Display.class.getMethod("getRawWidth")
                        .invoke(display);
                heightPixels = (Integer) Display.class
                        .getMethod("getRawHeight").invoke(display);
            } catch (Exception ignored) {
            }
        }
        // includes window decorations (status bar bar/menu bar)
        else if (Build.VERSION.SDK_INT >= 17) {
            try {
                final Point realSize = new Point();
                Display.class.getMethod("getRealSize", Point.class).invoke(
                        display, realSize);
                widthPixels = realSize.x;
                heightPixels = realSize.y;
            } catch (Exception ignored) {
            }
        }
        return new Point(widthPixels, heightPixels);
    }

    public static float getScreenDensity() {
        final Context context = KissTools.getApplicationContext();
        final Resources resources = context.getResources();
        final DisplayMetrics dm = resources.getDisplayMetrics();
        //noinspection UnnecessaryLocalVariable
        final float screenDensity = dm.density;
        return screenDensity;
    }

    public static int getScreenDensityDpi() {
        final Context context = KissTools.getApplicationContext();
        final Resources resources = context.getResources();
        final DisplayMetrics dm = resources.getDisplayMetrics();
        //noinspection UnnecessaryLocalVariable
        final int screenDensityDpi = dm.densityDpi;
        return screenDensityDpi;
    }

    public static String getBluetoothMac() {
        BluetoothAdapter adapter;
        String bluetoothMac = null;
        try {
            adapter = BluetoothAdapter.getDefaultAdapter();
            //noinspection MissingPermission // TODO Resolve if needed
            bluetoothMac = adapter.getAddress();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (!TextUtils.isEmpty(bluetoothMac)) {
            bluetoothMac = bluetoothMac.toLowerCase();
        }
        return bluetoothMac;
    }

    public static String getWlanMac() {
        final Context context = KissTools.getApplicationContext();
        String wlanMac = null;
        try {
            final WifiManager wm = (WifiManager) context
                    .getSystemService(Context.WIFI_SERVICE);
            wlanMac = wm.getConnectionInfo().getMacAddress();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (!TextUtils.isEmpty(wlanMac)) {
            wlanMac = wlanMac.toLowerCase();
        }
        return wlanMac;
    }

    public static String getAndroidId() {
        final Context context = KissTools.getApplicationContext();
        String androidID = null;
        try {
            androidID = Secure.getString(context.getContentResolver(),
                    Secure.ANDROID_ID);
        } catch (Exception ignored) {
        }
        return androidID;
    }

    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    public static String getSerialId() {
        return Build.SERIAL;
    }

    public static String getDeviceId() {
        final Context context = KissTools.getApplicationContext();
        String deviceIMEI = null;
        try {
            final TelephonyManager teleManager = (TelephonyManager) context
                    .getSystemService(Context.TELEPHONY_SERVICE);
            deviceIMEI = teleManager.getDeviceId();
        } catch (Exception ignored) {
        }
        return deviceIMEI;
    }

    public static double getScreenInches() {
        final Context context = KissTools.getApplicationContext();
        double screenInches = -1;
        try {
            final Resources resources = context.getResources();
            final DisplayMetrics dm = resources.getDisplayMetrics();
            final Point point = getScreenSize();
            final double width = Math.pow(point.x / dm.xdpi, 2);
            final double height = Math.pow(point.y / dm.ydpi, 2);
            screenInches = Math.sqrt(width + height);
        } catch (Exception ignored) {
        }
        return screenInches;
    }

    public static int dp2px(int dip) {
        final Context context = KissTools.getApplicationContext();
        final Resources resources = context.getResources();
        //noinspection UnnecessaryLocalVariable
        int px = Math
                .round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                        dip, resources.getDisplayMetrics()));
        return px;
    }

    public static int px2dp(int px) {
        final Context context = KissTools.getApplicationContext();
        final DisplayMetrics displayMetrics = context.getResources()
                .getDisplayMetrics();
        //noinspection UnnecessaryLocalVariable
        final int dp = Math.round(px
                / (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return dp;
    }

    public static int sp2px(float sp) {
        final Context context = KissTools.getApplicationContext();
        final float scale = context.getResources().getDisplayMetrics().scaledDensity;
        //noinspection UnnecessaryLocalVariable
        int px = Math.round(sp * scale);
        return px;
    }

    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    public static boolean hasFrontCamera() {
        final int number = Camera.getNumberOfCameras();
        for (int index = 0; index < number; index++) {
            final CameraInfo ci = new CameraInfo();
            Camera.getCameraInfo(index, ci);
            if (ci.facing == CameraInfo.CAMERA_FACING_FRONT) {
                return true;
            }
        }
        return false;
    }

    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    public static boolean hasBackCamera() {
        final int number = Camera.getNumberOfCameras();
        for (int index = 0; index < number; index++) {
            final CameraInfo ci = new CameraInfo();
            Camera.getCameraInfo(index, ci);
            if (ci.facing == CameraInfo.CAMERA_FACING_BACK) {
                return true;
            }
        }
        return false;
    }

    public static boolean hasSensor(int type) {
        final Context context = KissTools.getApplicationContext();
        final SensorManager manager = (SensorManager) context
                .getSystemService(Context.SENSOR_SERVICE);
        return manager.getDefaultSensor(type) != null;
    }

    public static long getTotalMemory() {
        final String memInfoPath = "/proc/meminfo";
        String str2;
        long initial_memory;
        try {
            final FileReader fr = new FileReader(memInfoPath);
            final BufferedReader bf = new BufferedReader(fr, 8192);
            str2 = bf.readLine();// total memory size
            final String[] as = str2.split("\\s+");
            initial_memory = Integer.valueOf(as[1]).intValue() * 1024;
            bf.close();
            return initial_memory;
        } catch (IOException e) {
            return -1;
        }
    }

    public static boolean screenCap(String localPath) {
        if (FileUtil.exists(localPath)) {
            FileUtil.delete(localPath);
        }

        if (!FileUtil.create(localPath)) {
            return false;
        }

        final ShellUtil shell = new ShellUtil();
        // failed to run 'su' command
        if (shell.prepare(true)) {
            return false;
        }
        final String command = "screencap -p '" + localPath + ",";
        final ShellResult result = shell.execute(command);
        return (result != null && result.success());
    }
}
