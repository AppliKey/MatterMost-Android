package com.applikey.mattermost.views;

import android.content.Context;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.View;

import com.devspark.robototextview.widget.RobotoButton;

/**
 * @author serega2593
 * @see <a href="https://gist.github.com/serega2593/c967f6187025205185d7">appropriate gist</a>
 */
public class SafeButton extends RobotoButton implements View.OnClickListener {

    private static final long MIN_INTERVAL_MS = 500;
    private OnClickListener mUserListener;
    private long mLastClickTime;

    public SafeButton(Context context) {
        super(context);
        init();
    }

    public SafeButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SafeButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @Override
    public void setOnClickListener(View.OnClickListener l) {
        mUserListener = l;
    }

    @Override
    public void onClick(View view) {
        final long currentTimestamp = SystemClock.uptimeMillis();
        if ((mLastClickTime == 0 || (currentTimestamp - mLastClickTime > MIN_INTERVAL_MS))
                && mUserListener != null) {
            mLastClickTime = currentTimestamp;
            mUserListener.onClick(view);
        }
    }

    private void init() {
        super.setOnClickListener(this);
    }
}
