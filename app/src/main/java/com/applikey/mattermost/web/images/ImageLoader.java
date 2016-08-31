package com.applikey.mattermost.web.images;


import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.widget.ImageView;

import rx.Observable;

public interface ImageLoader {

    Observable<Bitmap> getBitmapObservable(@NonNull String url);

    void displayImage(@NonNull String url, @NonNull ImageView imageView);

    void dropMemoryCache();

}
