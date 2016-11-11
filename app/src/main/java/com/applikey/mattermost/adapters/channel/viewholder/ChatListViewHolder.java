package com.applikey.mattermost.adapters.channel.viewholder;

import android.view.View;
import android.widget.ImageView;

import com.applikey.mattermost.R;

import butterknife.Bind;

public class ChatListViewHolder extends BaseChatListViewHolder {

    @Bind(R.id.iv_preview_image)
    ImageView mPreviewImage;

    @Bind(R.id.iv_channel_icon)
    ImageView mChannelIcon;

    @Bind(R.id.iv_status_bg)
    ImageView mStatusBackground;

    @Bind(R.id.iv_status)
    ImageView mStatus;

    public ChatListViewHolder(View itemView) {
        super(itemView);
    }

    public ImageView getPreviewImage() {
        return mPreviewImage;
    }

    public ImageView getChannelIcon() {
        return mChannelIcon;
    }

    public ImageView getStatusBackground() {
        return mStatusBackground;
    }

    public ImageView getStatus() {
        return mStatus;
    }
}