package com.applikey.mattermost.adapters.channel;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.applikey.mattermost.R;
import com.applikey.mattermost.adapters.channel.viewholder.UserChatListViewHolder;
import com.applikey.mattermost.models.channel.Channel;
import com.applikey.mattermost.web.images.ImageLoader;

import io.realm.OrderedRealmCollection;
import io.realm.RealmResults;

public class ChatListAdapter extends BaseChatListAdapter<UserChatListViewHolder> {

    public ChatListAdapter(@NonNull Context context,
                           RealmResults<Channel> data,
                           ImageLoader imageLoader,
                           String currentUserId) {
        super(context, data, imageLoader, currentUserId);
    }

    @Override
    public UserChatListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View v = LayoutInflater.from(mContext)
                .inflate(R.layout.list_item_chat, parent, false);

        final UserChatListViewHolder vh = new UserChatListViewHolder(v, mCurrentUserId);
        vh.getContainer().setOnClickListener(mOnClickListener);
        return vh;
    }

    @Override
    public void onBindViewHolder(UserChatListViewHolder vh, int position) {
        super.onBindViewHolder(vh, position);
        final OrderedRealmCollection<Channel> data = getData();
        if (data == null) {
            return;
        }
        final Channel channel = data.get(position);

        vh.bind(mImageLoader, channel);
    }
}
