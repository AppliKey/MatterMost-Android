package com.applikey.mattermost.web.images;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.widget.ImageView;

import com.applikey.mattermost.utils.image.PicassoCircularTransformation;
import com.applikey.mattermost.utils.rx.RetryWithDelay;
import com.jakewharton.picasso.OkHttp3Downloader;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.PicassoTools;

import java.io.File;

import okhttp3.OkHttpClient;
import rx.Observable;
import rx.schedulers.Schedulers;

public class PicassoImageLoader implements ImageLoader {

    private final Picasso picasso;
    private final PicassoCircularTransformation transformation;

    public PicassoImageLoader(Context context, OkHttpClient client) {
        picasso = new Picasso.Builder(context).downloader(new OkHttp3Downloader(client)).build();
        transformation = new PicassoCircularTransformation();
    }

    @Override
    public Observable<Bitmap> getBitmapObservable(@NonNull String url) {
        return Observable.fromCallable(() -> picasso.load(url).get())
                .subscribeOn(Schedulers.io())
                .retryWhen(new RetryWithDelay(5, 300));
    }

    @Override
    public void invalidateCache(String url) {
        picasso.invalidate(url);
    }

    @Override
    public void displayImage(@NonNull String url, @NonNull ImageView imageView) {
        picasso.load(url).into(imageView);
    }

    @Override
    public void displayCircularImage(@NonNull File file, @NonNull ImageView imageView) {
        picasso.load(file).transform(transformation).into(imageView);
    }

    @Override
    public void displayCircularImage(@NonNull String url, @NonNull ImageView imageView) {
        picasso.load(url).transform(transformation).into(imageView);
//        picasso.load(url).networkPolicy(NetworkPolicy.NO_CACHE).transform(transformation).into(imageView);
    }

    @Override
    public void displayThumbnailImage(@NonNull String url, @NonNull ImageView imageView) {
        picasso.load(url).resize(100, 100).centerInside().into(imageView);
    }

    @Override
    public void dropMemoryCache() {
        PicassoTools.clearCache(picasso);
    }
}
