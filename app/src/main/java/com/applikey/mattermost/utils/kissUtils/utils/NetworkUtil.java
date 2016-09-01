/**
 * Copyright (c) 2014 CoderKiss
 * <p>
 * CoderKiss[AT]gmail.com
 */

package com.applikey.mattermost.utils.kissUtils.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;

import com.applikey.mattermost.utils.kissUtils.KissTools;

public class NetworkUtil {

    public static final String TAG = "NetworkHelper";
    private NetworkType type;
    private NetworkListener listener;

    private NetworkUtil() {
        type = NetworkType.NONE;
        updateNetwork();

        final IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        KissTools.getApplicationContext().registerReceiver(receiver, filter);
    }

    public final synchronized NetworkType getNetworkType() {
        return type;
    }

    public final void setListener(NetworkListener l) {
        listener = l;
    }

    private void updateNetwork() {
        final NetworkInfo networkInfo = getNetworkInfo();
        final NetworkType t = type;
        type = checkType(networkInfo);
        if (type != t && listener != null) {
            listener.onNetworkChanged(t, type);
        }
        LogUtil.i(TAG, "network [type] " + type);
    }

    private synchronized NetworkInfo getNetworkInfo() {
        final Context context = KissTools.getApplicationContext();
        final ConnectivityManager manager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        return manager.getActiveNetworkInfo();
    }

    private static NetworkType checkType(NetworkInfo info) {
        if (info == null || !info.isConnected()) {
            return NetworkType.NONE;
        }

        final int type = info.getType();
        final int subType = info.getSubtype();
        if ((type == ConnectivityManager.TYPE_WIFI)
                || (type == ConnectivityManager.TYPE_ETHERNET)) {
            return NetworkType.WIFI_FAST;
        }

        if (type == ConnectivityManager.TYPE_MOBILE) {
            switch (subType) {
                case TelephonyManager.NETWORK_TYPE_GPRS:
                case TelephonyManager.NETWORK_TYPE_EDGE:
                case TelephonyManager.NETWORK_TYPE_CDMA:
                case TelephonyManager.NETWORK_TYPE_1xRTT:
                case TelephonyManager.NETWORK_TYPE_IDEN:
                    return NetworkType.MOBILE_SLOW; // 2G

                case TelephonyManager.NETWORK_TYPE_UMTS:
                case TelephonyManager.NETWORK_TYPE_EVDO_0:
                case TelephonyManager.NETWORK_TYPE_EVDO_A:
                case TelephonyManager.NETWORK_TYPE_HSDPA:
                case TelephonyManager.NETWORK_TYPE_HSUPA:
                case TelephonyManager.NETWORK_TYPE_HSPA:
                case TelephonyManager.NETWORK_TYPE_EVDO_B:
                case TelephonyManager.NETWORK_TYPE_EHRPD:
                case TelephonyManager.NETWORK_TYPE_HSPAP:
                    return NetworkType.MOBILE_MIDDLE;// 3G

                case TelephonyManager.NETWORK_TYPE_LTE:
                    return NetworkType.MOBILE_FAST; // 4G
            }
        }

        return NetworkType.NONE;
    }

    @SuppressWarnings("FieldCanBeLocal")
    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateNetwork();
        }
    };

    public enum NetworkType {
        WIFI_FAST,
        MOBILE_FAST,
        MOBILE_MIDDLE,
        MOBILE_SLOW,
        NONE,
    }

    public interface NetworkListener {
        void onNetworkChanged(NetworkType ot, NetworkType nt);
    }
}
