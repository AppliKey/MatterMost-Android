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

import io.realm.RealmResults;

public class UserChatListAdapter extends BaseChatListAdapter<UserChatListViewHolder> {

    public UserChatListAdapter(@NonNull Context context,
                               RealmResults<Channel> data,
                               ImageLoader imageLoader,
                               String currentUserId) {
        super(context, data, imageLoader, currentUserId);
    }

    @Override
    public UserChatListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final Context context = parent.getContext();
        final View view = LayoutInflater.from(context)
                .inflate(R.layout.list_item_chat, parent, false);

        final UserChatListViewHolder vh = new UserChatListViewHolder(view, mCurrentUserId);
        vh.getContainer().setOnClickListener(mOnClickListener);
        return vh;
    }
}
