package com.applikey.mattermost.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.applikey.mattermost.R;
import com.applikey.mattermost.models.user.User;
import com.applikey.mattermost.web.images.ImageLoader;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;


public class AddedPeopleLayout extends LinearLayout {

    private static final int MAX_VISIBLE_AVATARS = 6;
    private static final int VISIBLE_AVATARS_WITH_COUNTER = MAX_VISIBLE_AVATARS - 1;

    @Bind({R.id.first_added, R.id.second_added, R.id.third_added, R.id.fourth_added, R.id.fifth_added, R.id.sixth_added})
    ImageView[] mAddedUserAvatars;

    @Bind(R.id.added_people_excess_count)
    TextView mAddedPeopleExcessCount;

    private ImageLoader mImageLoader;


    public AddedPeopleLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        inflate(context, R.layout.added_people_layout, this);
        ButterKnife.bind(this);
        mAddedPeopleExcessCount.setTextColor(getContext().getResources().getColor(android.R.color.white));
    }

    public void showUsers(List<User> users) {
        if (isVisibleCounter(users.size())) {
            getLastUserAvatar().setVisibility(GONE);
            mAddedPeopleExcessCount.setVisibility(VISIBLE);
            mAddedPeopleExcessCount.setText(getContext().getString(R.string.added_people_count, users.size() - VISIBLE_AVATARS_WITH_COUNTER));
        } else {
            getLastUserAvatar().setVisibility(VISIBLE);
            mAddedPeopleExcessCount.setVisibility(GONE);
        }
        final int dataSize = users.size();
        int index = dataSize < MAX_VISIBLE_AVATARS ? dataSize : MAX_VISIBLE_AVATARS;
        if (isVisibleCounter(dataSize)) {
            index --;
        }
        int visibleAvatarCounter = 0;
        for (int i = 0; i < index; i++) {
            final ImageView ivUserAvatar = mAddedUserAvatars[i];
            ivUserAvatar.setVisibility(VISIBLE);
            mImageLoader.displayCircularImage(users.get(i).getProfileImage(), ivUserAvatar);
            visibleAvatarCounter++;
        }
        for (int i = visibleAvatarCounter; i < MAX_VISIBLE_AVATARS; i++) {
            mAddedUserAvatars[i].setVisibility(INVISIBLE);
        }
    }

    private boolean isVisibleCounter(int dataSize) {
        return dataSize > MAX_VISIBLE_AVATARS;
    }

    public void setImageLoader(ImageLoader imageLoader) {
        mImageLoader = imageLoader;
    }

    public ImageView getLastUserAvatar() {
        return mAddedUserAvatars[mAddedUserAvatars.length - 1];
    }
}
