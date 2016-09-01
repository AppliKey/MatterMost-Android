/**
 * Copyright (c) 2014 CoderKiss
 * <p>
 * CoderKiss[AT]gmail.com
 */

package com.applikey.mattermost.utils.kissUtils.utils;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;

import com.applikey.mattermost.utils.kissUtils.KissTools;

import java.util.LinkedList;


public class MessageUtil {

    private static final String TAG = MessageUtil.class.getSimpleName();

    private static final String BROADCAST_PARAM = "broadcast_param";
    private static final String REQUEST_ACTION = "request_action";
    private static final String RESPONSE_ACTION = "response_action";

    private static volatile MessageUtil instance;
    private LocalBroadcastManager mManager;
    private LinkedList<BroadcastReceiver> mReceivers;

    private MessageUtil() {
        mReceivers = new LinkedList<>();
        mManager = LocalBroadcastManager.getInstance(KissTools
                .getApplicationContext());
    }

    public static MessageUtil sharedInstance() {
        synchronized (MessageUtil.class) {
            if (instance == null) {
                instance = new MessageUtil();
            }
        }
        return instance;
    }

    public void registerReceiver(BroadcastReceiver receiver, IntentFilter filter) {
        if (receiver == null || filter == null) {
            LogUtil.e(TAG, "invalid parameters");
            return;
        }

        //noinspection SynchronizeOnNonFinalField
        synchronized (mManager) {
            if (mReceivers.contains(receiver)) {
                LogUtil.e(TAG, "unregister old receiver!");
                mManager.unregisterReceiver(receiver);
            }

            mReceivers.addLast(receiver);
            mManager.registerReceiver(receiver, filter);
        }
    }

    public void unregisterReceiver(BroadcastReceiver receiver) {
        if (receiver == null) {
            LogUtil.e(TAG, "invalid parameters!");
            return;
        }

        if (!mReceivers.contains(receiver)) {
            return;
        }

        //noinspection SynchronizeOnNonFinalField
        synchronized (mManager) {
            mReceivers.remove(receiver);
            mManager.unregisterReceiver(receiver);
        }
    }

    public void sendBroadcast(Intent intent) {
        if (intent == null) {
            return;
        }
        final String action = intent.getAction();
        LogUtil.d(TAG, "sendBroadcast " + action);
        mManager.sendBroadcast(intent);
    }

    public void sendBroadcast(String action, String info) {
        if (TextUtils.isEmpty(action)) {
            LogUtil.e(TAG, "invalid action");
            return;
        }

        final Intent intent = new Intent(action);
        if (!TextUtils.isEmpty(info)) {
            intent.putExtra(BROADCAST_PARAM, info);
        }
        mManager.sendBroadcast(intent);
    }
}
