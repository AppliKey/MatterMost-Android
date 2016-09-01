/**
 * Copyright (c) 2014 CoderKiss
 * <p>
 * CoderKiss[AT]gmail.com
 */

package com.applikey.mattermost.utils.kissUtils.utils;

import android.text.TextUtils;

import java.security.MessageDigest;
import java.util.Locale;

public class SecurityUtil {

    public static final String TAG = "SecurityUtil";

    private SecurityUtil() {
    }

    public static String getMD5(String text) {
        if (TextUtils.isEmpty(text)) {
            return null;
        }
        MessageDigest md5Digest;
        try {
            md5Digest = MessageDigest.getInstance("MD5");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        final char[] charArray = text.toCharArray();
        final byte[] byteArray = new byte[charArray.length];

        for (int index = 0; index < charArray.length; index++) {
            byteArray[index] = (byte) charArray[index];
        }

        final byte[] md5Bytes = md5Digest.digest(byteArray);
        final StringBuilder hexValue = new StringBuilder();

        for (byte md5Byte : md5Bytes) {
            int val = ((int) md5Byte) & 0xff;
            if (val < 16) {
                hexValue.append("0");
            }
            hexValue.append(Integer.toHexString(val));
        }

        //noinspection UnnecessaryLocalVariable
        final String md5 = hexValue.toString();
        return md5;
    }

    public static String bytes2Hex(byte[] bytes) {
        String hs = "";
        String stmp = "";
        for (byte aByte : bytes) {
            stmp = (Integer.toHexString(aByte & 0XFF));
            if (stmp.length() == 1) {
                hs += "0" + stmp;
            } else {
                hs += stmp;
            }
        }
        return hs.toLowerCase(Locale.ENGLISH);
    }

    public static String getSHA1(String text) {
        if (TextUtils.isEmpty(text)) {
            return null;
        }
        MessageDigest sha1Digest;
        try {
            sha1Digest = MessageDigest.getInstance("SHA-1");
        } catch (Exception e) {
            return null;
        }
        final byte[] textBytes = text.getBytes();
        sha1Digest.update(textBytes, 0, text.length());
        final byte[] sha1hash = sha1Digest.digest();
        return bytes2Hex(sha1hash);
    }
}
