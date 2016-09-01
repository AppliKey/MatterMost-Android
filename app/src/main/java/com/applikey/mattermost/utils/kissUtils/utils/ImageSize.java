/**
 * Copyright (c) 2014 CoderKiss
 * <p>
 * CoderKiss[AT]gmail.com
 */

package com.applikey.mattermost.utils.kissUtils.utils;

public class ImageSize {

    public static final String TAG = ImageSize.class.getSimpleName();

    private int mWidth;
    private int mHeight;

    public ImageSize(ImageSize size) {
        this.mWidth = size.mWidth;
        this.mHeight = size.mHeight;
    }

    public ImageSize(int width, int height) {
        mWidth = width;
        mHeight = height;
    }

    public int getWidth() {
        return this.mWidth;
    }

    public void setWidth(int width) {
        this.mWidth = width;
    }

    public int getHeight() {
        return this.mHeight;
    }

    public void setHeight(int height) {
        this.mHeight = height;
    }

    @Override
    public String toString() {
        return mWidth + "x" + mHeight;
    }

    public static boolean valid(ImageSize size) {
        if (size != null && size.getWidth() > 0 && size.getHeight() > 0) {
            return true;
        } else {
            return false;
        }
    }
}
