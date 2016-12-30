package com.applikey.mattermost.adapters.channel;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.applikey.mattermost.R;
import com.applikey.mattermost.adapters.channel.viewholder.GroupChatListViewHolder;
import com.applikey.mattermost.models.channel.Channel;
import com.applikey.mattermost.web.images.ImageLoader;

import io.realm.RealmResults;

public class GroupChatListAdapter extends BaseChatListAdapter<GroupChatListViewHolder> {

    public GroupChatListAdapter(@NonNull Context context, RealmResults<Channel> data,
                                ImageLoader imageLoader, String currentUserId) {
        super(context, data, imageLoader, currentUserId);
    }

    @Override
    public GroupChatListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        final View view = layoutInflater.inflate(R.layout.list_item_group_chat, parent, false);
        final GroupChatListViewHolder vh = new GroupChatListViewHolder(view, mCurrentUserId);

        vh.getContainer().setOnClickListener(mOnClickListener);
        return vh;
    }
}
