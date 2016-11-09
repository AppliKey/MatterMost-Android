package com.applikey.mattermost.views;

import android.animation.LayoutTransition;
import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.annimon.stream.Stream;
import com.applikey.mattermost.R;
import com.applikey.mattermost.models.user.User;
import com.applikey.mattermost.web.images.ImageLoader;

import java.util.List;

import butterknife.ButterKnife;
import timber.log.Timber;


public class AddedPeopleLayout extends LinearLayout {

    private static final int MAX_VISIBLE_AVATARS = 6;
    private static final int VISIBLE_AVATARS_WITH_COUNTER = MAX_VISIBLE_AVATARS - 1;

    private ImageLoader mImageLoader;

    private int mItemWidth;
    private int mItemHeight;
    private int mMinMargin;
    private int mActualMargin;
    private int mMaxViewCount;
    private int mCurrentlyAddedItems;
    private boolean mAddMoreMode;
    private TextView mCounter;

    public AddedPeopleLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AddedPeopleLayout(Context context) {
        this(context, null, 0);
    }

    public AddedPeopleLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        inflate(context, R.layout.added_people_layout, this);
        setLayoutTransition(new LayoutTransition());
        ButterKnife.bind(this);
        setVisibility(VISIBLE);
        final TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.AddedPeopleLayout);
        mMinMargin = typedArray.getDimensionPixelSize(R.styleable.AddedPeopleLayout_min_item_right_margin, 20);
        typedArray.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int width = MeasureSpec.getSize(widthMeasureSpec);
        mItemWidth = MeasureSpec.getSize(heightMeasureSpec);
        mItemHeight = mItemWidth;
        final int viewCount = (width + mMinMargin) / (mItemWidth + mMinMargin);
        final int viewSpace = (viewCount * (mItemWidth + mMinMargin)) - mMinMargin;
        final int freeSpace = width - viewSpace;
        final int additionalMargin = freeSpace / viewCount;
        mActualMargin = mMinMargin + additionalMargin;
        mMaxViewCount = viewCount;
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public void addUser(User user) {
        addView(user.getProfileImage());
    }

    public void removeUser(User user) {

    }

    private void addView(String imageUrl) {
        mCurrentlyAddedItems ++;
        if (mCurrentlyAddedItems <= mMaxViewCount) {
            final ImageView imageView = new ImageView(getContext());
            final LinearLayout.LayoutParams layoutParams = new LayoutParams(mItemWidth, mItemHeight);
            layoutParams.setMarginEnd(mActualMargin);
            imageView.setLayoutParams(layoutParams);
            mImageLoader.displayCircularImage(imageUrl, imageView);
            this.addView(imageView);
        } else if (!mAddMoreMode) {
            this.removeViewAt(getChildCount() - 1);
            mAddMoreMode = true;
            final TextView addMoreCounter = new TextView(getContext());
            final LinearLayout.LayoutParams layoutParams = new LayoutParams(mItemWidth, mItemHeight);
            addMoreCounter.setTextColor(ContextCompat.getColor(getContext(), android.R.color.white));
            addMoreCounter.setTextSize(TypedValue.COMPLEX_UNIT_PX, getContext().getResources().getDimension(R.dimen.added_people_count_textsize));
            addMoreCounter.setLayoutParams(layoutParams);
            addMoreCounter.setGravity(Gravity.CENTER);
            mCounter = addMoreCounter;
            final int currentlyAddedMembersDisplayable = mCurrentlyAddedItems - mMaxViewCount + 1;
            addMoreCounter.setText(getContext().getString(R.string.added_people_count, currentlyAddedMembersDisplayable));
            addMoreCounter.setBackgroundResource(R.drawable.bg_added_people_count);
            this.addView(addMoreCounter);
        } else {
            final int currentlyAddedMembersDisplayable = mCurrentlyAddedItems - mMaxViewCount + 1;
            mCounter.setText(getContext().getString(R.string.added_people_count, currentlyAddedMembersDisplayable));
        }
    }

    public void showUsers(List<User> users) {
        Stream.of(users).forEach(user -> Timber.d(user.toString()));
        Timber.d("%d", users.size());
       // notifyChange();
    }

    public void setImageLoader(ImageLoader imageLoader) {
        mImageLoader = imageLoader;
    }
}
