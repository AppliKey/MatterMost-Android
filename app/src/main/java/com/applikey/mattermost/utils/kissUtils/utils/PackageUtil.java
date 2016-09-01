/**
 * @author dawson dong
 */

package com.applikey.mattermost.utils.kissUtils.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.text.TextUtils;

import com.applikey.mattermost.utils.kissUtils.KissTools;
import com.applikey.mattermost.utils.kissUtils.shell.ShellResult;
import com.applikey.mattermost.utils.kissUtils.shell.ShellUtil;


public class PackageUtil {

    private static final String BOOT_START_PERMISSION = "android.permission.RECEIVE_BOOT_COMPLETED";

    private PackageUtil() {
    }

    // install package normally
    public static boolean install(String filePath) {
        if (!FileUtil.exists(filePath)) {
            return false;
        }
        final Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.parse("file://" + filePath),
                "application/vnd.android.package-archive");
        final Context context = KissTools.getApplicationContext();
        return ActivityUtil.startActivity(context, intent);
    }

    // install package silently
    public static boolean silentInstall(String filePath) {
        if (!FileUtil.exists(filePath)) {
            return false;
        }

        final String command = "pm install " +
                filePath.replace(" ", "\\ ");
        final ShellUtil shell = new ShellUtil();
        shell.prepare(true);
        final ShellResult result = shell.execute(command);
        return (result.getResultCode() == 0);
    }

    // uninstall package normally
    public static boolean uninstall(String packageName) {
        if (TextUtils.isEmpty(packageName)) {
            return false;
        }

        final Intent intent = new Intent(Intent.ACTION_DELETE, Uri.parse("package:"
                + packageName));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        final Context context = KissTools.getApplicationContext();
        return ActivityUtil.startActivity(context, intent);
    }

    public static boolean silentUninstall(String packageName, boolean keepData) {
        if (TextUtils.isEmpty(packageName)) {
            return false;
        }

        final String command = "pm uninstall" +
                (keepData ? " -k " : " ") +
                packageName.replace(" ", "\\ ");
        final ShellUtil shell = new ShellUtil();
        shell.prepare(true);
        final ShellResult result = shell.execute(command);
        return (result.getResultCode() == 0);
    }

    public static PackageInfo getPackageInfo(String packageName) {
        PackageInfo packageInfo = null;
        final Context context = KissTools.getApplicationContext();
        final PackageManager packageManager = context.getPackageManager();
        try {
            final int flags = PackageManager.GET_ACTIVITIES | PackageManager.GET_GIDS
                    | PackageManager.GET_CONFIGURATIONS
                    | PackageManager.GET_INSTRUMENTATION
                    | PackageManager.GET_PERMISSIONS
                    | PackageManager.GET_PROVIDERS
                    | PackageManager.GET_RECEIVERS
                    | PackageManager.GET_SERVICES
                    | PackageManager.GET_SIGNATURES
                    | PackageManager.GET_UNINSTALLED_PACKAGES;
            packageInfo = packageManager.getPackageInfo(packageName, flags);
        } catch (Exception ignored) {
        }
        return packageInfo;
    }

    public static boolean isBootStart(String packageName) {
        final Context context = KissTools.getApplicationContext();
        final PackageManager pm = context.getPackageManager();
        final int flag = pm.checkPermission(BOOT_START_PERMISSION, packageName);
        return (flag == PackageManager.PERMISSION_GRANTED);
    }

//	public static boolean isAutoStart(String packageName) {
//		Context context = KissTools.getApplicationContext();
//		PackageManager pm = context.getPackageManager();
//		Intent intent = new Intent(Intent.ACTION_BOOT_COMPLETED);
//		List<ResolveInfo> resolveInfoList = pm.queryBroadcastReceivers(intent,
//				PackageManager.GET_DISABLED_COMPONENTS);
//		for (ResolveInfo ri : resolveInfoList) {
//			String pn = ri.loadLabel(pm).toString();
//			if (packageName.equals(pn)) {
//				return true;
//			}
//		}
//		return false;
//	}

    public static String getPackageDir(String packageName) {
        String applicationDir = null;
        final PackageInfo pi = getPackageInfo(packageName);
        if (pi != null) {
            applicationDir = pi.applicationInfo.dataDir;
        }
        return applicationDir;
    }
}
