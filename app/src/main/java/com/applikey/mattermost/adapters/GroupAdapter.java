package com.applikey.mattermost.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.applikey.mattermost.R;
import com.applikey.mattermost.models.groups.Channel;
import com.applikey.mattermost.models.groups.ChannelWithMetadata;
import com.applikey.mattermost.utils.kissUtils.utils.TimeUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.ViewHolder> {

    private List<ChannelWithMetadata> mDataSet = null;

    public GroupAdapter(Collection<ChannelWithMetadata> dataSet) {
        super();

        mDataSet = new ArrayList<>(dataSet.size());
        mDataSet.addAll(dataSet);
        Collections.sort(mDataSet, ChannelWithMetadata.COMPARATOR_BY_DATE);
    }

    @Override
    public GroupAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_channel, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(GroupAdapter.ViewHolder holder, int position) {
        final ChannelWithMetadata data = mDataSet.get(position);

        final long lastPostAt = data.getChannel().getLastPostAt();

        holder.getChannelName().setText(data.getChannel().getDisplayName());
        holder.getLastMessageTime().setText(
                TimeUtil.formatTimeOrDate(lastPostAt != 0 ? lastPostAt :
                        data.getChannel().getCreatedAt()));
    }

    @Override
    public int getItemCount() {
        return mDataSet != null ? mDataSet.size() : 0;
    }

    /* package */
    class ViewHolder extends RecyclerView.ViewHolder {

        private final View mRoot;

        @Bind(R.id.iv_preview_image)
        ImageView mPreviewImage;

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

        TextView getChannelName() {
            return mChannelName;
        }

        TextView getLastMessageTime() {
            return mLastMessageTime;
        }

        TextView getMessagePreview() {
            return mMessagePreview;
        }
    }
}
