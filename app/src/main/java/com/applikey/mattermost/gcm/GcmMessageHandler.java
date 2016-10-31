package com.applikey.mattermost.gcm;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.NotificationCompat;

import com.applikey.mattermost.App;
import com.applikey.mattermost.R;
import com.applikey.mattermost.storage.db.PostStorage;
import com.google.android.gms.gcm.GcmListenerService;

import javax.inject.Inject;

import rx.subscriptions.CompositeSubscription;

public class GcmMessageHandler extends GcmListenerService {

    private static final String MESSAGE_TYPE_CLEAR = "clear";

    private CompositeSubscription mSubscription;

    @Inject
    PostStorage mPostStorage;

    @Override
    public void onCreate() {
        super.onCreate();
        App.getComponent().inject(this);
        mSubscription = new CompositeSubscription();
    }

    @Override
    public void onDestroy() {
        mSubscription.clear();

        super.onDestroy();
    }

    private static final String ARG_CHANNEL_ID = "channel_id";
    private static final String ARG_TYPE = "type";
    private static final String ARG_MESSAGE = "message";

    @Override
    public void onMessageReceived(String from, Bundle data) { //TODO implement notification logic
        super.onMessageReceived(from, data);

        final String type = data.getString(ARG_TYPE);

        if (type != null && type.equals(MESSAGE_TYPE_CLEAR)) {
            // cancel notification
        } else {
            showNotification(data.getString(ARG_MESSAGE), data.getString(ARG_CHANNEL_ID));
        }
    }

    private void showNotification(String message, String channelId) {

        Notification newMessageNotification =
                new NotificationCompat.Builder(this)
                        .setColor(ContextCompat.getColor(this, R.color.colorPrimary))
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(getString(R.string.new_message_received))
                        .setContentText(message)
                        .build();

        NotificationManager notificationManager =
                (NotificationManager)
                        getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(channelId.hashCode(), newMessageNotification);
    }
}
