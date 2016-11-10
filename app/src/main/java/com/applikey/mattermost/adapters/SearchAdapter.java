package com.applikey.mattermost.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.applikey.mattermost.R;
import com.applikey.mattermost.adapters.viewholders.ChannelViewHolder;
import com.applikey.mattermost.adapters.viewholders.ChatListViewHolder;
import com.applikey.mattermost.adapters.viewholders.UserViewHolder;
import com.applikey.mattermost.models.SearchItem;
import com.applikey.mattermost.models.channel.Channel;
import com.applikey.mattermost.models.post.Post;
import com.applikey.mattermost.models.user.User;
import com.applikey.mattermost.web.images.ImageLoader;

import java.util.ArrayList;
import java.util.List;

public class SearchAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<SearchItem> mDataSet = new ArrayList<>();
    private ImageLoader mImageLoader;
    private ClickListener mClickListener = null;

    public SearchAdapter(ImageLoader imageLoader) {
        super();

        mImageLoader = imageLoader;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent,
            @SearchItem.Type int viewType) {

        if (viewType == SearchItem.CHANNEL) {
            final View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_item_search_channel, parent, false);

            final ChannelViewHolder vh = new ChannelViewHolder(v);
            vh.getRoot().setOnClickListener(mOnClickListener);
            return vh;
        } else if (viewType == SearchItem.USER){
            final View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_item_search_user, parent, false);

            final UserViewHolder vh = new UserViewHolder(v);
            vh.getRoot().setOnClickListener(mOnClickListener);
            return vh;
        } else {
            final View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_item_chat, parent, false);
            return new ChatListViewHolder(v);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder vh, int position) {
        final int searchType = mDataSet.get(position).getSearchType();
        if (searchType == SearchItem.CHANNEL) {
            final Channel data = (Channel) mDataSet.get(position);
            final ChannelViewHolder viewHolder = (ChannelViewHolder) vh;

            viewHolder.getChannelName().setText(data.getDisplayName());

            setChannelIcon(viewHolder, data);
            setMessage(viewHolder, data);

            viewHolder.getRoot().setTag(position);
        } else if (searchType == SearchItem.USER) {
            final User data = (User) mDataSet.get(position);
            final UserViewHolder viewHolder = (UserViewHolder) vh;

            viewHolder.getChannelName().setText(User.getDisplayableName(data));

            setChannelIcon(viewHolder, data);

            viewHolder.getRoot().setTag(position);
        } else {
            final Post post = (Post) mDataSet.get(position);
            final ChatListViewHolder holder = (ChatListViewHolder) vh;

            holder.getMessagePreview().setText(post.getMessage());
        }

    }

    @Override
    public int getItemViewType(int position) {
        return mDataSet.get(position).getSearchType();
    }

    @Override
    public int getItemCount() {
        return mDataSet != null ? mDataSet.size() : 0;
    }

    public void setDataSet(List<SearchItem> dataSet) {
        mDataSet.clear();
        for(SearchItem searchItem : dataSet) {
            mDataSet.add(searchItem);
        }
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

    private void setChannelIcon(UserViewHolder viewHolder, User element) {

        final String previewImagePath = element.getProfileImage();
        if (previewImagePath != null && !previewImagePath.isEmpty()) {
            mImageLoader.displayCircularImage(previewImagePath, viewHolder.getPreviewImage());
        }
    }

    private void setMessage(ChannelViewHolder vh, Channel channel) {
        final Post post = channel.getLastPost();
        if (post != null) {
            vh.getTvMessage().setText(post.getMessage());
        }
    }

    private final View.OnClickListener mOnClickListener = v -> {
        final int position = (Integer) v.getTag();
    };

    public interface ClickListener {
        void onItemClicked(SearchItem searchItem);
    }
}
