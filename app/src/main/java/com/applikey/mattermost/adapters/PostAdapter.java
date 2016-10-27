package com.applikey.mattermost.adapters;

import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.annimon.stream.Optional;
import com.annimon.stream.Stream;
import com.applikey.mattermost.R;
import com.applikey.mattermost.models.channel.Channel;
import com.applikey.mattermost.models.post.Post;
import com.applikey.mattermost.models.post.PostDto;
import com.applikey.mattermost.utils.kissUtils.utils.TimeUtil;
import com.applikey.mattermost.web.images.ImageLoader;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {

    private static final int MY_POST_VIEW_TYPE = 0;
    private static final int OTHERS_POST_VIEW_TYPE = 1;

    private final String mCurrentUserId;
    private List<PostDto> mData = new ArrayList<>();
    private final ImageLoader mImageLoader;
    private final OnLongClickListener mOnLongClickListener;
    private final Channel.ChannelType mChannelType;
    private long mLastViewed;
    private int mNewMessageIndicatorPosition = -1;

    public PostAdapter(String currentUserId,
            ImageLoader imageLoader,
            Channel.ChannelType channelType,
            long lastViewed,
            OnLongClickListener onLongClickListener) {
        mCurrentUserId = currentUserId;
        mImageLoader = imageLoader;
        mChannelType = channelType;
        mLastViewed = lastViewed;
        mOnLongClickListener = onLongClickListener;
    }

    public void updateDataSet(List<PostDto> data) {
        mData = data;
        notifyDataSetChanged();
    }

    public void deletePost(Post post) {
        final Optional<PostDto> optionalPost = getPostDto(post);
        if (optionalPost.isPresent()) {
            final PostDto postDto = optionalPost.get();
            final int position = mData.indexOf(postDto);

            notifyItemRemoved(position);
            mData.remove(position);
        }
    }

    public void updatePost(Post post) {
        final Optional<PostDto> optionalPost = getPostDto(post);
        if (optionalPost.isPresent()) {
            final PostDto postDto = optionalPost.get();
            final int position = mData.indexOf(postDto);

            notifyItemChanged(position);
            postDto.setPost(post);
        }
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
        final PostDto dto = mData.get(position);
        final Post post = dto.getPost();

        final boolean isLastPost = position == mData.size() - 1;
        final boolean isFirstPost = position == 0;

        Post previousPost = null;
        Post nextPost = null;

        if (!isLastPost) {
            final int nextPostPosition = position + 1;
            final PostDto nextPostDto = mData.get(nextPostPosition);
            nextPost = nextPostDto != null ? nextPostDto.getPost() : null;
        }

        if (!isFirstPost) {
            final int previousPostPosition = position - 1;
            final PostDto previousPostDto = mData.get(previousPostPosition);
            previousPost = previousPostDto != null ? previousPostDto.getPost() : null;
        }

        final boolean showDate = isLastPost || !isPostsSameDate(post, nextPost);
        final boolean showAuthor = isLastPost || showDate || !isPostsSameAuthor(nextPost, post);
        final boolean showTime = isFirstPost || !isPostsSameSecond(post, previousPost) || !isPostsSameAuthor(post, previousPost);
        final boolean showNewMessageIndicator = (mNewMessageIndicatorPosition == -1 &&
                mLastViewed < post.getCreatedAt() &&
                !isLastPost && nextPost.getCreatedAt() < mLastViewed) ||
                mNewMessageIndicatorPosition == holder.getAdapterPosition();

        if (showNewMessageIndicator) {
            mNewMessageIndicatorPosition = holder.getAdapterPosition();
        }

        if (isMy(post)) {
            holder.bindOwnPost(mChannelType, dto, showAuthor, showNewMessageIndicator, showTime, showDate, mOnLongClickListener);
        } else {
            holder.bindOtherPost(mChannelType, dto, showAuthor, showNewMessageIndicator, showTime, showDate, mImageLoader);
        }
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    @Override
    public int getItemViewType(int position) {
        final PostDto dto = mData.get(position);
        return isMy(dto.getPost()) ? MY_POST_VIEW_TYPE : OTHERS_POST_VIEW_TYPE;
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

        private void bind(Channel.ChannelType channelType, PostDto dto, boolean showAuthor,
                boolean showNewMessageIndicator, boolean showTime, boolean showDate) {
            mTvDate.setText(TimeUtil.formatDateOnly(dto.getPost().getCreatedAt()));
            mTvTimestamp.setText(TimeUtil.formatTimeOrDateTime(dto.getPost().getCreatedAt()));
            mTvName.setText(dto.getAuthorName());
            mTvMessage.setText(dto.getPost().getMessage());

            mTvDate.setVisibility(showDate ? View.VISIBLE : View.GONE);
            mTvName.setVisibility(showAuthor ? View.VISIBLE : View.GONE);
            mTvNewMessage.setVisibility(showNewMessageIndicator ? View.VISIBLE : View.GONE);
            mTvTimestamp.setVisibility(showTime ? View.VISIBLE : View.GONE);

            if (channelType == Channel.ChannelType.DIRECT) {
                mTvName.setVisibility(View.GONE);
            }
        }

        void bindOwnPost(Channel.ChannelType channelType, PostDto dto, boolean showAuthor,
                boolean showNewMessageIndicator, boolean showTime, boolean showDate,
                OnLongClickListener onLongClickListener) {
            bind(channelType, dto, showAuthor, showNewMessageIndicator, showTime, showDate);

            itemView.setOnLongClickListener(v -> {
                onLongClickListener.onLongClick(dto.getPost());
                return true;
            });

            mTvName.setText(R.string.you);
        }

        void bindOtherPost(Channel.ChannelType channelType, PostDto dto, boolean showAuthor,
                boolean showNewMessageIndicator, boolean showTime, boolean showDate, ImageLoader imageLoader) {
            bind(channelType, dto, showAuthor, showNewMessageIndicator, showTime, showDate);

            final String previewImagePath = dto.getAuthorAvatar();
            if (mIvPreviewImageLayout != null && mIvPreviewImage != null
                    && mIvStatus != null && previewImagePath != null
                    && !previewImagePath.isEmpty()) {
                imageLoader.displayCircularImage(previewImagePath, mIvPreviewImage);
                mIvStatus.setImageResource(dto.getStatus().getDrawableId());
                mIvPreviewImageLayout.setVisibility(showAuthor ? View.VISIBLE : View.INVISIBLE);

                if (channelType == Channel.ChannelType.DIRECT) {
                    mIvPreviewImageLayout.setVisibility(View.GONE);
                }
            }
        }
    }

    private boolean isMy(Post post) {
        return post.getUserId().equals(mCurrentUserId);
    }

    private Optional<PostDto> getPostDto(Post post) {
        return Stream.of(mData)
                .filter(dto -> dto.getPost().getId().equals(post.getId()))
                .findFirst();
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
