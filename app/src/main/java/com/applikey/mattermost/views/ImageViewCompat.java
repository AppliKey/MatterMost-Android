package com.applikey.mattermost.views;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.content.res.AppCompatResources;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

import com.applikey.mattermost.R;

public class ImageViewCompat extends AppCompatImageView {

    @Nullable
    private ColorStateList mColorStateList;

    public ImageViewCompat(Context context) {
        super(context);
        init(context, null, 0);
    }

    public ImageViewCompat(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public ImageViewCompat(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, @Nullable AttributeSet attributeSet, int defStyle) {
        if (attributeSet != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attributeSet, R.styleable.ImageViewCompat, defStyle,
                                                                   0);
            try {
                mColorStateList = typedArray.getColorStateList(R.styleable.ImageViewCompat_backgroundColorState);
            } finally {
                typedArray.recycle();
            }
        }

        if (mColorStateList == null) {
            mColorStateList = AppCompatResources.getColorStateList(context, R.color.selector_accent_enable_state);
        }
        initColorStateList();
    }

    private void initColorStateList() {
        Drawable drawable = DrawableCompat.wrap(getDrawable());
        DrawableCompat.setTintList(drawable, mColorStateList);
        setImageDrawable(drawable);
    }
}
