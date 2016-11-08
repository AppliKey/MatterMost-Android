package com.applikey.mattermost.views;

import android.animation.LayoutTransition;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.annimon.stream.Stream;
import com.applikey.mattermost.R;
import com.applikey.mattermost.models.user.User;
import com.applikey.mattermost.web.images.ImageLoader;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import timber.log.Timber;


public class AddedPeopleLayout extends LinearLayout {

    private static final int MAX_VISIBLE_AVATARS = 6;
    private static final int VISIBLE_AVATARS_WITH_COUNTER = MAX_VISIBLE_AVATARS - 1;

    @Bind({R.id.first_added, R.id.second_added, R.id.third_added, R.id.fourth_added, R.id.fifth_added, R.id.sixth_added})
    ImageView[] mAddedUserAvatars;

    @Bind(R.id.added_people_excess_count)
    TextView mAddedPeopleExcessCount;

    private ImageLoader mImageLoader;
    private List<User> mUsers = new ArrayList<>();

    public AddedPeopleLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AddedPeopleLayout(Context context) {
        this(context, null, 0);
    }

    public AddedPeopleLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        inflate(context, R.layout.added_people_layout, this);
        setLayoutTransition(new LayoutTransition());
        ButterKnife.bind(this);
        setVisibility(GONE);
    }

    public void addUser(User user) {
        mUsers.add(user);
        notifyChange();
    }

    public void removeUser(User user) {
        mUsers.remove(user);
        notifyChange();
    }

    public void showUsers(List<User> users) {
        mUsers.clear();
        mUsers.addAll(users);
        Stream.of(users).forEach(user -> Timber.d(user.toString()));
        Timber.d("%d", users.size());
        notifyChange();
    }

    private void displayUserAvatarsInActivatedViews(List<User> users, int activatedViewsCount) {
        Stream.range(0, activatedViewsCount)
                .forEach(index -> {
                    final ImageView imageView = mAddedUserAvatars[index];
                    imageView.setVisibility(VISIBLE);
                    mImageLoader.displayCircularImage(users.get(index).getProfileImage(), imageView);
                });
    }

    private int getCountOfActivatedViews(int dataSize) {
        int count = dataSize < MAX_VISIBLE_AVATARS ? dataSize : MAX_VISIBLE_AVATARS;
        if (isCounterVisible(dataSize)) {
            count--;
        }
        return count;
    }

    private void showCounterIfNeeded(boolean isNeedToShowCounter, int totalUsersCount) {
        if (isNeedToShowCounter) {
            getLastUserAvatar().setVisibility(GONE);
            mAddedPeopleExcessCount.setVisibility(VISIBLE);
            mAddedPeopleExcessCount.setText(getContext().getString(R.string.added_people_count, totalUsersCount - VISIBLE_AVATARS_WITH_COUNTER));
        } else {
            getLastUserAvatar().setVisibility(VISIBLE);
            mAddedPeopleExcessCount.setVisibility(GONE);
        }
    }

    private void removeUnnecessaryViews(int lastVisibleViewIndex) {
        for (int i = lastVisibleViewIndex; i < MAX_VISIBLE_AVATARS; i++) {
            mAddedUserAvatars[i].setVisibility(INVISIBLE);
        }
    }

    private boolean isCounterVisible(int dataSize) {
        return dataSize > MAX_VISIBLE_AVATARS;
    }

    public void setImageLoader(ImageLoader imageLoader) {
        mImageLoader = imageLoader;
    }

    public ImageView getLastUserAvatar() {
        return mAddedUserAvatars[mAddedUserAvatars.length - 1];
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
