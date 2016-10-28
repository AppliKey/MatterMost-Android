package com.applikey.mattermost.gcm;

import android.os.Bundle;
import android.util.Log;
import com.applikey.mattermost.Constants;
import com.google.android.gms.gcm.GcmListenerService;

public class GcmMessageHandler extends GcmListenerService {

    @Override
    public void onMessageReceived(String from, Bundle data) {
        super.onMessageReceived(from, data);

        final String type = data.getString("type");

        logD("message received - " + type);
    }

    @Override
    public void onMessageSent(String s) {
        super.onMessageSent(s);

        logD("message sent - " + s);
    }

    @Override
    public void onDeletedMessages() {
        super.onDeletedMessages();

        logD("on delete messages");
    }

    @Override
    public void onSendError(String messageId, String error) {
        super.onSendError(messageId, error);

        logD("on send error - " + messageId + " - " + error);
    }

    private void logD(String message) {
        Log.d(Constants.LOG_TAG_DEBUG, "GcmMessageHandler: " + message);
    }
}
