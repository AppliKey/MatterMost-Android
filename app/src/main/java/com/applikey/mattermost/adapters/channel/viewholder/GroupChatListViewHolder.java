package com.applikey.mattermost.adapters.channel.viewholder;

import android.view.View;
import android.widget.ImageView;

import com.applikey.mattermost.R;
import com.applikey.mattermost.models.channel.Channel;
import com.applikey.mattermost.models.user.User;
import com.applikey.mattermost.views.CircleCounterView;
import com.applikey.mattermost.web.images.ImageLoader;

import java.util.Iterator;
import java.util.List;

import butterknife.Bind;

public class GroupChatListViewHolder extends BaseChatListViewHolder {

    private final static int GROUP_GRID_COUNT = 4;

    @Bind(R.id.iv_first)
    ImageView mIvFirst;

    @Bind(R.id.iv_second)
    ImageView mIvSecond;

    @Bind(R.id.iv_third)
    ImageView mIvThird;

    @Bind(R.id.iv_fourth)
    ImageView mIvFourth;

    @Bind(R.id.ccv_fourth)
    CircleCounterView mCcvFourth;

    public GroupChatListViewHolder(View itemView, String userId) {
        super(itemView, userId);
    }

    @Override
    public void bind(ImageLoader imageLoader, Channel channel) {
        super.bind(imageLoader, channel);

        final List<User> users = channel.getUsers();
        final Iterator<User> iterator = users.iterator();

        setGroupImage(imageLoader, iterator, mIvFirst);
        setGroupImage(imageLoader, iterator, mIvSecond);
        setGroupImage(imageLoader, iterator, mIvThird);

        if (users.size() == GROUP_GRID_COUNT) {
            mCcvFourth.setVisibility(View.GONE);

            setGroupImage(imageLoader, iterator, mIvFourth);
        } else if (users.size() > GROUP_GRID_COUNT) {
            mIvFourth.setVisibility(View.GONE);
            mCcvFourth.setVisibility(View.VISIBLE);

            mCcvFourth.setCount(users.size() - GROUP_GRID_COUNT);
        } else {
            mIvFourth.setVisibility(View.GONE);
            mCcvFourth.setVisibility(View.GONE);
        }
    }

    private void setGroupImage(ImageLoader imageLoader, Iterator<User> iterator, ImageView imageView) {
        if (iterator.hasNext()) {
            imageView.setVisibility(View.VISIBLE);
            imageLoader.displayCircularImage(iterator.next().getProfileImage(), imageView);
        } else {
            imageView.setVisibility(View.GONE);
        }
    }
}