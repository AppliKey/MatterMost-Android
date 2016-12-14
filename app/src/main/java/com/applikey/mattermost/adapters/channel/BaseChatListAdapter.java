package com.applikey.mattermost.adapters.channel;

import android.content.Context;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.view.View;

import com.applikey.mattermost.adapters.channel.viewholder.BaseChatListViewHolder;
import com.applikey.mattermost.models.channel.Channel;
import com.applikey.mattermost.web.images.ImageLoader;

import io.realm.OrderedRealmCollection;
import io.realm.RealmRecyclerViewAdapter;
import io.realm.RealmResults;

public abstract class BaseChatListAdapter<VH extends BaseChatListViewHolder>
        extends RealmRecyclerViewAdapter<Channel, VH> {

    private ChannelListener mChannelListener = null;

    protected final ImageLoader mImageLoader;

    protected final String mCurrentUserId;

    protected final Context mContext;

    public BaseChatListAdapter(@NonNull Context context, RealmResults<Channel> data,
            ImageLoader imageLoader, String currentUserId) {
        super(context, data, true);
        mContext = context;
        mImageLoader = imageLoader;
        mCurrentUserId = currentUserId;
        setHasStableIds(true);
    }

    @Override
    public long getItemId(int index) {
        final Channel item = getItem(index);
        return item != null ? item.hashCode() : 0;
    }

    @Override
    @CallSuper
    public void onBindViewHolder(VH vh, int position) {
        final OrderedRealmCollection<Channel> data = getData();
        if (data == null) {
            return;
        }
        final Channel channel = data.get(position);

        if (mChannelListener != null) {
            mChannelListener.onLoadAdditionalData(channel);
        }

        vh.bind(mImageLoader, channel);

        vh.getContainer().setTag(position);
    }

    public void setChannelListener(ChannelListener listener) {
        this.mChannelListener = listener;
    }

    public final View.OnClickListener mOnClickListener = view -> {
        final OrderedRealmCollection<Channel> data = getData();
        if (data == null) {
            return;
        }
        final int position = (Integer) view.getTag();

        final Channel team = data.get(position);

        if (mChannelListener != null) {
            mChannelListener.onItemClicked(team);
        }
    };

    public interface ChannelListener {

        void onItemClicked(Channel channel);

        void onLoadAdditionalData(Channel channel);

    }
}

