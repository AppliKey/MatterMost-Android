/**
 * Copyright (c) 2014 CoderKiss
 * <p>
 * CoderKiss[AT]gmail.com
 */

package com.applikey.mattermost.utils.kissUtils.utils;

import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class ZipUtil {

    private static final String TAG = ZipUtil.class.getSimpleName();

    private final static int BUFFER_SIZE = 8192;

    private ZipUtil() {
    }

    public static boolean zip(String filePath, String zipPath) {
        try {
            BufferedInputStream bis;
            final File file = new File(filePath);
            final FileOutputStream fos = new FileOutputStream(zipPath);
            final ZipOutputStream zos = new ZipOutputStream(new BufferedOutputStream(
                    fos));
            if (file.isDirectory()) {
                int baseLength = file.getParent().length() + 1;
                zipFolder(zos, file, baseLength);
            } else {
                final byte data[] = new byte[BUFFER_SIZE];
                final FileInputStream fis = new FileInputStream(filePath);
                bis = new BufferedInputStream(fis, BUFFER_SIZE);
                final String entryName = file.getName();
                Log.i(TAG, "zip entry " + entryName);
                final ZipEntry entry = new ZipEntry(entryName);
                zos.putNextEntry(entry);
                int count;
                while ((count = bis.read(data, 0, BUFFER_SIZE)) != -1) {
                    zos.write(data, 0, count);
                }
            }
            zos.close();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static boolean unzip(String zipPath, String unzipFolder) {
        if (!FileUtil.exists(zipPath)) {
            LogUtil.e(TAG, "zip path not exists!");
            return false;
        }

        if (!FileUtil.mkdirs(unzipFolder, true)) {
            LogUtil.e(TAG, "failed to create unzip folder.");
            return false;
        }

        try {
            final FileInputStream fin = new FileInputStream(zipPath);
            final ZipInputStream zin = new ZipInputStream(fin);
            ZipEntry ze;
            while ((ze = zin.getNextEntry()) != null) {
                final String entryName = ze.getName();
                LogUtil.d(TAG, "unzip entry " + entryName);

                final String entryPath = unzipFolder + "/" + entryName;
                if (ze.isDirectory()) {
                    FileUtil.mkdirs(entryPath);
                } else {
                    if (!FileUtil.create(entryPath, true)) {
                        continue;
                    }
                    final FileOutputStream fout = new FileOutputStream(entryPath);
                    final byte[] buffer = new byte[BUFFER_SIZE];
                    int len;
                    while ((len = zin.read(buffer)) != -1) {
                        fout.write(buffer, 0, len);
                    }
                    fout.close();
                    zin.closeEntry();
                }
            }
            zin.close();
        } catch (Exception e) {
            LogUtil.e("unzip exception", e);
            return false;
        }
        return true;
    }

    private static void zipFolder(ZipOutputStream zos, File folder,
            int baseLength) throws IOException {
        if (zos == null || folder == null) {
            return;
        }
        final File[] fileList = folder.listFiles();

        if (fileList == null || fileList.length == 0) {
            return;
        }

        for (File file : fileList) {
            if (file.isDirectory()) {
                zipFolder(zos, file, baseLength);
            } else {
                final byte data[] = new byte[BUFFER_SIZE];
                final String unmodifiedFilePath = file.getPath();
                final String realPath = unmodifiedFilePath.substring(baseLength);
                Log.i(TAG, "zip entry " + realPath);
                final FileInputStream fi = new FileInputStream(unmodifiedFilePath);
                final BufferedInputStream bis = new BufferedInputStream(fi,
                        BUFFER_SIZE);
                final ZipEntry entry = new ZipEntry(realPath);
                zos.putNextEntry(entry);
                int count;
                while ((count = bis.read(data, 0, BUFFER_SIZE)) != -1) {
                    zos.write(data, 0, count);
                }
                bis.close();
            }
        }
    }
}
