package com.applikey.mattermost.gcm;

import android.content.Intent;
import com.google.android.gms.iid.InstanceIDListenerService;
import timber.log.Timber;

public class GcmInstanceIdListener extends InstanceIDListenerService {

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();

        Timber.d("Token refresh requested");

        final Intent intent = new Intent(this, RegistrationIntentService.class);
        startService(intent);
    }
}
