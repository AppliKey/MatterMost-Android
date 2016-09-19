package com.applikey.mattermost.injects;

import com.applikey.mattermost.App;
import com.applikey.mattermost.Constants;
import com.applikey.mattermost.storage.db.Db;
import com.applikey.mattermost.storage.db.TeamStorage;
import com.applikey.mattermost.storage.preferences.Prefs;
import com.applikey.mattermost.web.Api;
import com.applikey.mattermost.web.ApiDelegate;
import com.applikey.mattermost.web.ServerUrlFactory;
import com.applikey.mattermost.web.images.ImageLoader;
import com.applikey.mattermost.web.images.PicassoImageLoader;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.concurrent.TimeUnit;

import dagger.Module;
import dagger.Provides;
import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;

@Module
public class GlobalModule {

    private App mApp;

    public GlobalModule(App app) {
        mApp = app;
    }

    @Provides
    @PerApp
    EventBus provideEventBus() {
        return EventBus.getDefault();
    }

    @Provides
    @PerApp
    ImageLoader provideImageLoader() {
        return new PicassoImageLoader(mApp);
    }

    @Provides
    @PerApp
    Prefs providePrefs() {
        return new Prefs(mApp);
    }

    @Provides
    @PerApp
    ServerUrlFactory provideServerUrlFactory(Prefs prefs) {
        return new ServerUrlFactory(prefs);
    }

    @Provides
    @PerApp
    OkHttpClient provideOkHttpClient() {
        final OkHttpClient.Builder okClientBuilder = new OkHttpClient.Builder();
        okClientBuilder.addInterceptor(chain -> {
            Request request = chain.request();
            // TODO Check if we have to do it manually, or it's done automatically
//            final String authToken = prefs.getAuthToken();
//            if (authToken != null) {
//                final Headers headers = request.headers().newBuilder().add("token", authToken).build();
//                request = request.newBuilder().headers(headers).build();
//            }
            return chain.proceed(request);
        });
        final HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BASIC);
        okClientBuilder.addInterceptor(httpLoggingInterceptor);
        final File baseDir = mApp.getCacheDir();
        if (baseDir != null) {
            final File cacheDir = new File(baseDir, "HttpResponseCache");
            okClientBuilder.cache(new Cache(cacheDir, 1024 * 1024 * 50));
        }
        okClientBuilder.connectTimeout(Constants.TIMEOUT_DURATION_SEC, TimeUnit.SECONDS);
        okClientBuilder.readTimeout(Constants.TIMEOUT_DURATION_SEC, TimeUnit.SECONDS);
        okClientBuilder.writeTimeout(Constants.TIMEOUT_DURATION_SEC, TimeUnit.SECONDS);
        return okClientBuilder.build();
    }

    @Provides
    @PerApp
    Api provideApi(OkHttpClient okHttpClient, ServerUrlFactory serverUrlFactory) {
        return new ApiDelegate(okHttpClient, serverUrlFactory);
    }

    @Provides
    @PerApp
    Db provideDb() {
        return new Db(mApp);
    }

    @Provides
    @PerApp
    TeamStorage provideTeamStorage() {
        return new TeamStorage();
    }

}
