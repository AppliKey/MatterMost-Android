package com.applikey.mattermost.adapters.viewholders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.applikey.mattermost.R;
import com.applikey.mattermost.models.channel.Channel;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NotJoinedChannelsViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.tv_more_channel_name)
    TextView mTvNotJoinedChannelName;

    public NotJoinedChannelsViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void bind(Channel channel) {
        final String channelName = channel.getDisplayName();
        final String formattedChannelName = itemView.getResources().getString(R.string.public_channel_name, channelName);
        mTvNotJoinedChannelName.setText(formattedChannelName);
    }

}
