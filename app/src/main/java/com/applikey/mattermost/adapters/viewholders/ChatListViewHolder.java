package com.applikey.mattermost.adapters.viewholders;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.applikey.mattermost.R;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ChatListViewHolder extends ClickableViewHolder {

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

    @Bind(R.id.container)
    LinearLayout mContainer;

    public ChatListViewHolder(View itemView) {
        super(itemView);

        mRoot = itemView;

        ButterKnife.bind(this, itemView);
    }

    public View getRoot() {
        return mRoot;
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

    public TextView getChannelName() {
        return mChannelName;
    }

    public TextView getLastMessageTime() {
        return mLastMessageTime;
    }

    public TextView getMessagePreview() {
        return mMessagePreview;
    }

    public ImageView getNotificationIcon() {
        return mNotificationIcon;
    }

    public LinearLayout getContainer() {
        return mContainer;
    }


}