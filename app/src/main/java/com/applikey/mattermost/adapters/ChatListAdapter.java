package com.applikey.mattermost.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.applikey.mattermost.R;
import com.applikey.mattermost.adapters.viewholders.ChatListViewHolder;
import com.applikey.mattermost.models.channel.Channel;
import com.applikey.mattermost.models.post.Post;
import com.applikey.mattermost.models.user.User;
import com.applikey.mattermost.utils.kissUtils.utils.TimeUtil;
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
        vh.getRoot().setOnClickListener(mOnClickListener);

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

        final long lastPostAt = channel.getLastPostAt();

        vh.getChannelName().setText(channel.getDisplayName());

        final String messagePreview = getMessagePreview(channel, mContext);

        vh.getMessagePreview().setText(messagePreview);
        vh.getLastMessageTime().setText(
                TimeUtil.formatTimeOrDateOnlyChannel(lastPostAt != 0 ? lastPostAt :
                        channel.getCreatedAt()));

        setChannelIcon(vh, channel);
        setChannelIconVisibility(vh, channel);
        setStatusIcon(vh, channel);
        setUnreadStatus(vh, channel);

        vh.getRoot().setTag(position);
    }

    @Override
    public long getItemId(int index) {
        final Channel item = getItem(index);
        return item != null ? item.hashCode() : 0;
    }

    public String getMessagePreview(Channel channel, Context context) {
        final Post lastPost = channel.getLastPost();
        final String messagePreview;
        if (channel.getLastPost() == null) {
            messagePreview = context.getString(R.string.channel_preview_message_placeholder);
        } else if (isMy(lastPost)) {
            messagePreview = context.getString(R.string.channel_post_author_name_format, "You") +
                    channel.getLastPost().getMessage();
        } else if (!channel.getType().equals(Channel.ChannelType.DIRECT.getRepresentation())) {
            if (lastPost != null) {
                final String postAuthor = User.getDisplayableName(lastPost.getAuthor());
                messagePreview = context.getString(R.string.channel_post_author_name_format, postAuthor)
                        + channel.getLastPost().getMessage();
            } else {
                messagePreview = context.getString(R.string.loading);
            }
        } else {
            messagePreview = channel.getLastPost().getMessage();
        }
        return messagePreview;
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

    private void setChannelIconVisibility(ChatListViewHolder viewHolder, Channel element) {
        final String type = element.getType();
        if (Channel.ChannelType.PRIVATE.getRepresentation().equals(type)) {
            viewHolder.getChannelIcon().setVisibility(View.VISIBLE);
        } else {
            viewHolder.getChannelIcon().setVisibility(View.GONE);
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

    private void setUnreadStatus(ChatListViewHolder vh, Channel data) {
        if (data.hasUnreadMessages()) {
            vh.getNotificationIcon().setVisibility(View.VISIBLE);
            vh.getContainer().setBackgroundResource(R.color.unread_background);
        } else {
            vh.getNotificationIcon().setVisibility(View.GONE);
            vh.getContainer().setBackgroundResource(android.R.color.white);
        }
    }

    private boolean isMy(Post post) {
        return post.getUserId().equals(mCurrentUserId);
    }

    /* package */

}
