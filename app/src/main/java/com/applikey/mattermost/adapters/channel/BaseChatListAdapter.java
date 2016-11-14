package com.applikey.mattermost.adapters.channel;

import android.content.Context;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.view.View;

import com.applikey.mattermost.R;
import com.applikey.mattermost.adapters.channel.viewholder.BaseChatListViewHolder;
import com.applikey.mattermost.models.channel.Channel;
import com.applikey.mattermost.models.post.Post;
import com.applikey.mattermost.models.user.User;
import com.applikey.mattermost.utils.kissUtils.utils.TimeUtil;
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

        final long lastPostAt = channel.getLastPostAt();

        vh.getChannelName().setText(channel.getDisplayName());

        final String messagePreview = getMessagePreview(channel, mContext);

        vh.getMessagePreview().setText(messagePreview);
        vh.getLastMessageTime().setText(
                TimeUtil.formatTimeOrDateOnlyChannel(lastPostAt != 0 ? lastPostAt :
                        channel.getCreatedAt()));

        setUnreadStatus(vh, channel);

        vh.getContainer().setTag(position);
    }

    public void setOnClickListener(ChannelListener listener) {
        this.mChannelListener = listener;
    }

    private String getMessagePreview(Channel channel, Context context) {
        final Post lastPost = channel.getLastPost();
        final String messagePreview;
        if (channel.getLastPost() == null) {
            messagePreview = context.getString(R.string.channel_preview_message_placeholder);
        } else if (isMy(lastPost)) {
            messagePreview = context.getString(R.string.channel_post_author_name_format, "You") +
                    channel.getLastPost().getMessage();
        } else if (!channel.getType().equals(Channel.ChannelType.DIRECT.getRepresentation())) {
            final String postAuthor = User.getDisplayableName(lastPost.getAuthor());
            messagePreview = context.getString(R.string.channel_post_author_name_format, postAuthor)
                    +
                    channel.getLastPost().getMessage();
        } else {
            messagePreview = channel.getLastPost().getMessage();
        }
        return messagePreview;
    }

    private void setUnreadStatus(VH vh, Channel data) {
        if (data.hasUnreadMessages()) {
            vh.getNotificationIcon().setVisibility(View.VISIBLE);
            vh.getContainer().setBackgroundResource(R.color.unread_background);
        } else {
            vh.getNotificationIcon().setVisibility(View.GONE);
            vh.getContainer().setBackgroundResource(android.R.color.white);
        }
    }

    private boolean isMy(Post post) {
        return post.getUserId().equals(mCurrentUserId);
    }

    public final View.OnClickListener mOnClickListener = v -> {
        final OrderedRealmCollection<Channel> data = getData();
        if (data == null) {
            return;
        }
        final int position = (Integer) v.getTag();

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

