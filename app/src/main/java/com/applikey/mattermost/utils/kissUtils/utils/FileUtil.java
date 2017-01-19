/**
 * Copyright (c) 2014 CoderKiss
 * <p>
 * CoderKiss[AT]gmail.com
 */

package com.applikey.mattermost.utils.kissUtils.utils;

import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.webkit.MimeTypeMap;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.security.MessageDigest;

public class FileUtil {

    private static final String TAG = FileUtil.class.getSimpleName();
    private static final int IO_BUFFER_SIZE = 16384;

    private FileUtil() {
    }

    public static boolean create(String absPath) {
        return create(absPath, false);
    }

    public static boolean create(String absPath, boolean force) {
        if (TextUtils.isEmpty(absPath)) {
            return false;
        }

        if (exists(absPath)) {
            return true;
        }

        final String parentPath = getParent(absPath);
        mkdirs(parentPath, force);

        try {
            final File file = new File(absPath);
            return file.createNewFile();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean mkdirs(String absPath) {
        return mkdirs(absPath, false);
    }

    public static boolean mkdirs(String absPath, boolean force) {
        final File file = new File(absPath);
        if (exists(absPath) && !isFolder(absPath)) {
            if (!force) {
                return false;
            } else {
                delete(file);
            }
        }
        try {
            //noinspection ResultOfMethodCallIgnored
            file.mkdirs();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return exists(file);
    }

    public static boolean move(String srcPath, String dstPath) {
        return move(srcPath, dstPath, false);
    }

    public static boolean move(String srcPath, String dstPath, boolean force) {
        if (TextUtils.isEmpty(srcPath) || TextUtils.isEmpty(dstPath)) {
            return false;
        }

        if (!exists(srcPath)) {
            return false;
        }

        if (exists(dstPath)) {
            if (!force) {
                return false;
            } else {
                delete(dstPath);
            }
        }

        try {
            final File srcFile = new File(srcPath);
            final File dstFile = new File(dstPath);
            return srcFile.renameTo(dstFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean delete(String absPath) {
        if (TextUtils.isEmpty(absPath)) {
            return false;
        }

        final File file = new File(absPath);
        return delete(file);
    }

    public static boolean delete(File file) {
        if (!exists(file)) {
            return true;
        }

        if (file.isFile()) {
            return file.delete();
        }

        boolean result = true;
        final File files[] = file.listFiles();
        for (int index = 0; index < files.length; index++) {
            result |= delete(files[index]);
        }
        result |= file.delete();

        return result;
    }

    public static boolean exists(String absPath) {
        if (TextUtils.isEmpty(absPath)) {
            return false;
        }
        final File file = new File(absPath);
        return exists(file);
    }

    public static boolean exists(File file) {
        return file == null ? false : file.exists();
    }

    public static boolean childOf(String childPath, String parentPath) {
        if (TextUtils.isEmpty(childPath) || TextUtils.isEmpty(parentPath)) {
            return false;
        }
        childPath = cleanPath(childPath);
        parentPath = cleanPath(parentPath);
        if (childPath.startsWith(parentPath + File.separator)) {
            return true;
        }
        return false;
    }

    public static int childCount(String absPath) {
        if (!exists(absPath)) {
            return 0;
        }
        final File file = new File(absPath);
        final File[] children = file.listFiles();
        if (children == null || children.length == 0) {
            return 0;
        }
        return children.length;
    }

    public static String cleanPath(String absPath) {
        if (TextUtils.isEmpty(absPath)) {
            return absPath;
        }
        try {
            File file = new File(absPath);
            absPath = file.getCanonicalPath();
        } catch (Exception ignored) {
        }
        return absPath;
    }

    public static long size(String absPath) {
        if (absPath == null) {
            return 0;
        }
        final File file = new File(absPath);
        return size(file);
    }

    public static long size(File file) {
        if (!exists(file)) {
            return 0;
        }

        long length = 0;
        if (isFile(file)) {
            length = file.length();
            return length;
        }

        final File files[] = file.listFiles();
        if (files == null || files.length == 0) {
            return length;
        }

        final int size = files.length;
        for (File child : files) {
            length += size(child);
        }
        return length;
    }

    public static boolean copy(String srcPath, String dstPath) {
        return copy(srcPath, dstPath, false);
    }

    public static boolean copy(String srcPath, String dstPath, boolean force) {
        if (TextUtils.isEmpty(srcPath) || TextUtils.isEmpty(dstPath)) {
            return false;
        }

        // check if copy source equals destination
        if (srcPath.equals(dstPath)) {
            return true;
        }

        // check if source file exists or is a directory
        if (!exists(srcPath) || !isFile(srcPath)) {
            return false;
        }

        // delete old content
        if (exists(dstPath)) {
            if (!force) {
                return false;
            } else {
                delete(dstPath);
            }
        }
        if (!create(dstPath)) {
            return false;
        }

        FileInputStream in;
        FileOutputStream out;

        // get streams
        try {
            in = new FileInputStream(srcPath);
            out = new FileOutputStream(dstPath);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return copy(in, out);
    }

    public static boolean copy(InputStream is, OutputStream os) {
        if (is == null || os == null) {
            return false;
        }

        try {
            final byte[] buffer = new byte[IO_BUFFER_SIZE];
            int length;
            while ((length = is.read(buffer)) != -1) {
                os.write(buffer, 0, length);
            }
            os.flush();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                is.close();
            } catch (Exception ignored) {
            }
            try {
                os.close();
            } catch (Exception ignored) {

            }
        }
        return true;
    }

    public static boolean isFile(String absPath) {
        boolean exists = exists(absPath);
        if (!exists) {
            return false;
        }

        final File file = new File(absPath);
        return isFile(file);
    }

    public static boolean isFile(File file) {
        return file != null && file.isFile();
    }

    public static boolean isFolder(String absPath) {
        boolean exists = exists(absPath);
        if (!exists) {
            return false;
        }

        final File file = new File(absPath);
        return file.isDirectory();
    }

    public static boolean isSymlink(File file) {
        if (file == null) {
            return false;
        }

        boolean isSymlink = false;
        try {
            File canon = null;
            if (file.getParent() == null) {
                canon = file;
            } else {
                final File canonDir = file.getParentFile().getCanonicalFile();
                canon = new File(canonDir, file.getName());
            }
            isSymlink = !canon.getCanonicalFile().equals(
                    canon.getAbsoluteFile());
        } catch (Exception ignored) {
        }
        return isSymlink;
    }

    public static String getName(File file) {
        return file == null ? null : getName(file.getAbsolutePath());
    }

    public static String getName(String absPath) {
        if (TextUtils.isEmpty(absPath)) {
            return absPath;
        }

        String fileName = null;
        final int index = absPath.lastIndexOf("/");
        if (index >= 0 && index < (absPath.length() - 1)) {
            fileName = absPath.substring(index + 1, absPath.length());
        }
        return fileName;
    }

    public static String getParent(File file) {
        return file == null ? null : file.getParent();
    }

    public static String getParent(String absPath) {
        if (TextUtils.isEmpty(absPath)) {
            return null;
        }
        absPath = cleanPath(absPath);
        final File file = new File(absPath);
        return getParent(file);
    }

    public static String getStem(File file) {
        return file == null ? null : getStem(file.getName());
    }

    public static String getStem(String fileName) {
        if (TextUtils.isEmpty(fileName)) {
            return null;
        }

        final int index = fileName.lastIndexOf(".");
        if (index > 0) {
            return fileName.substring(0, index);
        } else {
            return "";
        }
    }

    public static String getExtension(File file) {
        return file == null ? null : getExtension(file.getName());
    }

    public static String getExtension(String fileName) {
        if (TextUtils.isEmpty(fileName)) {
            return "";
        }

        final int index = fileName.lastIndexOf('.');
        if (index < 0 || index >= (fileName.length() - 1)) {
            return "";
        }
        return fileName.substring(index + 1);
    }

    public static String getMimeType(File file) {
        if (file == null) {
            return "*/*";
        }
        final String fileName = file.getName();
        return getMimeType(fileName);
    }

    public static String getMimeType(String fileName) {
        if (TextUtils.isEmpty(fileName)) {
            return "*/*";
        }
        final String extension = getExtension(fileName);
        final MimeTypeMap map = MimeTypeMap.getSingleton();
        final String type = map.getMimeTypeFromExtension(extension);
        if (TextUtils.isEmpty(type)) {
            return "*/*";
        } else {
            return type;
        }
    }

    public static String fileSHA1(String absPath) {
        if (TextUtils.isEmpty(absPath)) {
            return null;
        }
        final File file = new File(absPath);

        if (!file.exists() || file.isDirectory()) {
            return null;
        }
        String fileSHA1 = null;
        FileInputStream fis;
        try {
            fis = new FileInputStream(file);
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
            return null;
        }
        MessageDigest messageDigest;
        try {
            messageDigest = MessageDigest.getInstance("SHA-1");
            final byte[] buffer = new byte[IO_BUFFER_SIZE];
            int length;
            while ((length = fis.read(buffer)) > 0) {
                messageDigest.update(buffer, 0, length);
            }
            fis.close();
            fileSHA1 = SecurityUtil.bytes2Hex(messageDigest.digest());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (!TextUtils.isEmpty(fileSHA1)) {
            fileSHA1 = fileSHA1.trim();
        }
        return fileSHA1;
    }

    public static String fileMD5(String absPath) {
        if (TextUtils.isEmpty(absPath)) {
            return null;
        }
        final File file = new File(absPath);
        if (!file.exists() || file.isDirectory()) {
            return null;
        }
        String fileMD5 = null;
        FileInputStream fis;
        try {
            fis = new FileInputStream(file);
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
            return null;
        }
        MessageDigest messageDigest;
        try {
            messageDigest = MessageDigest.getInstance("MD5");
            final byte[] buffer = new byte[IO_BUFFER_SIZE];
            int length;
            while ((length = fis.read(buffer)) > 0) {
                messageDigest.update(buffer, 0, length);
            }
            fis.close();
            fileMD5 = SecurityUtil.bytes2Hex(messageDigest.digest());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (!TextUtils.isEmpty(fileMD5)) {
            fileMD5 = fileMD5.trim();
        }
        return fileMD5;
    }

    public static boolean write(String absPath, String text) {
        if (!create(absPath, true)) {
            return false;
        }

        FileOutputStream fos = null;
        PrintWriter pw = null;
        try {
            fos = new FileOutputStream(absPath);
            pw = new PrintWriter(fos);
            pw.write(text);
            pw.flush();
        } catch (Exception ignored) {
        } finally {
            CloseUtil.close(pw);
            CloseUtil.close(fos);
        }

        return true;
    }

    public static boolean write(String absPath, InputStream ips) {
        if (!create(absPath, true)) {
            return false;
        }

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(absPath);
            final byte buffer[] = new byte[IO_BUFFER_SIZE];
            int count = ips.read(buffer);
            for (; count != -1; ) {
                fos.write(buffer, 0, count);
                count = ips.read(buffer);
            }
            fos.flush();
        } catch (Exception e) {
            return false;
        } finally {
            CloseUtil.close(fos);
        }

        return true;
    }


    public static InputStream getStream(String absPath) {
        InputStream inputStream = null;

        try {
            inputStream = new FileInputStream(absPath);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return inputStream;
    }

    /**
     * Method for return file path of Gallery image
     *
     * @param context Context
     * @param uri Uri
     *
     * @return path of the selected image file from gallery
     */
    @Nullable
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static String getPath(final Context context, final Uri uri) {

        //check here to KITKAT or new version
        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {

            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[] {
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {

            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();

            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context The context.
     * @param uri The Uri to query.
     * @param selection (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     *
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    /**
     * @param uri The Uri to check.
     *
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     *
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     *
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     *
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }
}
