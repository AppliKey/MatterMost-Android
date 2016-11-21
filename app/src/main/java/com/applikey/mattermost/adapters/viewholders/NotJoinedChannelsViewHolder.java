package com.applikey.mattermost.adapters.viewholders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.applikey.mattermost.R;
import com.applikey.mattermost.models.channel.Channel;

import butterknife.Bind;
import butterknife.ButterKnife;

public class NotJoinedChannelsViewHolder extends RecyclerView.ViewHolder {

    @Bind(R.id.tv_more_channel_name)
    TextView mTvNotJoinedChannelName;

    @Bind(R.id.tv_more_channel_members_count)
    TextView mTvNotJoinedChannelNameMembersCount;

    public NotJoinedChannelsViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(itemView);
    }

    public void bind(Channel channel) {
        final String channelName = channel.getDisplayName();
        final int channelMembersCount = channel.getUsers() == null ? 0 : channel.getUsers().size();
        mTvNotJoinedChannelName.setText(channelName);
        final String formattedChannelMembersCount =
                itemView.getResources().getQuantityString(R.plurals.members_count, channelMembersCount, channelMembersCount);
        mTvNotJoinedChannelNameMembersCount.setText(formattedChannelMembersCount);
    }

}
