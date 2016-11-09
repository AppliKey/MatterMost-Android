package com.applikey.mattermost.injects;

import android.net.Uri;

import com.applikey.mattermost.Constants;
import com.applikey.mattermost.platform.socket.MessagingSocket;
import com.applikey.mattermost.platform.socket.Socket;
import com.applikey.mattermost.storage.preferences.Prefs;
import com.applikey.mattermost.utils.kissUtils.utils.UrlUtil;
import com.applikey.mattermost.web.BearerTokenFactory;
import com.applikey.mattermost.web.GsonFactory;
import com.google.gson.Gson;

import dagger.Module;
import dagger.Provides;

import javax.inject.Named;

@Module
@PerUser
public class UserModule {

    @Provides
    @PerUser
    @Named(Constants.CURRENT_USER_QUALIFIER)
    String provideCurrentUserId(Prefs mPrefs) {
        return mPrefs.getCurrentUserId();
    }

    @Provides
    @PerUser
    Socket provideMessagingSocket(BearerTokenFactory bearerTokenFactory, Prefs prefs, Gson gson) {
        String baseUrl = prefs.getCurrentServerUrl();
        baseUrl = UrlUtil.removeProtocol(baseUrl);
        baseUrl = UrlUtil.WEB_SERVICE_PROTOCOL_PREFIX + baseUrl;
        baseUrl = baseUrl + Constants.WEB_SOCKET_ENDPOINT;
        return new MessagingSocket(bearerTokenFactory, gson, Uri.parse(baseUrl));
    }

}
