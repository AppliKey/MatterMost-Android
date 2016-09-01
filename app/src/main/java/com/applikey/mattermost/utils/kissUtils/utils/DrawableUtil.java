/**
 * Copyright (c) 2014 CoderKiss
 * <p>
 * CoderKiss[AT]gmail.com
 */

package com.applikey.mattermost.utils.kissUtils.utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;

public class DrawableUtil {

    public static final String TAG = "DrawableUtil";

    private DrawableUtil() {
    }

    public static Drawable fromColor(int color) {
        //noinspection UnnecessaryLocalVariable
        final ColorDrawable cd = new ColorDrawable(color);
        return cd;
    }

    public static Drawable fromBitmap(Bitmap bitmap) {
        if (bitmap == null) {
            return null;
        }
        @SuppressWarnings("deprecation")
        final BitmapDrawable bd = new BitmapDrawable(bitmap);
        return bd;
    }

    public static Bitmap toBitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }

        int width = drawable.getIntrinsicWidth();
        width = width > 0 ? width : 1;
        int height = drawable.getIntrinsicHeight();
        height = height > 0 ? height : 1;

        final Bitmap bitmap = Bitmap.createBitmap(width, height,
                Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }
}
