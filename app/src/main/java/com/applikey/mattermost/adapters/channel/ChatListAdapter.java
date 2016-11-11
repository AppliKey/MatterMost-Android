package com.applikey.mattermost.adapters.channel;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.applikey.mattermost.R;
import com.applikey.mattermost.adapters.channel.viewholder.ChatListViewHolder;
import com.applikey.mattermost.models.channel.Channel;
import com.applikey.mattermost.models.user.User;
import com.applikey.mattermost.web.images.ImageLoader;

import io.realm.OrderedRealmCollection;
import io.realm.RealmResults;

public class ChatListAdapter extends BaseChatListAdapter<ChatListViewHolder> {

    public ChatListAdapter(@NonNull Context context,
                           RealmResults<Channel> data,
                           ImageLoader imageLoader,
                           String currentUserId) {
        super(context, data, imageLoader, currentUserId);
    }

    @Override
    public ChatListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View v = LayoutInflater.from(mContext)
                .inflate(R.layout.list_item_chat, parent, false);

        final ChatListViewHolder vh = new ChatListViewHolder(v);
        vh.getContainer().setOnClickListener(mOnClickListener);
        return vh;
    }

    @Override
    public void onBindViewHolder(ChatListViewHolder vh, int position) {
        super.onBindViewHolder(vh, position);
        final OrderedRealmCollection<Channel> data = getData();
        if (data == null) {
            return;
        }
        final Channel channel = data.get(position);

        setStatusIcon(vh, channel);
        setChannelIcon(vh, channel);
        setChannelIconVisibility(vh, channel);
    }

    private void setChannelIconVisibility(ChatListViewHolder vh, Channel element) {
        final String type = element.getType();
        if (Channel.ChannelType.PRIVATE.getRepresentation().equals(type)) {
            vh.getChannelIcon().setVisibility(View.VISIBLE);
        } else {
            vh.getChannelIcon().setVisibility(View.GONE);
        }
    }

    private void setChannelIcon(ChatListViewHolder viewHolder, Channel element) {
        final User member = element.getDirectCollocutor();
        final String previewImagePath = member != null ?
                member.getProfileImage() : null;
        final ImageView previewImage = viewHolder.getPreviewImage();
        if (previewImagePath != null && !previewImagePath.isEmpty()) {
            mImageLoader.displayCircularImage(previewImagePath, previewImage);
        } else {
            previewImage.setImageResource(R.drawable.no_resource);
        }
    }

    private void setStatusIcon(ChatListViewHolder vh, Channel data) {
        if (Channel.ChannelType.DIRECT.getRepresentation().equals(data.getType())) {
            final User member = data.getDirectCollocutor();
            final User.Status status = member != null ?
                    User.Status.from(member.getStatus()) : null;
            if (status != null) {
                vh.getStatus().setImageResource(status.getDrawableId());
            }
            vh.getStatusBackground().setVisibility(View.VISIBLE);
            vh.getStatus().setVisibility(View.VISIBLE);
        } else {
            vh.getStatusBackground().setVisibility(View.GONE);
            vh.getStatus().setVisibility(View.GONE);
        }
    }
}
