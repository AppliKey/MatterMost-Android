package com.applikey.mattermost.adapters.channel.viewholder;

import android.content.Context;
import android.support.annotation.CallSuper;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.applikey.mattermost.R;
import com.applikey.mattermost.adapters.viewholders.ClickableViewHolder;
import com.applikey.mattermost.models.channel.Channel;
import com.applikey.mattermost.models.post.Message;
import com.applikey.mattermost.models.post.Post;
import com.applikey.mattermost.models.user.User;
import com.applikey.mattermost.utils.kissUtils.utils.TimeUtil;
import com.applikey.mattermost.web.images.ImageLoader;

import butterknife.BindView;
import butterknife.ButterKnife;

public abstract class BaseChatListViewHolder extends ClickableViewHolder {

    private String mCurrentUserId;

    @BindView(R.id.iv_notification_icon)
    ImageView mNotificationIcon;

    @BindView(R.id.tv_channel_name)
    TextView mChannelName;

    @BindView(R.id.tv_last_message_time)
    TextView mLastMessageTime;

    @BindView(R.id.tv_message_preview)
    TextView mMessagePreview;

    public BaseChatListViewHolder(View itemView) {
        super(itemView);

        ButterKnife.bind(this, itemView);
    }

    public BaseChatListViewHolder(View itemView, String userId) {
        this(itemView);
        mCurrentUserId = userId;
    }

    public View getContainer() {
        return itemView;
    }

    public ImageView getNotificationIcon() {
        return mNotificationIcon;
    }

    public TextView getName() {
        return mChannelName;
    }

    public TextView getLastMessageTime() {
        return mLastMessageTime;
    }

    public TextView getMessagePreview() {
        return mMessagePreview;
    }

    @CallSuper
    public void bind(ImageLoader imageLoader, Channel channel) {

        final long lastPostAt = channel.getLastPostAt();

        getName().setText(channel.getDisplayName());

        final String messagePreview = getMessagePreview(channel, getContainer().getContext());

        getMessagePreview().setText(messagePreview);
        getLastMessageTime().setText(
                TimeUtil.formatTimeOrDateOnlyChannel(lastPostAt != 0 ? lastPostAt : channel.getCreatedAt()));

        setUnreadStatus(channel);
    }

    protected void setMessageDate(Message message) {
        getLastMessageTime().setText(
                TimeUtil.formatTimeOrDateOnlyChannel(message.getPost().getCreatedAt()));
    }

    private String getMessagePreview(Channel channel, Context context) {
        final Post lastPost = channel.getLastPost();
        final String messagePreview;
        if (lastPost == null) {
            messagePreview = context.getString(R.string.channel_preview_message_placeholder);
        } else if (isMy(lastPost)) {
            messagePreview = context.getString(R.string.channel_post_author_name_format, "You") +
                    lastPost.getMessage();
        } else if (!channel.getType().equals(Channel.ChannelType.DIRECT.getRepresentation())) {
            final String postAuthor = User.getDisplayableName(lastPost.getAuthor());
            messagePreview = context.getString(R.string.channel_post_author_name_format, postAuthor)
                    +
                    lastPost.getMessage();
        } else {
            messagePreview = lastPost.getMessage();
        }
        return messagePreview;
    }

    private void setUnreadStatus(Channel channel) {
        if (channel.hasUnreadMessages()) {
            getNotificationIcon().setVisibility(View.VISIBLE);
            getContainer().setBackgroundResource(R.color.unreadBackground);
        } else {
            getNotificationIcon().setVisibility(View.GONE);
            getContainer().setBackgroundResource(android.R.color.white);
        }
    }

    private boolean isMy(Post post) {
        return post.getUserId().equals(mCurrentUserId);
    }

    protected String getAuthorPrefix(Context context, Message message) {
        final Post post = message.getPost();
        if (mCurrentUserId.equals(post.getUserId())) {
            return context.getString(R.string.chat_you);
        }
        return message.getUser().getUsername() + ":";
    }
}
