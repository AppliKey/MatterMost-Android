package com.applikey.mattermost.adapters.channel.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.applikey.mattermost.R;

import butterknife.Bind;
import butterknife.ButterKnife;

public abstract class BaseChatListViewHolder extends RecyclerView.ViewHolder {

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

    public BaseChatListViewHolder(View itemView) {
        super(itemView);

        ButterKnife.bind(this, itemView);
    }

    public View getContainer() {
        return itemView;
    }

    public ImageView getNotificationIcon() {
        return mNotificationIcon;
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
}