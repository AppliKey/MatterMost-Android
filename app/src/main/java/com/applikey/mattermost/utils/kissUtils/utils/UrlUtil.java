/**
 * Copyright (c) 2014 CoderKiss
 * <p>
 * CoderKiss[AT]gmail.com
 */

package com.applikey.mattermost.utils.kissUtils.utils;

import android.net.Uri;
import android.text.TextUtils;

import java.net.URLEncoder;

public class UrlUtil {

    public static final String TAG = UrlUtil.class.getSimpleName();
    public static final String PROTOCOL_DELIMITER = "://";
    public static final String WEB_SERVICE_PROTOCOL_PREFIX = "ws" + PROTOCOL_DELIMITER;

    private UrlUtil() {
    }

    public static String encode(String url) {
        if (TextUtils.isEmpty(url)) {
            return url;
        }

        String encodedURL = "";
        final String[] temp = url.split("/");
        final int length = temp.length;
        for (int index = 0; index < length; index++) {
            try {
                temp[index] = URLEncoder.encode(temp[index], "UTF-8");
                temp[index] = temp[index].replace("+", "%20");
            } catch (Exception e) {
                e.printStackTrace();
                return url;
            }
            encodedURL += temp[index];
            if (index < (length - 1)) {
                encodedURL += "/";
            }
        }
        return encodedURL;
    }

    public static Uri parse(String url) {
        if (TextUtils.isEmpty(url)) {
            return null;
        }

        Uri uri = null;
        try {
            uri = Uri.parse(url);
        } catch (Exception ignored) {
        }
        return uri;
    }

    public static String getParam(String url, String key) {
        final Uri uri = parse(url);
        if (uri == null) {
            return null;
        }

        String value = null;
        try {
            value = uri.getQueryParameter(key);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return value;
    }

    public static String removeProtocol(String baseUrl) {
        final int index = baseUrl.indexOf(PROTOCOL_DELIMITER);

        if (index == -1) {
            return baseUrl;
        }

        return baseUrl.substring(index + PROTOCOL_DELIMITER.length());
    }
}
