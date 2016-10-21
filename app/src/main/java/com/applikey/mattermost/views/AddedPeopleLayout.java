package com.applikey.mattermost.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.applikey.mattermost.R;
import com.applikey.mattermost.models.user.User;
import com.applikey.mattermost.web.images.ImageLoader;

import butterknife.Bind;
import butterknife.ButterKnife;


public class AddedPeopleLayout extends LinearLayout {

    private static final int MAX_VISIBLE_AVATARS = 6;
    private static final int INDEX_OF_CHANGEABLE_ITEM = 5;

    @Bind({R.id.first_added, R.id.second_added, R.id.third_added, R.id.fourth_added, R.id.fifth_added, R.id.sixth_added})
    ImageView[] mAddedUserAvatars;

    @Bind(R.id.added_people_excess_count)
    TextView mAddedPeopleExcessCount;

    private int mCount;


    public AddedPeopleLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        inflate(context, R.layout.added_people_layout, this);
        ButterKnife.bind(this);
        mCount = 0;
    }

    public void addUser(User user, ImageLoader imageLoader) {
        mCount++;
        if (mCount <= MAX_VISIBLE_AVATARS) {
            int index = mCount - 1;
            mAddedUserAvatars[index].setVisibility(VISIBLE);
            imageLoader.displayCircularImage(user.getProfileImage(), mAddedUserAvatars[index]);
        } else {
            mAddedUserAvatars[INDEX_OF_CHANGEABLE_ITEM].setVisibility(GONE);
            mAddedPeopleExcessCount.setVisibility(VISIBLE);
            mAddedPeopleExcessCount.setText(String.valueOf(mCount - MAX_VISIBLE_AVATARS));
        }
    }

    public void removeUser(User user, ImageLoader imageLoader) {
        mCount--;
        if (mCount > MAX_VISIBLE_AVATARS) {
            mAddedUserAvatars[INDEX_OF_CHANGEABLE_ITEM].setVisibility(GONE);
            mAddedPeopleExcessCount.setVisibility(VISIBLE);
            mAddedPeopleExcessCount.setText(String.valueOf(mCount - MAX_VISIBLE_AVATARS));
        } else {
            int index = mCount - 1;
            mAddedUserAvatars[index].setVisibility(VISIBLE);
            imageLoader.displayCircularImage(user.getProfileImage(), mAddedUserAvatars[index]);
        }
    }
}
