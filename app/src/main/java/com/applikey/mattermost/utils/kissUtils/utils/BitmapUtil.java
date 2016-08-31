/**
 * Copyright (c) 2014 CoderKiss
 * <p>
 * CoderKiss[AT]gmail.com
 */

package com.applikey.mattermost.utils.kissUtils.utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.media.ExifInterface;
import android.media.ThumbnailUtils;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.provider.MediaStore.Images;
import android.text.TextUtils;
import android.view.Gravity;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class BitmapUtil {

    public static final String TAG = BitmapUtil.class.getSimpleName();

    private BitmapUtil() {
    }

    public static ImageSize getImageSize(String absPath) {
        if (TextUtils.isEmpty(absPath)) {
            return null;
        }
        final Options options = getOptions(absPath);
        final int width = options.outWidth;
        final int height = options.outHeight;
        final ImageSize size = new ImageSize(width, height);
        return size;
    }

    @TargetApi(VERSION_CODES.HONEYCOMB_MR1)
    public static int getImageBytes(Bitmap bitmap) {
        if (bitmap == null) {
            return 0;
        }
        int bitmapSize;
        if (VERSION.SDK_INT >= VERSION_CODES.HONEYCOMB_MR1) {
            bitmapSize = bitmap.getByteCount();
        } else {
            bitmapSize = bitmap.getRowBytes() * bitmap.getHeight();
        }
        if (bitmapSize < 0) {
            bitmapSize = 0;
        }

        return bitmapSize;
    }

    public static Options getOptions(String absPath) {
        if (TextUtils.isEmpty(absPath)) {
            return null;
        }
        final Options options = new Options();
        options.inPreferredConfig = Config.ALPHA_8;
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(absPath, options);
        return options;
    }

    public static Options getOptions(String absPath, int desireWidth,
                                     int desireHeight) {
        if (!FileUtil.exists(absPath) || desireWidth <= 0 || desireHeight <= 0) {
            return null;
        }
        final Options options = getOptions(absPath);
        final int scaleW = sampleSize(options.outWidth, desireWidth);
        final int scaleH = sampleSize(options.outHeight, desireWidth);
        options.inSampleSize = scaleH > scaleW ? scaleH : scaleW;
        options.inJustDecodeBounds = false;
        return options;
    }

    public static Bitmap getAssetBitmap(Context context, String filePath) {
        final AssetManager assetManager = context.getAssets();
        InputStream ips;
        Bitmap bitmap = null;
        try {
            ips = assetManager.open(filePath);
            bitmap = BitmapFactory.decodeStream(ips);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    public static Bitmap getImage(String absPath) {
        if (!FileUtil.exists(absPath)) {
            LogUtil.w(TAG, "invalid file path: " + absPath);
            return null;
        }

        LogUtil.d(TAG, "decode bitmap " + absPath);
        final long enter = System.currentTimeMillis();
        final Bitmap bitmap = BitmapFactory.decodeFile(absPath);
        final long delta = System.currentTimeMillis() - enter;
        LogUtil.v(TAG, "decode bitmap time " + delta);

        return bitmap;
    }

    public static Bitmap getImage(String absPath, ImageSize size) {
        if (TextUtils.isEmpty(absPath)) {
            return null;
        }

        if (ImageSize.valid(size)) {
            return getImage(absPath, size.getWidth(), size.getHeight());
        } else {
            return getImage(absPath);
        }
    }

    public static Bitmap getImage(String absPath, int width, int height) {
        if (width <= 0 && height <= 0 || !FileUtil.exists(absPath)) {
            LogUtil.e(TAG, "invalid parameters absPath " + absPath + " width "
                    + width + " height " + height);
            return null;
        }

        LogUtil.d(TAG, "decode bitmap " + absPath + " width " + width
                + " height " + height);
        final long enter = System.currentTimeMillis();
        final Options options = getOptions(absPath, width, height);
        Bitmap bitmap = BitmapFactory.decodeFile(absPath, options);
        if (bitmap == null) {
            LogUtil.i(TAG, "decode bitmap failed " + absPath);
            return null;
        }

        final int rotate = getRotate(absPath);
        bitmap = rotateImage(bitmap, rotate);
        System.gc();
        final long delta = System.currentTimeMillis() - enter;
        LogUtil.v(TAG, "decode bitmap time " + delta);
        return bitmap;
    }

    public static Bitmap rotateImage(Bitmap bitmap, int rotate) {
        if (bitmap == null) {
            return bitmap;
        }
        final Matrix matrix = new Matrix();
        matrix.setRotate(rotate);
        final Bitmap rotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                bitmap.getHeight(), matrix, true);
        if (rotated != null && !rotated.equals(bitmap)) {
            bitmap.recycle();
            bitmap = rotated;
        }
        return bitmap;
    }

    public static boolean saveImage(Bitmap bitmap, String absPath) {
        return saveImage(bitmap, absPath, 100);
    }

    public static boolean saveImage(Bitmap bitmap, String absPath, int quality) {
        if (!FileUtil.create(absPath)) {
            LogUtil.w(TAG, "create file failed.");
            return false;
        }

        try {
            final File outFile = new File(absPath);
            final FileOutputStream fos = new FileOutputStream(outFile);
            final BufferedOutputStream bos = new BufferedOutputStream(fos);
            bitmap.compress(Bitmap.CompressFormat.PNG, quality, bos);
            bos.flush();
            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
            LogUtil.e(TAG, "failed to write image content");
            return false;
        }

        return true;
    }

    public static Bitmap squareImage(Bitmap bitmap) {
        if (bitmap == null) {
            return null;
        }
        final int width = bitmap.getWidth();
        final int height = bitmap.getHeight();
        if (width == height) {
            LogUtil.w(TAG, "no need to square image");
            return bitmap;
        }
        final int x = (height < width) ? ((width - height) / 2) : 0;
        final int y = (width < height) ? ((height - width) / 2) : 0;
        final int pixels = width < height ? width : height;
        //noinspection UnnecessaryLocalVariable
        final Bitmap square = Bitmap.createBitmap(bitmap, x, y, pixels, pixels);
        return square;
    }

    public static int getRotate(String absPath) {
        if (!FileUtil.exists(absPath)) {
            LogUtil.w(TAG, "invalid file path");
            return 0;
        }

        ExifInterface exifInterface;
        try {
            exifInterface = new ExifInterface(absPath);
        } catch (IOException e) {
            e.printStackTrace();
            return 0;
        }

        final int orientation = exifInterface.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_UNDEFINED);
        int rotate = 0;
        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                rotate = 90;
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                rotate = 180;
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                rotate = 270;
                break;
            default:
                break;
        }
        LogUtil.v(TAG, "image rotate " + rotate);
        return rotate;
    }

    public static Bitmap resizeImage(Bitmap bitmap, int desireWidth,
                                     int desireHeight, boolean isEnlarge) {
        if (bitmap == null || desireHeight <= 0 || desireWidth <= 0) {
            return null;
        }

        final int width = bitmap.getWidth();
        final int height = bitmap.getHeight();

        if (width <= 0 || height <= 0) {
            return bitmap;
        }
        if (!isEnlarge && (width < desireWidth && height < desireHeight)) {
            return bitmap;
        }
        float scale;
        if (width < height) {
            scale = (float) desireHeight / (float) height;
            if (desireWidth < width * scale) {
                scale = (float) desireWidth / (float) width;
            }
        } else {
            scale = (float) desireWidth / (float) width;
        }

        final Matrix matrix = new Matrix();
        matrix.postScale(scale, scale);
        bitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);

        return bitmap;
    }

    public static Bitmap getThumbnail(String absPath, int width,
                                      int height) {
        if (!FileUtil.exists(absPath) || width <= 0 || height <= 0) {
            return null;
        }
        final Options options = getOptions(absPath, width, height);
        try {
            final Bitmap bitmap = BitmapFactory.decodeFile(absPath, options);
            //noinspection UnnecessaryLocalVariable
            final Bitmap result = ThumbnailUtils.extractThumbnail(bitmap, width,
                    height, ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
            // recycle(bitmap);
            return result;
        } catch (Exception | OutOfMemoryError e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Bitmap getVideoThumbnail(String absPath, int width,
                                           int height) {
        if (!FileUtil.exists(absPath) || width <= 0 || height <= 0) {
            return null;
        }
        Bitmap bitmap;
        bitmap = ThumbnailUtils.createVideoThumbnail(absPath,
                Images.Thumbnails.MINI_KIND);
        LogUtil.d(TAG, "bitmap width " + bitmap.getWidth() + " height "
                + bitmap.getHeight());
        bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height,
                ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
        return bitmap;
    }

    public static Bitmap waterMark(Context context, Bitmap bitmap,
                                   int waterResId, int gravity) {
        final Drawable waterPrintDrawable = context.getResources().getDrawable(
                waterResId);
        final int waterPrintWidth = waterPrintDrawable.getIntrinsicWidth();
        final int waterPrintHeight = waterPrintDrawable.getIntrinsicHeight();
        final int w = bitmap.getWidth();
        final int h = bitmap.getHeight();
        if (gravity == Gravity.CENTER) {
            waterPrintDrawable.setBounds((w - waterPrintWidth) / 2,
                    (h - waterPrintHeight) / 2, (w + waterPrintWidth) / 2,
                    (h + waterPrintHeight) / 2);
        } else {
            waterPrintDrawable.setBounds((w - waterPrintWidth) / 2, h,
                    (w + waterPrintWidth) / 2, h + waterPrintHeight);
        }
        final Canvas mCanvas = new Canvas(bitmap);
        waterPrintDrawable.draw(mCanvas);
        return bitmap;
    }

    public static Bitmap toRounded(Context context, Bitmap bitmap,
                                   int color, int borderDips, int desireWidth) {
        try {
            final Bitmap bitmap0 = squareImage(bitmap);
            final Bitmap bitmap1 = resizeImage(bitmap0, desireWidth, desireWidth,
                    true);
            final int toPX = DeviceUtil.dp2px(borderDips);
            int maxBorder = (desireWidth / 2) / 5;
            maxBorder = maxBorder > 15 ? 15 : maxBorder;
            final int borderSizePx = toPX > maxBorder ? maxBorder : toPX;

            //noinspection UnnecessaryLocalVariable
            final int size = desireWidth;
            final int center = (int) (size / 2);
            final int left = (int) ((desireWidth - size) / 2);
            final int top = (int) ((desireWidth - size) / 2);
            final int right = left + size;
            final int bottom = top + size;

            final Bitmap output = Bitmap.createBitmap(size, size, Config.ARGB_8888);
            final Canvas canvas = new Canvas(output);
            final Paint paint = new Paint();

            final Rect src = new Rect(left, top, right, bottom);
            final Rect dst = new Rect(0, 0, size, size);

            canvas.drawARGB(0, 0, 0, 0);
            // draw border
            paint.setColor(Color.WHITE);
            paint.setAntiAlias(true);
            int radius = center - borderSizePx;
            canvas.drawCircle(center, center, radius, paint);
            // draw bitmap
            paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
            canvas.drawBitmap(bitmap1, src, dst, paint);
            paint.setXfermode(null);

            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth((float) borderSizePx);
            radius = center - borderSizePx / 2;
            canvas.drawCircle(center, center, radius, paint);
            canvas.save();
            recycle(bitmap0, bitmap1, bitmap);
            return output;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    public static Bitmap toRoundCorner(Context context, Bitmap bitmap) {
        try {
            final int w = bitmap.getWidth();
            final int h = bitmap.getHeight();
            final int delta = DeviceUtil.dp2px(5);
            final float roundPx = DeviceUtil.dp2px(14);
            final Paint paint = new Paint();
            paint.setAntiAlias(true);

			/* draw round foreground */
            final int foreW = w - 2 * delta;
            final int foreH = h - 2 * delta;
            final Bitmap foreBmp = Bitmap
                    .createBitmap(foreW, foreH, Config.ARGB_8888);
            final Rect rect = new Rect(0, 0, foreW, foreH);
            final RectF rectF = new RectF(rect);
            final Canvas canvas0 = new Canvas(foreBmp);
            canvas0.drawRoundRect(rectF, roundPx, roundPx, paint);
            paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
            canvas0.drawBitmap(bitmap, null, rect, paint);

			/* draw round background */
            final Drawable drawable = getDrawbale(0xffffffff, (int) (delta + roundPx));
            drawable.setBounds(0, 0, w, h);
            final Bitmap result = Bitmap.createBitmap(w, h, Config.ARGB_8888);
            final Canvas canvas = new Canvas(result);
            drawable.draw(canvas);
            canvas.drawBitmap(foreBmp, delta, delta, null);
            recycle(foreBmp, bitmap);

            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return bitmap;
        }
    }

    public static GradientDrawable getDrawbale(int argb, int radius) {
        final GradientDrawable drawable = new GradientDrawable();
        drawable.setCornerRadius(radius);
        drawable.setColor(argb);
        return drawable;
    }

    public static Bitmap drawRing(Bitmap bitmap, int deltaRadius,
                                  int color) {
        final int w = bitmap.getWidth();
        final int dia = deltaRadius * 2 + w;
        final float radius = (float) dia / 2;
        final Bitmap resultBitmap = Bitmap.createBitmap(dia, dia, Config.ARGB_8888);
        final Paint paint = new Paint();
        paint.setColor(color);
        paint.setAlpha(128);
        paint.setAntiAlias(true);
        final Canvas canvas = new Canvas(resultBitmap);
        canvas.drawCircle(radius, radius, radius, paint);
        canvas.drawBitmap(bitmap, deltaRadius, deltaRadius, null);
        return resultBitmap;
    }

    public static Bitmap getRound(int argb, int dia) {
        final float radius = dia / 2;
        final Bitmap resultBitmap = Bitmap.createBitmap(dia, dia, Config.ARGB_8888);
        final Paint paint = new Paint();
        paint.setColor(argb);
        paint.setAntiAlias(true);
        final Canvas canvas = new Canvas(resultBitmap);
        canvas.drawCircle(radius, radius, radius, paint);
        return resultBitmap;
    }

    public static Bitmap stretch(Bitmap bitmap, int height) {
        if (bitmap == null) {
            return null;
        }
        if (height <= bitmap.getHeight()) {
            return bitmap;
        }
        final int w = bitmap.getWidth();
        final int h = bitmap.getHeight();
        final int tmpHeight = 1;
        final Bitmap resultBitmap = Bitmap.createBitmap(w, height, Config.ARGB_8888);
        final Canvas mCanvas = new Canvas(resultBitmap);
        mCanvas.drawBitmap(bitmap, 0, 0, null);
        final Bitmap tmp = Bitmap
                .createBitmap(bitmap, 0, h - tmpHeight, w, tmpHeight);
        bitmap.recycle();
        for (int index = 0; index < (height - h) / tmpHeight; index++) {
            mCanvas.drawBitmap(tmp, 0, h + index * tmpHeight, null);
        }
        tmp.recycle();
        return resultBitmap;
    }

    public static Bitmap overlay(final Bitmap bitmap) {
        if (bitmap == null) {
            return null;
        }
        final int width = bitmap.getWidth();
        final int height = bitmap.getHeight();
        if (width == 0 || height == 0) {
            return null;
        }
        final float rota = 345;
        final double radians = Math.toRadians(rota);
        /* after rotate , the new width is the original width */
        final int rotateWidth = (int) (width / (Math.abs(Math.sin(radians)) + Math
                .abs(Math.cos(radians))));
        final int delta = (width - rotateWidth) / 2;

        // result
        final Bitmap result = Bitmap.createBitmap(width, width, Config.ARGB_8888);
        final Canvas canvas = new Canvas(result);
        // foreground
        final Bitmap foreBmp = resizeImage(bitmap, rotateWidth, rotateWidth, true);
        // rotate
        final Matrix matrix = new Matrix();
        matrix.postRotate(rota);
        final Bitmap rotate = Bitmap.createBitmap(foreBmp, 0, 0, rotateWidth,
                rotateWidth, matrix, true);
        canvas.drawBitmap(rotate, 0, 0, null);
        canvas.drawBitmap(foreBmp, delta, delta, null);
        recycle(bitmap, rotate, foreBmp);
        return result;
    }

    public static Bitmap drawLayoutDropShadow(Bitmap bitmap) {
        final BlurMaskFilter blurFilter = new BlurMaskFilter(2,
                BlurMaskFilter.Blur.OUTER);
        final Paint shadowPaint = new Paint();
        shadowPaint.setMaskFilter(blurFilter);
        final int[] offsetXY = {5, 5};
        final Bitmap shadowBitmap = bitmap.extractAlpha(shadowPaint, offsetXY);
        final Bitmap shadowImage32 = shadowBitmap.copy(Config.ARGB_8888, true);
        final Canvas c = new Canvas(shadowImage32);
        c.drawBitmap(bitmap, 0, 0, null);
        return shadowImage32;
    }

    public static Bitmap blur(Bitmap origin, int radius) {
        final int iterations = 1;
        final int width = origin.getWidth();
        final int height = origin.getHeight();
        final int[] inPixels = new int[width * height];
        final int[] outPixels = new int[width * height];
        final Bitmap bitmap = Bitmap.createBitmap(width, height,
                Config.ARGB_8888);
        origin.getPixels(inPixels, 0, width, 0, 0, width, height);
        for (int index = 0; index < iterations; index++) {
            blur(inPixels, outPixels, width, height, radius);
            blur(outPixels, inPixels, height, width, radius);
        }
        bitmap.setPixels(inPixels, 0, width, 0, 0, width, height);
        return bitmap;
    }

    private static void blur(int[] in, int[] out, int width, int height,
                             int radius) {
        final int widthMinus1 = width - 1;
        final int tableSize = 2 * radius + 1;
        final int divide[] = new int[256 * tableSize];

        for (int index = 0; index < 256 * tableSize; index++) {
            divide[index] = index / tableSize;
        }

        int inIndex = 0;

        for (int y = 0; y < height; y++) {
            int outIndex = y;
            int ta = 0, tr = 0, tg = 0, tb = 0;

            for (int i = -radius; i <= radius; i++) {
                final int rgb = in[inIndex + clamp(i, 0, width - 1)];
                ta += (rgb >> 24) & 0xff;
                tr += (rgb >> 16) & 0xff;
                tg += (rgb >> 8) & 0xff;
                tb += rgb & 0xff;
            }

            for (int x = 0; x < width; x++) {
                out[outIndex] = (divide[ta] << 24) | (divide[tr] << 16)
                        | (divide[tg] << 8) | divide[tb];

                int i1 = x + radius + 1;
                if (i1 > widthMinus1)
                    i1 = widthMinus1;
                int i2 = x - radius;
                if (i2 < 0)
                    i2 = 0;
                final int rgb1 = in[inIndex + i1];
                final int rgb2 = in[inIndex + i2];

                ta += ((rgb1 >> 24) & 0xff) - ((rgb2 >> 24) & 0xff);
                tr += ((rgb1 & 0xff0000) - (rgb2 & 0xff0000)) >> 16;
                tg += ((rgb1 & 0xff00) - (rgb2 & 0xff00)) >> 8;
                tb += (rgb1 & 0xff) - (rgb2 & 0xff);
                outIndex += height;
            }
            inIndex += width;
        }
    }

    private static int clamp(int x, int a, int b) {
        return (x < a) ? a : (x > b) ? b : x;
    }

    private static int sampleSize(int size, int target) {
        int sample = 1;
        for (int index = 0; index < 10; index++) {
            if (size < target * 2) {
                break;
            }
            size = size / 2;
            sample = sample * 2;
        }
        return sample;
    }

    private static void recycle(Bitmap... bitmaps) {
        if (bitmaps == null || bitmaps.length == 0) {
            return;
        }
        for (Bitmap bitmap : bitmaps) {
            if (bitmap == null) {
                continue;
            }
            if (!bitmap.isRecycled()) {
                bitmap.recycle();
                continue;
            }
            bitmap = null;
        }
        System.gc();
    }
}
