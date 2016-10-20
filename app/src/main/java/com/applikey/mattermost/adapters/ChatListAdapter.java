package com.applikey.mattermost.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.applikey.mattermost.R;
import com.applikey.mattermost.models.channel.Channel;
import com.applikey.mattermost.models.post.Post;
import com.applikey.mattermost.models.user.User;
import com.applikey.mattermost.utils.kissUtils.utils.TimeUtil;
import com.applikey.mattermost.web.images.ImageLoader;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ViewHolder> {

    private List<Channel> mDataSet = null;
    private final ImageLoader mImageLoader;
    private final String mCurrentUserId;

    private ClickListener mClickListener = null;

    public ChatListAdapter(List<Channel> dataSet, ImageLoader imageLoader, String currentUserId) {
        super();

        mImageLoader = imageLoader;
        mDataSet = new ArrayList<>(dataSet.size());
        mCurrentUserId = currentUserId;
        mDataSet.addAll(dataSet);
        Collections.sort(mDataSet, Channel.COMPARATOR_BY_DATE);
    }

    @Override
    public ChatListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_chat, parent, false);

        final ViewHolder vh = new ViewHolder(v);
        vh.getRoot().setOnClickListener(mOnClickListener);

        return vh;
    }

    @Override
    public void onBindViewHolder(ChatListAdapter.ViewHolder vh, int position) {
        final Channel data = mDataSet.get(position);
        final Context context = vh.itemView.getContext();

        final long lastPostAt = data.getLastPostAt();

        vh.getChannelName().setText(data.getDisplayName());

        final String messagePreview = getMessagePreview(data, context);

        vh.getMessagePreview().setText(messagePreview);
        vh.getLastMessageTime().setText(
                TimeUtil.formatTimeOrDateOnly(lastPostAt != 0 ? lastPostAt :
                        data.getCreatedAt()));

        setChannelIcon(vh, data);
        setChannelIconVisibility(vh, data);
        setStatusIcon(vh, data);
        setUnreadStatus(vh, data);

        vh.getRoot().setTag(position);
    }

    private String getMessagePreview(Channel channel, Context context) {
        final Post lastPost = channel.getLastPost();
        final String messagePreview;
        if (channel.getLastPost() == null) {
            messagePreview = context.getString(R.string.channel_preview_message_placeholder);
        } else if (isMy(lastPost)) {
            messagePreview = context.getString(R.string.channel_post_author_name_format, "You") +
                    channel.getLastPost().getMessage();
        } else if (!channel.getType().equals(Channel.ChannelType.DIRECT.getRepresentation())) {
            messagePreview = context.getString(R.string.channel_post_author_name_format,
                    channel.getLastPostAuthorDisplayName()) +
                    channel.getLastPost().getMessage();
        } else {
            messagePreview = channel.getLastPost().getMessage();
        }
        return messagePreview;
    }

    @Override
    public int getItemCount() {
        return mDataSet != null ? mDataSet.size() : 0;
    }

    public void setOnClickListener(ClickListener listener) {
        this.mClickListener = listener;
    }

    private void setChannelIcon(ViewHolder viewHolder, Channel element) {

        final String previewImagePath = element.getPreviewImagePath();
        if (previewImagePath != null && !previewImagePath.isEmpty()) {
            mImageLoader.displayCircularImage(previewImagePath, viewHolder.getPreviewImage());
        }
    }

    private void setChannelIconVisibility(ViewHolder viewHolder, Channel element) {
        final String type = element.getType();
        if (Channel.ChannelType.PRIVATE.getRepresentation().equals(type)) {
            viewHolder.getChannelIcon().setVisibility(View.VISIBLE);
        } else {
            viewHolder.getChannelIcon().setVisibility(View.GONE);
        }
    }

    private void setStatusIcon(ViewHolder vh, Channel data) {
        if (Channel.ChannelType.DIRECT.getRepresentation().equals(data.getType())) {
            final User.Status status = User.Status.from(data.getStatus());
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

    private void setUnreadStatus(ViewHolder vh, Channel data) {
        if (data.isUnread()) {
            vh.getNotificationIcon().setVisibility(View.VISIBLE);
        } else {
            vh.getNotificationIcon().setVisibility(View.GONE);
        }
    }

    private boolean isMy(Post post) {
        return post.getUserId().equals(mCurrentUserId);
    }

    public interface ClickListener {

        void onItemClicked(Channel channel);
    }

    private final View.OnClickListener mOnClickListener = v -> {
        final int position = (Integer) v.getTag();

        final Channel team = mDataSet.get(position);

        if (mClickListener != null) {
            mClickListener.onItemClicked(team);
        }
    };

    /* package */
    class ViewHolder extends RecyclerView.ViewHolder {

        private final View mRoot;

        @Bind(R.id.iv_preview_image)
        ImageView mPreviewImage;

        @Bind(R.id.iv_channel_icon)
        ImageView mChannelIcon;

        @Bind(R.id.iv_status_bg)
        ImageView mStatusBackground;

        @Bind(R.id.iv_status)
        ImageView mStatus;

        @Bind(R.id.iv_notification_icon)
        ImageView mNotificationIcon;

        @Bind(R.id.tv_channel_name)
        TextView mChannelName;

        @Bind(R.id.tv_last_message_time)
        TextView mLastMessageTime;

        @Bind(R.id.tv_message_preview)
        TextView mMessagePreview;

        ViewHolder(View itemView) {
            super(itemView);

            mRoot = itemView;

            ButterKnife.bind(this, itemView);
        }

        View getRoot() {
            return mRoot;
        }

        ImageView getPreviewImage() {
            return mPreviewImage;
        }

        ImageView getChannelIcon() {
            return mChannelIcon;
        }

        ImageView getStatusBackground() {
            return mStatusBackground;
        }

        ImageView getStatus() {
            return mStatus;
        }

        TextView getChannelName() {
            return mChannelName;
        }

        TextView getLastMessageTime() {
            return mLastMessageTime;
        }

        TextView getMessagePreview() {
            return mMessagePreview;
        }

        ImageView getNotificationIcon() {
            return mNotificationIcon;
        }
    }
}
