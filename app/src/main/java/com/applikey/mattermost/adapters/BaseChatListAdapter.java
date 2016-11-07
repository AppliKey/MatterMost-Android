package com.applikey.mattermost.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.annimon.stream.function.FunctionalInterface;
import com.applikey.mattermost.models.channel.Channel;
import com.applikey.mattermost.web.images.ImageLoader;

import io.realm.OrderedRealmCollection;
import io.realm.RealmRecyclerViewAdapter;
import io.realm.RealmResults;

public abstract class BaseChatListAdapter<T extends RecyclerView.ViewHolder> extends RealmRecyclerViewAdapter<Channel, T> {

    private ClickListener mClickListener = null;

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

    @FunctionalInterface
    public interface ClickListener {
        void onItemClicked(Channel channel);
    }

    public void setOnClickListener(ClickListener listener) {
        this.mClickListener = listener;
    }

    protected final View.OnClickListener mOnClickListener = v -> {
        final OrderedRealmCollection<Channel> data = getData();
        if (data == null) {
            return;
        }
        final int position = (Integer) v.getTag();

        final Channel team = data.get(position);

        if (mClickListener != null) {
            mClickListener.onItemClicked(team);
        }
    };
}

