package com.applikey.skeletonproject.web.images;


import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.widget.ImageView;

import com.applikey.skeletonproject.utils.rx.RetryWithDelay;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.PicassoTools;

import rx.Observable;
import rx.schedulers.Schedulers;

public class PicassoImageLoader implements ImageLoader {

    private Context context;

    public PicassoImageLoader(Context context) {
        this.context = context;
    }

    @Override
    public Observable<Bitmap> getBitmapObservable(@NonNull String url) {
        return Observable.fromCallable(() -> Picasso.with(context).load(url).get())
                .subscribeOn(Schedulers.io())
                .retryWhen(new RetryWithDelay(5, 300));
    }

    @Override
    public void displayImage(@NonNull String url, @NonNull ImageView imageView) {
        Picasso.with(context).load(url).into(imageView);
    }

    @Override
    public void dropMemoryCache() {
        PicassoTools.clearCache(Picasso.with(context));
    }


}
