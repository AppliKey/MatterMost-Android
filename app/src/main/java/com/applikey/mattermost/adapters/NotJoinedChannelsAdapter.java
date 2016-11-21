package com.applikey.mattermost.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.applikey.mattermost.R;
import com.applikey.mattermost.adapters.viewholders.NotJoinedChannelsViewHolder;
import com.applikey.mattermost.models.channel.Channel;

import java.util.ArrayList;
import java.util.List;

public class NotJoinedChannelsAdapter extends RecyclerView.Adapter<NotJoinedChannelsViewHolder> {

    private final List<Channel> mNotJoinedChannels = new ArrayList<>();
    private final OnNotJoinedChannelClick mListener;

    public NotJoinedChannelsAdapter(OnNotJoinedChannelClick listener) {
        mListener = listener;
    }

    public void addChannel(Channel channel) {
        mNotJoinedChannels.add(channel);
        notifyDataSetChanged();
    }

    public void setChannels(List<Channel> channels) {
        mNotJoinedChannels.clear();
        mNotJoinedChannels.addAll(channels);
        notifyDataSetChanged();
    }

    @Override
    public NotJoinedChannelsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        final View view = layoutInflater.inflate(R.layout.list_item_more_channels, parent, false);
        final NotJoinedChannelsViewHolder viewHolder = new NotJoinedChannelsViewHolder(view);
        viewHolder.itemView.setOnClickListener(v -> {
            final int adapterPosition = viewHolder.getAdapterPosition();
            if (adapterPosition != RecyclerView.NO_POSITION) {
                mListener.onChannelClick(mNotJoinedChannels.get(adapterPosition));
            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(NotJoinedChannelsViewHolder holder, int position) {
        final Channel channel = mNotJoinedChannels.get(position);
        holder.bind(channel);
    }

    @Override
    public int getItemCount() {
        return mNotJoinedChannels.size();
    }

    public interface OnNotJoinedChannelClick {
        void onChannelClick(Channel channel);
    }
}
