package com.applikey.mattermost.views;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.applikey.mattermost.R;

public class CircleCounterView extends FrameLayout {

    private TextView mCounterTextView;

    public CircleCounterView(Context context) {
        super(context);
        init();
    }

    public CircleCounterView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CircleCounterView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public CircleCounterView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    public void setCount(int count) {
        mCounterTextView.setText(getContext().getString(R.string.added_people_count, count));
    }

    private void init() {
        initCounter();
        addView(mCounterTextView);
    }

    private void initCounter() {
        mCounterTextView = new AutoResizeTextView(getContext());
        mCounterTextView.setTextColor(ContextCompat.getColor(getContext(), android.R.color.white));
        mCounterTextView.setGravity(Gravity.CENTER);
        mCounterTextView.setTypeface(Typeface.create("sans-serif", Typeface.BOLD));
        mCounterTextView.setBackgroundResource(R.drawable.bg_added_people_count);
        mCounterTextView.setPadding(0, getPaddingTop(), 0, getPaddingBottom());
    }
}
