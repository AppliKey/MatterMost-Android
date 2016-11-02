package com.applikey.mattermost.adapters;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.applikey.mattermost.R;
import com.applikey.mattermost.models.channel.Channel;
import com.applikey.mattermost.models.post.Post;
import com.applikey.mattermost.models.user.User;
import com.applikey.mattermost.utils.kissUtils.utils.TimeUtil;
import com.applikey.mattermost.web.images.ImageLoader;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.realm.RealmRecyclerViewAdapter;
import io.realm.RealmResults;

public class PostAdapter extends RealmRecyclerViewAdapter<Post, PostAdapter.ViewHolder> {

    private static final int MY_POST_VIEW_TYPE = 0;
    private static final int OTHERS_POST_VIEW_TYPE = 1;

    private final String mCurrentUserId;
    private final ImageLoader mImageLoader;
    private final OnLongClickListener mOnLongClickListener;
    private final Channel.ChannelType mChannelType;

    private long mLastViewed;
    private int mNewMessageIndicatorPosition = -1;

    public PostAdapter(Context context,
            RealmResults<Post> posts,
            String currentUserId,
            ImageLoader imageLoader,
            Channel.ChannelType channelType,
            long lastViewed,
            OnLongClickListener onLongClickListener) {
        super(context, posts, true);
        mCurrentUserId = currentUserId;
        mImageLoader = imageLoader;
        mChannelType = channelType;
        mLastViewed = lastViewed;
        mOnLongClickListener = onLongClickListener;
        setHasStableIds(true);
    }

    @Override
    public long getItemId(int index) {
        final Post item = getItem(index);
        return item != null ? item.hashCode() : 0;
    }

