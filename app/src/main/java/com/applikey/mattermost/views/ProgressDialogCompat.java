package com.applikey.mattermost.views;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorRes;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.widget.ProgressBar;

public class ProgressDialogCompat extends ProgressDialog {

    public ProgressDialogCompat(Context context) {
        super(context);
    }

    public ProgressDialogCompat(Context context, int theme) {
        super(context, theme);
    }

    public void setTint(@ColorRes int colorRes) {
        final Drawable drawable = DrawableCompat.wrap(new ProgressBar(getContext()).getIndeterminateDrawable());
        setIndeterminateDrawable(drawable);
        DrawableCompat.setTint(drawable, ContextCompat.getColor(getContext(), colorRes));
    }
}
