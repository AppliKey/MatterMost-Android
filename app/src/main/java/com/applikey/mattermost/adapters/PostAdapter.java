package com.applikey.mattermost.adapters;

import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.annimon.stream.Optional;
import com.annimon.stream.Stream;
import com.applikey.mattermost.R;
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

    public PostAdapter(String currentUserId, ImageLoader imageLoader, OnLongClickListener onLongClickListener) {
        this.mCurrentUserId = currentUserId;
        this.mImageLoader = imageLoader;
        this.mOnLongClickListener = onLongClickListener;
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
                ? R.layout.list_item_post_my : R.layout.list_item_post;

        final View v = inflater.inflate(layoutId, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final PostDto dto = mData.get(position);

        boolean showDate = position == 0 ||
                !oneDatePosts(dto.getPost(), mData.get(position - 1).getPost());

        boolean showAuthor = position == 0 ||
                !hasSameAuthor(mData.get(position - 1).getPost(), dto.getPost()) ||
                showDate;

        boolean showTime = position == mData.size() - 1 ||
                !oneTimePosts(dto.getPost(), mData.get(position + 1).getPost()) ||
                !hasSameAuthor(dto.getPost(), mData.get(position + 1).getPost());

        if (isMy(dto.getPost())) {
            holder.bindOwn(dto, showAuthor, showTime, showDate, mOnLongClickListener);
        } else {
            holder.bindOther(dto, showAuthor, showTime, showDate, mImageLoader);
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
        RelativeLayout mIvPreviewImageLayout;

        @Bind(R.id.tv_date)
        TextView mTvDate;

        @Bind(R.id.tv_message)
        TextView mTvMessage;

        @Bind(R.id.tv_timestamp)
        TextView mTvTimestamp;

        @Bind(R.id.tv_name)
        TextView mTvName;

        ViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }

        private void bind(PostDto dto, boolean showAuthor, boolean showTime, boolean showDate) {
            mTvDate.setText(TimeUtil.formatDateOnly(dto.getPost().getCreatedAt()));
            mTvTimestamp.setText(TimeUtil.formatTimeOnly(dto.getPost().getCreatedAt()));
            mTvName.setText(dto.getAuthorName());
            mTvMessage.setText(dto.getPost().getMessage());

            mTvDate.setVisibility(showDate ? View.VISIBLE : View.GONE);
            mTvName.setVisibility(showAuthor ? View.VISIBLE : View.GONE);
            mTvTimestamp.setVisibility(showTime ? View.VISIBLE : View.GONE);
        }

        void bindOwn(PostDto dto, boolean showAuthor, boolean showTime, boolean showDate,
                OnLongClickListener onLongClickListener) {
            bind(dto, showAuthor, showTime, showDate);

            itemView.setOnLongClickListener(v -> {
                onLongClickListener.onLongClick(dto.getPost());
                return true;
            });
        }

        void bindOther(PostDto dto, boolean showAuthor, boolean showTime,
                boolean showDate, ImageLoader imageLoader) {
            bind(dto, showAuthor, showTime, showDate);

            final String previewImagePath = dto.getAuthorAvatar();
            if (mIvPreviewImageLayout != null && mIvPreviewImage != null
                    && mIvStatus != null && previewImagePath != null
                    && !previewImagePath.isEmpty()) {
                imageLoader.displayCircularImage(previewImagePath, mIvPreviewImage);
                mIvStatus.setImageResource(dto.getStatus().getDrawableId());
                mIvPreviewImageLayout.setVisibility(showAuthor ? View.VISIBLE : View.INVISIBLE);
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

    private boolean hasSameAuthor(Post post, Post nextPost) {
        return post.getUserId().equals(nextPost.getUserId());
    }

    private boolean oneTimePosts(Post post, Post nextPost) {
        return TimeUtil.sameTime(post.getCreatedAt(), nextPost.getCreatedAt());
    }

    private boolean oneDatePosts(Post post, Post nextPost) {
        return TimeUtil.sameDate(post.getCreatedAt(), nextPost.getCreatedAt());
    }

    @FunctionalInterface
    public interface OnLongClickListener {

        void onLongClick(Post post);

    }

}
