package com.applikey.mattermost.manager.notitifcation;

import android.app.Notification;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.NotificationCompat;

import com.applikey.mattermost.R;
import com.applikey.mattermost.injects.PerApp;

import javax.inject.Inject;

@PerApp
public class NotificationManager {

    private final Context mContext;
    private final NotificationManagerCompat mNotificationManager;

    @Inject
    public NotificationManager(Context context, NotificationManagerCompat notificationManager) {
        this.mContext = context;
        this.mNotificationManager = notificationManager;
    }

    public void showNotification(@NonNull String id, @NonNull String message) {
        final Notification notification =
                new NotificationCompat.Builder(mContext)
                        .setColor(ContextCompat.getColor(mContext, R.color.colorPrimary))
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(mContext.getString(R.string.new_message_received))
                        .setContentText(message)
                        .setAutoCancel(true)
                        .build();

        mNotificationManager.notify(id.hashCode(), notification);
    }

    public void dismissNotification(@NonNull String id) {
        mNotificationManager.cancel(id.hashCode());
    }
}
