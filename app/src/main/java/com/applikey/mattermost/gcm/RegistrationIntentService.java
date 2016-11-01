package com.applikey.mattermost.gcm;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.applikey.mattermost.App;
import com.applikey.mattermost.Constants;
import com.applikey.mattermost.R;
import com.applikey.mattermost.storage.preferences.Prefs;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import java.io.IOException;

import javax.inject.Inject;

import timber.log.Timber;

public class RegistrationIntentService extends IntentService {

    private static final String TAG = RegistrationIntentService.class.getSimpleName();

    @Inject
    Prefs mPrefs;

    public RegistrationIntentService() {
        super(TAG);

        App.getComponent().inject(this);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        final InstanceID instanceId = InstanceID.getInstance(this);

        try {
            synchronized (TAG) {
                final String token = instanceId.getToken(getString(R.string.gcm_sender_id),
                        GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
                Log.d(TAG, token);
                mPrefs.setGcmToken(token);
            }
        } catch (IOException e) {
            Timber.e("Failed to refresh token");
        }

        final Intent registrationComplete = new Intent(Constants.GCM_REGISTRATION_COMPLETE);
        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
    }
}
