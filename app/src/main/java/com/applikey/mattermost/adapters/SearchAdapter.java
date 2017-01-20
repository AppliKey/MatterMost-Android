package com.applikey.mattermost.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.applikey.mattermost.R;
import com.applikey.mattermost.adapters.channel.viewholder.GroupChatListViewHolder;
import com.applikey.mattermost.adapters.viewholders.ChatListViewHolder;
import com.applikey.mattermost.adapters.viewholders.ClickableViewHolder;
import com.applikey.mattermost.adapters.viewholders.MessageChannelViewHolder;
import com.applikey.mattermost.adapters.viewholders.SearchHeaderViewHolder;
import com.applikey.mattermost.adapters.viewholders.UserViewHolder;
import com.applikey.mattermost.listeners.OnLoadAdditionalDataListener;
import com.applikey.mattermost.models.SearchItem;
import com.applikey.mattermost.models.channel.Channel;
import com.applikey.mattermost.models.user.User;
import com.applikey.mattermost.utils.RecyclerItemClickListener;
import com.applikey.mattermost.web.images.ImageLoader;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersAdapter;

import java.util.ArrayList;
import java.util.List;

public class SearchAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements RecyclerItemClickListener.OnItemClickListener, StickyRecyclerHeadersAdapter<SearchHeaderViewHolder> {

    private List<SearchItem> mDataSet = new ArrayList<>();

    private final ImageLoader mImageLoader;

    private ClickListener mClickListener;

    private final String mCurrentUserId;

    private String mSearchText;

    private OnLoadAdditionalDataListener mOnLoadAdditionalDataListener;

    public void setOnLoadAdditionalDataListener(OnLoadAdditionalDataListener onLoadAdditionalDataListener) {
        mOnLoadAdditionalDataListener = onLoadAdditionalDataListener;
    }

    public SearchAdapter(ImageLoader imageLoader, String currentUserId) {
        mCurrentUserId = currentUserId;
        mImageLoader = imageLoader;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view;
        final ClickableViewHolder viewHolder;

        if (viewType == SearchItem.Type.CHANNEL.ordinal()) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_item_group_chat, parent, false);
            viewHolder = new GroupChatListViewHolder(view, mCurrentUserId);
        } else if (viewType == SearchItem.Type.MESSAGE_CHANNEL.ordinal()) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_item_group_chat, parent, false);
            viewHolder = new MessageChannelViewHolder(view, mCurrentUserId);
        } else if (viewType == SearchItem.Type.USER.ordinal()) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_item_search_user, parent, false);
            viewHolder = new UserViewHolder(view);
        } else {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_item_chat, parent, false);
            viewHolder = new ChatListViewHolder(view, mCurrentUserId);
        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder vh, int position) {
        final SearchItem searchItem = mDataSet.get(position);
        final SearchItem.Type searchType = searchItem.getSearchType();

        if (searchType.equals(SearchItem.Type.CHANNEL)) {
            final Channel channel = searchItem.getChannel();
            loadUsersForChannel(channel, position);
            final GroupChatListViewHolder viewHolder = (GroupChatListViewHolder) vh;
            viewHolder.bind(mImageLoader, channel);
            viewHolder.setClickListener(this);
        } else if (searchType.equals(SearchItem.Type.USER)) {
            ((UserViewHolder) vh).bind(mImageLoader, this, searchItem.getUser());
        } else if (searchType.equals(SearchItem.Type.MESSAGE)) {
            ((ChatListViewHolder) vh).bind(mImageLoader, this, searchItem.getMessage(), mSearchText);
        } else if (searchType.equals(SearchItem.Type.MESSAGE_CHANNEL)) {
            final Channel channel = searchItem.getMessage().getChannel();
            loadUsersForChannel(channel, position);
            ((MessageChannelViewHolder) vh).bind(mImageLoader, this,
                    searchItem.getMessage(), mSearchText);
        }

    }

    private void loadUsersForChannel(Channel channel, int position) {
        final List<User> users = channel.getUsers();
        if (users == null || users.isEmpty()) {
            mOnLoadAdditionalDataListener.onLoadAdditionalData(channel, position);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return mDataSet.get(position).getSearchType().ordinal();
    }

    @Override
    public long getHeaderId(int position) {
        final SearchItem.Type type = mDataSet.get(position).getSearchType();
        //MESSAGE_CHANNEL and MESSAGE has the same header
        return type.equals(SearchItem.Type.MESSAGE_CHANNEL) ? SearchItem.Type.MESSAGE.ordinal() : type.ordinal();
    }

    @Override
    public SearchHeaderViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
        final View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.header_search_all, parent, false);
        return new SearchHeaderViewHolder(view);
    }

    @Override
    public void onBindHeaderViewHolder(SearchHeaderViewHolder holder, int position) {
        holder.setHeader(mDataSet.get(position).getSearchType().getRes());
    }

    @Override
    public int getItemCount() {
        return mDataSet != null ? mDataSet.size() : 0;
    }

    public void setDataSet(List<SearchItem> dataSet) {
        mDataSet = dataSet;
        notifyDataSetChanged();
    }

    public void clear() {
        mDataSet.clear();
        notifyDataSetChanged();
    }

    public void setSearchText(String searchText) {
        mSearchText = searchText;
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

    public interface ClickListener {

        void onItemClicked(SearchItem searchItem);
    }

}
