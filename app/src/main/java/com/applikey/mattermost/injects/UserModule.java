package com.applikey.mattermost.injects;

import android.content.Context;
import android.net.Uri;

import com.applikey.mattermost.Constants;
import com.applikey.mattermost.manager.metadata.MetaDataManager;
import com.applikey.mattermost.platform.socket.MessagingSocket;
import com.applikey.mattermost.platform.socket.Socket;
import com.applikey.mattermost.storage.db.ChannelStorage;
import com.applikey.mattermost.storage.db.Db;
import com.applikey.mattermost.storage.db.PostStorage;
import com.applikey.mattermost.storage.db.PreferenceStorage;
import com.applikey.mattermost.storage.db.UserStorage;
import com.applikey.mattermost.storage.preferences.PersistentPrefs;
import com.applikey.mattermost.storage.preferences.Prefs;
import com.applikey.mattermost.utils.image.ImagePathHelper;
import com.applikey.mattermost.utils.kissUtils.utils.UrlUtil;
import com.applikey.mattermost.web.BearerTokenFactory;
import com.google.gson.Gson;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;

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
    PersistentPrefs providePersistencePrefs(Context context, Gson gson) {
        return new PersistentPrefs(context, gson);
    }

    @Provides
    @PerUser
    ChannelStorage provideChannelStorage(Db db, Prefs prefs, MetaDataManager metaDataManager) {
        return new ChannelStorage(db, prefs, metaDataManager);
    }

    @Provides
    @PerUser
    UserStorage provideUserStorage(Db db, Prefs prefs, ImagePathHelper imagePathHelper) {
        return new UserStorage(db, prefs, imagePathHelper);
    }

    @Provides
    @PerUser
    PostStorage providePostStorage(Db db) {
        return new PostStorage(db);
    }

    @Provides
    @PerUser
    PreferenceStorage providePreferenceStorage(Db db, Prefs prefs) {
        return new PreferenceStorage(db, prefs);
    }

    @Provides
    @PerUser
    MetaDataManager provideMetadataManager(Prefs prefs, PersistentPrefs persistentPrefs) {
        return new MetaDataManager(prefs, persistentPrefs);
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
