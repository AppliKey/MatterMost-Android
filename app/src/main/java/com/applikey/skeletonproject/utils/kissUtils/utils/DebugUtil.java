/**
 * @author dawson dong
 */

package com.applikey.skeletonproject.utils.kissUtils.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;

import com.applikey.skeletonproject.utils.kissUtils.KissTools;


public class DebugUtil {

    public static boolean isDebugable() {
        try {
            Context context = KissTools.getApplicationContext();
            ApplicationInfo info = context.getApplicationInfo();
            return (info.flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

}
