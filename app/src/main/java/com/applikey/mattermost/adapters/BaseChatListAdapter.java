package com.applikey.mattermost.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.annimon.stream.function.FunctionalInterface;
import com.applikey.mattermost.R;
import com.applikey.mattermost.models.channel.Channel;
import com.applikey.mattermost.models.post.Post;
import com.applikey.mattermost.models.user.User;
import com.applikey.mattermost.web.images.ImageLoader;

import io.realm.OrderedRealmCollection;
import io.realm.RealmRecyclerViewAdapter;
import io.realm.RealmResults;

public abstract class BaseChatListAdapter<T extends RecyclerView.ViewHolder> extends RealmRecyclerViewAdapter<Channel, T> {

    private ClickListener mClickListener = null;

    final ImageLoader mImageLoader;
    final String mCurrentUserId;
    final Context mContext;

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

    final View.OnClickListener mOnClickListener = v -> {
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

    String getMessagePreview(Channel channel, Context context) {
        final Post lastPost = channel.getLastPost();
        final String messagePreview;
        if (channel.getLastPost() == null) {
            messagePreview = context.getString(R.string.channel_preview_message_placeholder);
        } else if (isMy(lastPost)) {
            messagePreview = context.getString(R.string.channel_post_author_name_format, "You") +
                    channel.getLastPost().getMessage();
        } else if (!channel.getType().equals(Channel.ChannelType.DIRECT.getRepresentation())) {
            final String postAuthor = User.getDisplayableName(lastPost.getAuthor());
            messagePreview = context.getString(R.string.channel_post_author_name_format, postAuthor) +
                    channel.getLastPost().getMessage();
        } else {
            messagePreview = channel.getLastPost().getMessage();
        }
        return messagePreview;
    }

    private boolean isMy(Post post) {
        return post.getUserId().equals(mCurrentUserId);
    }

}

