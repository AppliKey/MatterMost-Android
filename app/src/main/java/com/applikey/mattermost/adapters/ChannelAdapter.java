package com.applikey.mattermost.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.applikey.mattermost.R;
import com.applikey.mattermost.adapters.viewholders.ChannelViewHolder;
import com.applikey.mattermost.models.channel.Channel;
import com.applikey.mattermost.models.post.Post;
import com.applikey.mattermost.web.images.ImageLoader;

import java.util.ArrayList;
import java.util.List;

public class ChannelAdapter extends RecyclerView.Adapter<ChannelViewHolder> {

    private static final String TAG = ChannelAdapter.class.getSimpleName();

    private List<Channel> mDataSet = new ArrayList<>();
    private ImageLoader mImageLoader;
    private ClickListener mClickListener = null;
    private final View.OnClickListener mOnClickListener = v -> {
        final int position = (Integer) v.getTag();

        final Channel channel = mDataSet.get(position);

        if (mClickListener != null) {
            mClickListener.onItemClicked(channel);
        }
    };

    public interface ClickListener {

        void onItemClicked(Channel channel);
    }

    public ChannelAdapter(ImageLoader imageLoader) {
        super();

        mImageLoader = imageLoader;
    }

    @Override
    public ChannelViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_search_channel, parent, false);

        final ChannelViewHolder vh = new ChannelViewHolder(v);
        vh.getRoot().setOnClickListener(mOnClickListener);

        return vh;
    }

    @Override
    public void onBindViewHolder(ChannelViewHolder vh, int position) {
        final Channel data = mDataSet.get(position);

        vh.getChannelName().setText(data.getDisplayName());

        setChannelIcon(vh, data);
        setMessage(vh, position);

        vh.getRoot().setTag(position);
    }

    @Override
    public int getItemCount() {
        return mDataSet != null ? mDataSet.size() : 0;
    }

    public void setDataSet(List<Channel> dataSet) {
        mDataSet.clear();
        mDataSet.addAll(dataSet);
        notifyDataSetChanged();
    }

    public void clear() {
        mDataSet.clear();
        notifyDataSetChanged();
    }

    public void setOnClickListener(ClickListener listener) {
        this.mClickListener = listener;
    }

    private void setChannelIcon(ChannelViewHolder viewHolder, Channel element) {

    }

    private void setMessage(ChannelViewHolder vh, int position) {
        final Channel channel = mDataSet.get(position);
        final Post post = channel.getLastPost();
        if (post != null) {
            vh.getTvMessage().setText(post.getMessage());
        }
    }

    /* package */

}
