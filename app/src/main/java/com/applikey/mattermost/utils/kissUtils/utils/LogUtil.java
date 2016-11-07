/**
 * Copyright (c) 2014 CoderKiss
 * <p>
 * CoderKiss[AT]gmail.com
 */

package com.applikey.mattermost.utils.kissUtils.utils;

import android.text.TextUtils;
import android.util.Log;

import com.applikey.mattermost.utils.kissUtils.KissTools;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;


public class LogUtil {

    public final static String TAG = LogUtil.class.getSimpleName();
    public final static int LOG_VERBOSE = Log.VERBOSE;
    public final static int LOG_DEBUG = Log.DEBUG;
    public final static int LOG_INFO = Log.INFO;
    public final static int LOG_WARN = Log.WARN;
    public final static int LOG_ERROR = Log.ERROR;
    public final static int LOG_ASSERT = Log.ASSERT;
    public final static int LOG_NONE = Log.ASSERT + 1;
    public final static int LOG_DEFAULT = LOG_VERBOSE;
    private static final String LOG_FORMAT = "%1$s\n%2$s";
    private final static int LOG_MAX_MEM = 16384; // 16k
    private final static int LOG_MAX_LEN = 2048; // 2k
    private final static long LOG_FILE_LEN = 4194304; // 4MB
    private static int mLogPriority = LOG_DEFAULT;
    private static boolean mFileLog = false;
    private static StringBuilder mStringBuilder = new StringBuilder();

    private LogUtil() {
    }

    public static int getLogPriority() {
        return mLogPriority;
    }

    public static void setLogPriority(int priority) {
        mLogPriority = priority;
    }

    public static void setFileLog(boolean fileLog) {
        mFileLog = fileLog;
    }

    public static void v(String tag, String msg) {
        log(Log.VERBOSE, null, tag, msg);
    }

    public static void d(String tag, String msg) {
        log(LOG_DEBUG, null, tag, msg);
    }

    public static void i(String tag, String msg) {
        log(LOG_INFO, null, tag, msg);
    }

    public static void w(String tag, String msg) {
        log(LOG_WARN, null, tag, msg);
    }

    public static void e(String tag, String msg) {
        log(LOG_ERROR, null, tag, msg);
    }

    public static void e(Throwable ex) {
        log(LOG_ERROR, ex, null, null);
    }

    public static void e(String tag, Throwable ex) {
        log(LOG_ERROR, ex, tag, null);
    }

    public static void e(String tag, String msg, Throwable ex) {
        log(LOG_ERROR, ex, tag, msg);
    }

    public static void destroy() {
        writeLog();
    }

    private static void log(int priority, Throwable ex, String tag,
            String message) {
        if (priority < mLogPriority) {
            return;
        }

        if (ex != null) {
            message = message == null ? ex.getMessage() : message;
            final String logBody = Log.getStackTraceString(ex);
            message = String.format(LOG_FORMAT, message, logBody);
        }

        String text = message;
        int partLen;
        int length = text.length();
        while (length > 0) {
            partLen = length > LOG_MAX_LEN ? LOG_MAX_LEN : length;
            final String partLog = text.substring(0, partLen);
            Log.println(priority, tag, partLog);
            text = text.substring(partLen);
            length = text.length();
        }

        if (mFileLog) {
            synchronized (mStringBuilder) {
                mStringBuilder.append(message);
                final int builderLen = mStringBuilder.length();
                if (builderLen > LOG_MAX_MEM) {
                    writeLog();
                }
            }
        }
    }

    private static void writeLog() {
        new Thread() {
            public void run() {
                write2LogFile();
            }
        }.start();
    }

    private static void write2LogFile() {
        String log;
        synchronized (mStringBuilder) {
            log = mStringBuilder.toString();
            int length = mStringBuilder.length();
            mStringBuilder.delete(0, length);
        }

        final String logFolder = getLogFolder();
        if (TextUtils.isEmpty(logFolder)) {
            return;
        }

        final String logPath = logFolder + "/bestutils.log";
        final File logFile = new File(logPath);
        if (!logFile.exists()) {
            FileUtil.create(logPath);
        }
        if (logFile.length() >= LOG_FILE_LEN) {
            resizeLogFile(logFile);
        }

        try {
            final FileOutputStream fos = new FileOutputStream(logFile, true);
            fos.write(log.getBytes());
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void resizeLogFile(File logFile) {
        BufferedOutputStream bos = null;
        RandomAccessFile raf = null;
        try {
            final String logFolder = getLogFolder();
            if (TextUtils.isEmpty(logFolder)) {
                return;
            }
            final String tempLog = logFolder + "/temp.log";
            FileUtil.delete(tempLog);
            if (!FileUtil.create(tempLog)) {
                return;
            }

            bos = new BufferedOutputStream(new FileOutputStream(tempLog));
            raf = new RandomAccessFile(logFile, "r");
            raf.seek(LOG_FILE_LEN / 2);
            int readLen;
            final byte[] readBuff = new byte[1024];
            while ((readLen = raf.read(readBuff)) != -1) {
                bos.write(readBuff, 0, readLen);
            }

            //noinspection ResultOfMethodCallIgnored
            logFile.delete();
            FileUtil.move(tempLog, logFile.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (bos != null) {
                    bos.close();
                }
                if (raf != null) {
                    raf.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static String getLogFolder() {
        final File cacheFolder = KissTools.getApplicationContext().getExternalCacheDir();
        //noinspection UnnecessaryLocalVariable
        final String logFolder = cacheFolder.getAbsolutePath() + "/KissTools";
        return logFolder;
    }
}
