package com.applikey.mattermost.gcm;

import android.content.Intent;
import android.util.Log;
import com.applikey.mattermost.Constants;
import com.google.android.gms.iid.InstanceIDListenerService;

public class GcmInstanceIdListener extends InstanceIDListenerService {

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();

        Log.d(Constants.LOG_TAG_DEBUG, "Token refresh requested");

        final Intent intent = new Intent(this, RegistrationIntentService.class);
        startService(intent);
    }
}
