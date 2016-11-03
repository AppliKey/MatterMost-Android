package com.applikey.mattermost.gcm;

import android.os.Bundle;

import com.applikey.mattermost.App;
import com.applikey.mattermost.manager.notitifcation.NotificationManager;
import com.google.android.gms.gcm.GcmListenerService;

import javax.inject.Inject;

public class GcmMessageHandler extends GcmListenerService {

    private static final String MESSAGE_TYPE_CLEAR = "clear";
    private static final String ARG_CHANNEL_ID = "channel_id";
    private static final String ARG_TYPE = "type";
    private static final String ARG_MESSAGE = "message";

    @Inject
    NotificationManager mNotificationManager;

    public GcmMessageHandler() {
        App.getComponent().inject(this);
    }

    @Override
    public void onMessageReceived(String from, Bundle data) {
        super.onMessageReceived(from, data);

        final String type = data.getString(ARG_TYPE);

        if (type != null && type.equals(MESSAGE_TYPE_CLEAR)) {
            //todo cancel notification
        } else {
            String message = data.getString(ARG_MESSAGE);
            String id = data.getString(ARG_CHANNEL_ID);

            if (message != null && id != null) {
                mNotificationManager.showNewMessageNotification(id, message);
            }
        }
    }
}
