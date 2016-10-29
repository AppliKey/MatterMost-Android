package com.applikey.mattermost.gcm;

import android.os.Bundle;
import android.util.Log;

import com.applikey.mattermost.Constants;
import com.applikey.mattermost.storage.db.UserStorage;
import com.google.android.gms.gcm.GcmListenerService;

import rx.subscriptions.CompositeSubscription;

public class GcmMessageHandler extends GcmListenerService {

    private static final String MESSAGE_TYPE_CLEAR = "clear";

    private CompositeSubscription mSubscription;

    @Override
    public void onCreate() {
        super.onCreate();

        mSubscription = new CompositeSubscription();
    }

    @Override
    public void onDestroy() {
        mSubscription.clear();

        super.onDestroy();
    }

    @Override
    public void onMessageReceived(String from, Bundle data) {
        super.onMessageReceived(from, data);

        final String type = GcmMessageHelper.extractType(data);
        logD("message received - " + type);

        if (type != null && type.equals(MESSAGE_TYPE_CLEAR)) {
            // cancel notification
        } else {
            final String body = data.getString("message");
            logD("message body: " + body);
            // notify other components
            // show notification

            final GcmMessageHelper.RawPostDto rawPost = GcmMessageHelper.extractRawPost(data);
            processPostDto();
        }
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

    private void processPostDto() {

    }

    private void logD(String message) {
        Log.d(Constants.LOG_TAG_DEBUG, "GcmMessageHandler: " + message);
    }
}
