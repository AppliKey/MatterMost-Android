package com.applikey.mattermost.adapters.channel;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.applikey.mattermost.R;
import com.applikey.mattermost.adapters.channel.viewholder.GroupChatListViewHolder;
import com.applikey.mattermost.models.channel.Channel;
import com.applikey.mattermost.models.user.User;
import com.applikey.mattermost.web.images.ImageLoader;

import java.util.Iterator;
import java.util.List;

import io.realm.OrderedRealmCollection;
import io.realm.RealmResults;

public class GroupChatListAdapter extends BaseChatListAdapter<GroupChatListViewHolder> {

    private final static int GROUP_GRID_COUNT = 4;

    public GroupChatListAdapter(@NonNull Context context, RealmResults<Channel> data,
                                ImageLoader imageLoader, String currentUserId) {
        super(context, data, imageLoader, currentUserId);
    }

    @Override
    public GroupChatListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View v = LayoutInflater.from(mContext)
                .inflate(R.layout.list_item_group_chat, parent, false);

        final GroupChatListViewHolder vh = new GroupChatListViewHolder(v);
        vh.getContainer().setOnClickListener(mOnClickListener);

        return vh;
    }

    @Override
    public void onBindViewHolder(GroupChatListViewHolder vh, int position) { //TODO
        super.onBindViewHolder(vh, position);
        final OrderedRealmCollection<Channel> data = getData();
        if (data == null) {
            return;
        }
        final Channel channel = data.get(position);

        final List<User> users = channel.getUsers();
        final Iterator<User> iterator = users.iterator();

        setGroupImage(iterator, vh.getFirstImageView());
        setGroupImage(iterator, vh.getSecondImageView());
        setGroupImage(iterator, vh.getThirdImageView());

        if (users.size() == GROUP_GRID_COUNT) {
            vh.getCounterView().setVisibility(View.GONE);

            setGroupImage(iterator, vh.getFourthImageView());
        } else if (users.size() > GROUP_GRID_COUNT) {
            vh.getFourthImageView().setVisibility(View.GONE);
            vh.getCounterView().setVisibility(View.VISIBLE);

            vh.getCounterView().setCount(users.size());
        } else {
            vh.getFourthImageView().setVisibility(View.GONE);
            vh.getCounterView().setVisibility(View.GONE);
        }
    }

    private void setGroupImage(Iterator<User> iterator, ImageView imageView) {
        if (iterator.hasNext()) {
            imageView.setVisibility(View.VISIBLE);
            mImageLoader.displayCircularImage(iterator.next().getProfileImage(), imageView);
        } else {
            imageView.setVisibility(View.GONE);
        }
    }
}
