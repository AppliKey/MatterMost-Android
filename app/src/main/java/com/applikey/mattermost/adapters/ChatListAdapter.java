package com.applikey.mattermost.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.applikey.mattermost.R;
import com.applikey.mattermost.models.channel.Channel;
import com.applikey.mattermost.models.channel.ChannelWithMetadata;
import com.applikey.mattermost.models.user.UserStatus;
import com.applikey.mattermost.utils.kissUtils.utils.TimeUtil;
import com.applikey.mattermost.web.images.ImageLoader;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ViewHolder> {

    private List<ChannelWithMetadata> mDataSet = null;
    private ImageLoader mImageLoader;

    public ChatListAdapter(Collection<ChannelWithMetadata> dataSet, ImageLoader imageLoader) {
        super();

        mImageLoader = imageLoader;
        mDataSet = new ArrayList<>(dataSet.size());
        mDataSet.addAll(dataSet);
        Collections.sort(mDataSet, ChannelWithMetadata.COMPARATOR_BY_DATE);
    }

    @Override
    public ChatListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_chat, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ChatListAdapter.ViewHolder vh, int position) {
        final ChannelWithMetadata data = mDataSet.get(position);

        final long lastPostAt = data.getChannel().getLastPostAt();

        vh.getChannelName().setText(data.getChannel().getDisplayName());
        vh.getLastMessageTime().setText(
                TimeUtil.formatTimeOrDate(lastPostAt != 0 ? lastPostAt :
                        data.getChannel().getCreatedAt()));

        setChannelIcon(vh, data);
        setChannelIconVisibility(vh, data);
        setStatusIcon(vh, data);
        setUnreadStatus(vh, data);
    }

    @Override
    public int getItemCount() {
        return mDataSet != null ? mDataSet.size() : 0;
    }

    private void setChannelIcon(ViewHolder viewHolder, ChannelWithMetadata element) {

        final String previewImagePath = element.getChannel().getPreviewImagePath();
        if (previewImagePath != null && !previewImagePath.isEmpty()) {
            mImageLoader.displayCircularImage(previewImagePath, viewHolder.getPreviewImage());
        }
    }

    private void setChannelIconVisibility(ViewHolder viewHolder, ChannelWithMetadata element) {
        final String type = element.getChannel().getType();
        if (Channel.ChannelType.PRIVATE.getRepresentation().equals(type)) {
            viewHolder.getChannelIcon().setVisibility(View.VISIBLE);
        } else {
            viewHolder.getChannelIcon().setVisibility(View.GONE);
        }
    }

    private void setStatusIcon(ViewHolder vh, ChannelWithMetadata data) {
        if (Channel.ChannelType.DIRECT.getRepresentation().equals(data.getChannel().getType())) {
            final UserStatus.Status status = UserStatus.Status.from(data.getChannel().getStatus());
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

    private void setUnreadStatus(ViewHolder vh, ChannelWithMetadata data) {
        if (data.checkIsUnread()) {
            vh.getNotificationIcon().setVisibility(View.VISIBLE);
        } else {
            vh.getNotificationIcon().setVisibility(View.GONE);
        }
    }

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

        public ImageView getNotificationIcon() {
            return mNotificationIcon;
        }
    }
}
