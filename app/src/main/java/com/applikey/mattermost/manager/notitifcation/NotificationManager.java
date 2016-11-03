package com.applikey.mattermost.manager.notitifcation;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.NotificationCompat;

import com.applikey.mattermost.R;
import com.applikey.mattermost.activities.SplashActivity;
import com.applikey.mattermost.injects.PerApp;

import javax.inject.Inject;

@PerApp
public class NotificationManager {

    public static final String NOTIFICATION_BUNDLE_KEY = "notification-bundle";
    public static final String NOTIFICATION_CHANNEL_ID_KEY = "channel-id";

    private final Context mContext;
    private final NotificationManagerCompat mNotificationManager;

    @Inject
    public NotificationManager(Context context, NotificationManagerCompat notificationManager) {
        this.mContext = context;
        this.mNotificationManager = notificationManager;
    }

    public void showNewMessageNotification(@NonNull String id, @NonNull String message) {
        Bundle bundle = new Bundle();
        bundle.putString(NOTIFICATION_CHANNEL_ID_KEY, id);

        Intent intent = SplashActivity.getIntent(mContext, bundle);

        PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        final Notification notification =
                new NotificationCompat.Builder(mContext)
                        .setColor(ContextCompat.getColor(mContext, R.color.colorPrimary))
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(mContext.getString(R.string.new_message_received))
                        .setContentText(message)
                        .setContentIntent(pendingIntent)
                        .setAutoCancel(true)
                        .build();

        mNotificationManager.notify(id.hashCode(), notification);
    }

    public void dismissNotification(@NonNull String id) {
        mNotificationManager.cancel(id.hashCode());
    }
}
