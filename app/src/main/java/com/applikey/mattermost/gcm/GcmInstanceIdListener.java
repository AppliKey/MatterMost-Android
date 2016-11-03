package com.applikey.mattermost.gcm;

import android.content.Intent;

import com.google.android.gms.iid.InstanceIDListenerService;

public class GcmInstanceIdListener extends InstanceIDListenerService {

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();

        final Intent intent = RegistrationIntentService.getIntent(this);
        startService(intent);
    }
}
