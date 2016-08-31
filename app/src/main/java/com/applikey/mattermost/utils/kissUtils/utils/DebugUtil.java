/**
 * @author dawson dong
 */

package com.applikey.mattermost.utils.kissUtils.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;

import com.applikey.mattermost.utils.kissUtils.KissTools;

public class DebugUtil {

    private DebugUtil() {
    }

    public static boolean isDebuggable() {
        try {
            final Context context = KissTools.getApplicationContext();
            final ApplicationInfo info = context.getApplicationInfo();
            return (info.flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

}