    public void setLastViewed(long lastViewed) {
        mLastViewed = lastViewed;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        final int layoutId = viewType == MY_POST_VIEW_TYPE
                ? R.layout.list_item_post_my : R.layout.list_item_post_other;

        final View v = inflater.inflate(layoutId, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Post post = getData().get(position);

        final boolean isLastPost = position == getData().size() - 1;
        final boolean isFirstPost = position == 0;

        Post previousPost = null;
        Post nextPost = null;

        if (!isLastPost) {
            final int nextPostPosition = position + 1;
            final Post nextPostTemp = getData().get(nextPostPosition);
            nextPost = nextPostTemp != null ? nextPostTemp : null;
        }

        if (!isFirstPost) {
            final int previousPostPosition = position - 1;
            final Post previousPostTemp = getData().get(previousPostPosition);
            previousPost = previousPostTemp != null ? previousPostTemp : null;
        }

        final boolean showDate = isLastPost || !isPostsSameDate(post, nextPost);
        final boolean showAuthor = isLastPost || showDate || !isPostsSameAuthor(nextPost, post);
        final boolean showTime = isFirstPost || !isPostsSameSecond(post, previousPost) || !isPostsSameAuthor(post, previousPost);

        final boolean mNewMessageIndicatorShowed = mNewMessageIndicatorPosition != -1;
        final boolean showNewMessageIndicator = (!mNewMessageIndicatorShowed &&
                mLastViewed < post.getCreatedAt() &&
                !isLastPost && nextPost.getCreatedAt() < mLastViewed) ||
                mNewMessageIndicatorPosition == holder.getAdapterPosition();

        if (showNewMessageIndicator) {
            mNewMessageIndicatorPosition = holder.getAdapterPosition();
        }

        holder.bindHeader(showNewMessageIndicator, showDate);

        if (isMy(post)) {
            holder.bindOwnPost(mChannelType, post, showAuthor, showTime, showDate, mOnLongClickListener);
        } else {
            holder.bindOtherPost(mChannelType, post, showAuthor, showTime, showDate, mImageLoader);
        }
    }

    @Override
    public int getItemViewType(int position) {
        final Post post = getItem(position);
        return isMy(post) ? MY_POST_VIEW_TYPE : OTHERS_POST_VIEW_TYPE;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        @Nullable
        @Bind(R.id.iv_status)
        ImageView mIvStatus;

        @Nullable
        @Bind(R.id.iv_preview_image)
        ImageView mIvPreviewImage;

        @Nullable
        @Bind(R.id.iv_preview_image_layout)
        FrameLayout mIvPreviewImageLayout;

        @Bind(R.id.tv_date)
        TextView mTvDate;

        @Bind(R.id.tv_message)
        TextView mTvMessage;

        @Bind(R.id.tv_timestamp)
        TextView mTvTimestamp;

        @Bind(R.id.tv_new_message)
        TextView mTvNewMessage;

        @Bind(R.id.tv_name)
        TextView mTvName;

        ViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }

        private void bindHeader(boolean showNewMessageIndicator, boolean showDate) {
            mTvDate.setVisibility(showDate ? View.VISIBLE : View.GONE);
            mTvNewMessage.setVisibility(showNewMessageIndicator ? View.VISIBLE : View.GONE);
        }

        private void bind(Channel.ChannelType channelType, Post post, boolean showAuthor, boolean showTime, boolean showDate) {
            mTvDate.setText(TimeUtil.formatDateOnly(post.getCreatedAt()));
            mTvTimestamp.setText(TimeUtil.formatTimeOnly(post.getCreatedAt()));
            mTvName.setText(post.getAuthor().toString());
            mTvMessage.setText(post.getMessage());

            mTvName.setVisibility(showAuthor ? View.VISIBLE : View.GONE);
            mTvTimestamp.setVisibility(showTime ? View.VISIBLE : View.GONE);

            if (channelType == Channel.ChannelType.DIRECT) {
                mTvName.setVisibility(View.GONE);
            }
        }

        void bindOwnPost(Channel.ChannelType channelType, Post post, boolean showAuthor, boolean showTime, boolean showDate,
                OnLongClickListener onLongClickListener) {
            bind(channelType, post, showAuthor, showTime, showDate);

            itemView.setOnLongClickListener(v -> {
                onLongClickListener.onLongClick(post);
                return true;
            });
            mTvName.setText(R.string.you);
        }

        void bindOtherPost(Channel.ChannelType channelType, Post post, boolean showAuthor, boolean showTime,
                boolean showDate, ImageLoader imageLoader) {
            bind(channelType, post, showAuthor, showTime, showDate);

            final User author = post.getAuthor();

            final String previewImagePath = author.getProfileImage();
            if (mIvPreviewImageLayout != null && mIvPreviewImage != null
                    && mIvStatus != null && previewImagePath != null
                    && !previewImagePath.isEmpty()) {
                imageLoader.displayCircularImage(previewImagePath, mIvPreviewImage);
                mIvStatus.setImageResource(User.Status.from(author.getStatus()).getDrawableId());
                mIvPreviewImageLayout.setVisibility(showAuthor ? View.VISIBLE : View.INVISIBLE);
            }

            if (mIvPreviewImageLayout != null && channelType == Channel.ChannelType.DIRECT) {
                mIvPreviewImageLayout.setVisibility(View.GONE);
            }
        }
    }

    private boolean isMy(Post post) {
        return post.getUserId().equals(mCurrentUserId);
    }

    private boolean isPostsSameAuthor(Post post, Post nextPost) {
        if (post == null || nextPost == null) {
            return false;
        }
        return post.getUserId().equals(nextPost.getUserId());
    }

    private boolean isPostsSameSecond(Post post, Post nextPost) {
        if (post == null || nextPost == null) {
            return false;
        }
        return TimeUtil.sameTime(post.getCreatedAt(), nextPost.getCreatedAt());
    }

    private boolean isPostsSameDate(Post post, Post nextPost) {
        if (post == null || nextPost == null) {
            return false;
        }
        return TimeUtil.sameDate(post.getCreatedAt(), nextPost.getCreatedAt());
    }

    @FunctionalInterface
    public interface OnLongClickListener {

        void onLongClick(Post post);
    }
}
