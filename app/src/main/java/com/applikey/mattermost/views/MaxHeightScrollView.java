package com.applikey.mattermost.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.widget.NestedScrollView;
import android.util.AttributeSet;

import com.applikey.mattermost.R;

/**
 * @author Denis Kolesnik
 * @since 18.11.16
 */

public class MaxHeightScrollView extends NestedScrollView {

    private int mMaxHeight = 0;

    public MaxHeightScrollView(Context context) {
        super(context);
    }

    public MaxHeightScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public MaxHeightScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }


    private void init(AttributeSet attrs) {
        TypedArray a = getContext().obtainStyledAttributes(attrs,
                R.styleable.MaxHeightScrollView);
        mMaxHeight = a.getDimensionPixelSize(R.styleable.MaxHeightScrollView_maxHeight,
                Integer.MAX_VALUE);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(mMaxHeight, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

    }
}
