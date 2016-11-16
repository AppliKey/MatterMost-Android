package com.applikey.mattermost.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.applikey.mattermost.R;
import com.applikey.mattermost.adapters.viewholders.ChannelViewHolder;
import com.applikey.mattermost.adapters.viewholders.ChatListViewHolder;
import com.applikey.mattermost.adapters.viewholders.UserViewHolder;
import com.applikey.mattermost.models.SearchItem;
import com.applikey.mattermost.models.channel.Channel;
import com.applikey.mattermost.models.post.Message;
import com.applikey.mattermost.models.post.Post;
import com.applikey.mattermost.models.user.User;
import com.applikey.mattermost.storage.preferences.Prefs;
import com.applikey.mattermost.utils.RecyclerItemClickListener;
import com.applikey.mattermost.utils.kissUtils.utils.TimeUtil;
import com.applikey.mattermost.web.images.ImageLoader;

import java.util.ArrayList;
import java.util.List;

public class SearchAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements RecyclerItemClickListener.OnItemClickListener {

    private List<SearchItem> mDataSet = new ArrayList<>();

    private ImageLoader mImageLoader;

    private ClickListener mClickListener = null;

    private Prefs mPrefs;

    public SearchAdapter(ImageLoader imageLoader, Prefs prefs) {
        super();

        mPrefs = prefs;
        mImageLoader = imageLoader;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                      @SearchItem.Type int viewType) {

        if (viewType == SearchItem.CHANNEL || viewType == SearchItem.MESSAGE_CHANNEL) {
            final View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_item_search_channel, parent, false);

            final ChannelViewHolder vh = new ChannelViewHolder(v);
            vh.getRoot().setOnClickListener(mOnClickListener);
            return vh;
        } else if (viewType == SearchItem.USER) {
            final View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_item_search_user, parent, false);

            final UserViewHolder vh = new UserViewHolder(v);
            vh.getRoot().setOnClickListener(mOnClickListener);
            return vh;
        } else {
            final View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_item_chat, parent, false);
            final ChatListViewHolder vh = new ChatListViewHolder(v);
            vh.getRoot().setOnClickListener(mOnClickListener);
            return vh;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder vh, int position) {
        final int searchType = mDataSet.get(position).getSearchType();
        if (searchType == SearchItem.CHANNEL) {
            bindChannelVH(vh, position);
        } else if (searchType == SearchItem.USER) {
            bindUserVH(vh, position);
        } else if (searchType == SearchItem.MESSAGE) {
            bindMessageVH(vh, position);
        } else if (searchType == SearchItem.MESSAGE_CHANNEL) {
            bindMessageChannelVH(vh, position);
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
        for (SearchItem searchItem : dataSet) {
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

    @Override
    public void onItemClick(View childView, int position) {
        mClickListener.onItemClicked(mDataSet.get(position));
    }

    @Override
    public void onItemLongPress(View childView, int position) {

    }

    private void bindMessageVH(RecyclerView.ViewHolder vh, int position) {
        final ChatListViewHolder holder = (ChatListViewHolder) vh;
        final Message message = (Message) mDataSet.get(position);
        final Post post = message.getPost();
        final Channel channel = message.getChannel();
        final User user = message.getChannel().getDirectCollocutor();

        holder.getChannelName().setText(channel.getDisplayName());
        holder.getLastMessageTime().setText(
                TimeUtil.formatTimeOnly(post.getCreatedAt()));

        String messageText = getAuthorPrefix(holder.getChannelIcon().getContext(), post) + post.getMessage();

        holder.getMessagePreview().setText(messageText);

        setMessageStatus(holder, user);

        setMessageReadStatus(holder, channel);

        setMessageChannelIcon(holder, user);

        holder.setClickListener(this);
    }

    private String getAuthorPrefix(Context context, Post post){
        if(mPrefs.getCurrentUserId().equals(post.getUserId())){
            return context.getString(R.string.chat_you);
        }
        return post.getAuthor() + ":";
    }
    private void setMessageStatus(ChatListViewHolder holder, User user){
        final User.Status status = user != null ?
                User.Status.from(user.getStatus()) : null;
        if (status != null) {
            holder.getStatus().setImageResource(status.getDrawableId());
        }
        holder.getStatusBackground().setVisibility(View.VISIBLE);
        holder.getStatus().setVisibility(View.VISIBLE);
    }

    private void setMessageReadStatus(ChatListViewHolder holder, Channel channel){
        if (channel.hasUnreadMessages()) {
            holder.getNotificationIcon().setVisibility(View.VISIBLE);
            holder.getContainer().setBackgroundResource(R.color.unread_background);
        } else {
            holder.getNotificationIcon().setVisibility(View.GONE);
            holder.getContainer().setBackgroundResource(android.R.color.white);
        }
    }

    private void setMessageChannelIcon(ChatListViewHolder holder, User user){
        final String previewImagePath = user != null ?
                user.getProfileImage() : null;
        final ImageView previewImage = holder.getPreviewImage();
        if (previewImagePath != null && !previewImagePath.isEmpty()) {
            mImageLoader.displayCircularImage(previewImagePath, previewImage);
        } else {
            previewImage.setImageResource(R.drawable.no_resource);
        }
    }

    private void bindMessageChannelVH(RecyclerView.ViewHolder vh, int position) {
        final Message message = (Message) mDataSet.get(position);
        final Post post = message.getPost();
        final Channel channel = message.getChannel();
        final ChannelViewHolder holder = (ChannelViewHolder) vh;

        holder.getChannelName().setText(channel.getDisplayName());

        String messageText = getAuthorPrefix(holder.getRoot().getContext(), post) + post.getMessage();

        holder.getTvMessage().setText(messageText);
        holder.setClickListener(this);

    }

    private void bindUserVH(RecyclerView.ViewHolder vh, int position) {
        final User data = (User) mDataSet.get(position);
        final UserViewHolder viewHolder = (UserViewHolder) vh;

        viewHolder.getChannelName().setText(User.getDisplayableName(data));

        setChannelIcon(viewHolder, data);

        viewHolder.getRoot().setTag(position);

        viewHolder.setClickListener(this);
    }

    private void bindChannelVH(RecyclerView.ViewHolder vh, int position) {
        final Channel data = (Channel) mDataSet.get(position);
        final ChannelViewHolder viewHolder = (ChannelViewHolder) vh;

        viewHolder.getChannelName().setText(data.getDisplayName());

        setChannelIcon(viewHolder, data);
        setMessage(viewHolder, data);

        viewHolder.getRoot().setTag(position);

        viewHolder.setClickListener(this);
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
