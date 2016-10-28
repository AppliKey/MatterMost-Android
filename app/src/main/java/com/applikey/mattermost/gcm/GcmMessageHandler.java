package com.applikey.mattermost.gcm;

import android.os.Bundle;
import com.google.android.gms.gcm.GcmListenerService;
import timber.log.Timber;

public class GcmMessageHandler extends GcmListenerService {

    @Override
    public void onMessageReceived(String from, Bundle data) {
        final String type = data.getString("type");

        Timber.d(type);

        super.onMessageReceived(from, data);
    }
}
