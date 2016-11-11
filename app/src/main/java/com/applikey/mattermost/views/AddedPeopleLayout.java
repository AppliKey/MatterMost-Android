package com.applikey.mattermost.views;

import android.animation.LayoutTransition;
import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.annimon.stream.Stream;
import com.applikey.mattermost.R;
import com.applikey.mattermost.models.user.User;
import com.applikey.mattermost.web.images.ImageLoader;

import java.util.ArrayList;
import java.util.List;


public class AddedPeopleLayout extends LinearLayout {

    private ImageLoader mImageLoader;

    private int mItemWidth;
    private int mItemHeight;
    private int mMinMargin;
    private int mActualMargin;
    private int mMaxViewCount;
    private TextView mCounter;
    private ImageView[] mUserAvatars;
    private final List<User> mUsers = new ArrayList<>();

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
        if (isInEditMode()) {
            return;
        }
        setLayoutTransition(new LayoutTransition());
        setVisibility(VISIBLE);
        final TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.AddedPeopleLayout);
        mMinMargin = typedArray.getDimensionPixelSize(R.styleable.AddedPeopleLayout_min_item_right_margin, 0);
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
        if (mUserAvatars == null) {
            initAvatarViews(mMaxViewCount);
            setVisibility(GONE);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    private void initAvatarViews(int mMaxViewCount) {
        mUserAvatars = new ImageView[mMaxViewCount];
        final int lastAvatarIndex = mMaxViewCount - 1;
        for (int i = 0; i < mMaxViewCount; i++) {
            mUserAvatars[i] = initSingleAvatar();
            if (i < lastAvatarIndex) {
                this.addView(mUserAvatars[i]);
            }
        }
        mCounter = initCounter();
        final ImageView lastAvatar = mUserAvatars[lastAvatarIndex];

        final FrameLayout frameLayout = new FrameLayout(getContext());
        frameLayout.setLayoutTransition(new LayoutTransition());
        final LinearLayout.LayoutParams frameLayoutParams = new LayoutParams(mItemWidth, mItemHeight);
        frameLayout.setLayoutParams(frameLayoutParams);
        frameLayout.addView(lastAvatar);
        frameLayout.addView(mCounter);
        this.addView(frameLayout);
    }

    private TextView initCounter() {
        final TextView textView = new TextView(getContext());
        final LinearLayout.LayoutParams layoutParams = new LayoutParams(mItemWidth, mItemHeight);
        textView.setTextColor(ContextCompat.getColor(getContext(), android.R.color.white));
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, getContext().getResources().getDimension(R.dimen.added_people_count_textsize));
        textView.setLayoutParams(layoutParams);
        textView.setGravity(Gravity.CENTER);
        textView.setVisibility(GONE);
        textView.setBackgroundResource(R.drawable.bg_added_people_count);
        return textView;
    }

    private ImageView initSingleAvatar() {
        final ImageView userAvatar = new ImageView(getContext());
        final LinearLayout.LayoutParams layoutParams = new LayoutParams(mItemWidth, mItemHeight);
        layoutParams.setMarginEnd(mActualMargin);
        userAvatar.setLayoutParams(layoutParams);
        return userAvatar;
    }

    public void addUser(User user) {
        mUsers.add(user);
        notifyChange();
    }

    public void removeUser(User user) {
        mUsers.remove(user);
        notifyChange();
    }

    private int getDisplayableCounterValue(int totalAddedItems, int maxVisibleItems) {
        return totalAddedItems - maxVisibleItems + 1;
    }

    public void showUsers(List<User> users) {
        mUsers.clear();
        mUsers.addAll(users);
        notifyChange();
    }

    private void displayUserAvatarsInActivatedViews(List<User> users, int activatedViewsCount) {
        Stream.range(0, activatedViewsCount)
                .forEach(index -> {
                    final ImageView imageView = mUserAvatars[index];
                    imageView.setVisibility(VISIBLE);
                    mImageLoader.displayCircularImage(users.get(index).getProfileImage(), imageView);
                });
    }

    private int getCountOfActivatedViews(int dataSize) {
        int count = dataSize < mUserAvatars.length ? dataSize : mUserAvatars.length;
        if (isCounterVisible(dataSize)) {
            count--;
        }
        return count;
    }

    private void showCounterIfNeeded(boolean isNeedToShowCounter, int totalUsersCount) {
        if (isNeedToShowCounter) {
            if (mCounter.getVisibility() != VISIBLE) {
                mCounter.setVisibility(VISIBLE);
            }
            mCounter.setText(getContext().getString(R.string.added_people_count,
                    getDisplayableCounterValue(totalUsersCount, mUserAvatars.length)));
        } else {
            getLastUserAvatar().setVisibility(VISIBLE);
            mCounter.setVisibility(GONE);
        }
    }

    private void removeUnnecessaryViews(int lastVisibleViewIndex) {
        for (int i = lastVisibleViewIndex; i < mUserAvatars.length; i++) {
            mUserAvatars[i].setVisibility(GONE);
        }
    }

    private boolean isCounterVisible(int dataSize) {
        return dataSize > mUserAvatars.length;
    }

    public void setImageLoader(ImageLoader imageLoader) {
        mImageLoader = imageLoader;
    }

    public ImageView getLastUserAvatar() {
        return mUserAvatars[mUserAvatars.length - 1];
    }

    private void setVisible(boolean isActive) {
        this.setVisibility(isActive ? VISIBLE : GONE);
    }

    private boolean isNeedToBeShown(int dataSize) {
        return dataSize != 0;
    }

    public List<User> getUsers() {
        return mUsers;
    }

    private void notifyChange() {
        if (mImageLoader == null) {
            throw new RuntimeException("Please initialize ImageLoader");
        }
        setVisible(isNeedToBeShown(mUsers.size()));
        showCounterIfNeeded(isCounterVisible(mUsers.size()), mUsers.size());
        final int activatedViewsCount = getCountOfActivatedViews(mUsers.size());
        displayUserAvatarsInActivatedViews(mUsers, activatedViewsCount);
        removeUnnecessaryViews(activatedViewsCount);
    }
}
