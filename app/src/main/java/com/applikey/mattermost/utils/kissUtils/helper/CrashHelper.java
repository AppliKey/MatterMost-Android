/**
 * @author dawson dong
 */

package com.applikey.mattermost.utils.kissUtils.helper;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Application;
import android.os.Build;
import android.os.Looper;

import com.applikey.mattermost.utils.kissUtils.utils.FileUtil;
import com.applikey.mattermost.utils.kissUtils.utils.LogUtil;
import com.applikey.mattermost.utils.kissUtils.utils.MediaUtil;
import com.applikey.mattermost.utils.kissUtils.utils.StringUtil;
import com.applikey.mattermost.utils.kissUtils.utils.TimeUtil;
import com.applikey.mattermost.utils.kissUtils.utils.ToastUtil;

import java.lang.Thread.UncaughtExceptionHandler;


@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
public class CrashHelper implements UncaughtExceptionHandler {

    public static final String TAG = CrashHelper.class.getSimpleName();

    @SuppressLint("NewApi")
    public static void init(Application application) {
        if (Thread.getDefaultUncaughtExceptionHandler() instanceof CrashHelper) {
            return;
        }

        final CrashHelper helper = new CrashHelper();
        Thread.setDefaultUncaughtExceptionHandler(helper);
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        final String name = thread.getName();

        final String crashMessage = StringUtil.stringify(ex);
        final StringBuilder builder = new StringBuilder()
                .append("crash in thread ")
                .append(name)
                .append("crash detail message:\n")
                .append(crashMessage);

        final long time = System.currentTimeMillis();
        final String format = TimeUtil.FILE_FORMAT;
        final String fileName = TimeUtil.format(time, format) + ".txt";
        final String crashFolder = MediaUtil.getFileDir("crash_log");
        final String filePath = crashFolder + "/" + fileName;
        LogUtil.e(TAG, builder.toString());
        FileUtil.write(filePath, builder.toString());
        showMessage("application cashed!");
        try {
            Thread.sleep(2500);
        } catch (Exception ignored) {
        }
        System.exit(1);
    }

    private void showMessage(final String message) {
        new Thread() {
            @Override
            public void run() {
                Looper.prepare();
                ToastUtil.show(message);
                Looper.loop();
            }
        }.start();
    }
}
