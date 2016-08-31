package com.applikey.mattermost.injects;

import com.applikey.mattermost.App;
import com.applikey.mattermost.Constants;
import com.applikey.mattermost.utils.PrimitiveConverterFactory;
import com.applikey.mattermost.web.Api;

import java.io.File;
import java.util.concurrent.TimeUnit;

import dagger.Module;
import dagger.Provides;
import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;


@Module
public class NetworkModule {

    private App mApp;

    public NetworkModule(App app) {
        mApp = app;
    }

    @Provides
    @PerApp
    OkHttpClient provideOkHttpClient() {
        OkHttpClient.Builder okClientBuilder = new OkHttpClient.Builder();
        okClientBuilder.addInterceptor(chain -> {
            Request request = chain.request();
//                Headers headers = request.headers().newBuilder().add(“Authorization”, mApiKey)
// .build();
//                request = request.newBuilder().headers(headers).build();
            // TODO you can add your authorization headers here like in example above
            request = request.newBuilder().build();
            return chain.proceed(request);
        });
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
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
    Retrofit provideRestAdapter(OkHttpClient okHttpClient) {
        return new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(PrimitiveConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
    }

    @Provides
    @PerApp
    Api provideApi(Retrofit restAdapter) {
        return restAdapter.create(Api.class);
    }

}
