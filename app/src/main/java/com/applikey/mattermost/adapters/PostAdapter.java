package com.applikey.mattermost.adapters;

import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
    private final List<PostDto> mData = new ArrayList<>();
    private final ImageLoader mImageLoader;
    private final OnLongClickListener mOnLongClickListener;

    public PostAdapter(String currentUserId, ImageLoader imageLoader, OnLongClickListener onLongClickListener) {
        this.mCurrentUserId = currentUserId;
        this.mImageLoader = imageLoader;
        this.mOnLongClickListener = onLongClickListener;
    }

    public void updateDataSet(List<PostDto> data) {
        mData.clear();
        mData.addAll(data);
        notifyDataSetChanged();
    }

    public void addToDataSet(List<PostDto> data) {
        this.mData.addAll(mData.size(), data);
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

        if (isMy(dto.getPost())) {
            holder.bindOwn(dto, mOnLongClickListener);
        } else {
            holder.bindOther(dto, mImageLoader);
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

        private void bind(PostDto dto) {
            mTvTimestamp.setText(TimeUtil.formatTimeOrDate(dto.getPost().getCreatedAt()));
            mTvName.setText(dto.getAuthorName());
            mTvMessage.setText(dto.getPost().getMessage());
        }

        void bindOwn(PostDto dto, OnLongClickListener onLongClickListener) {
            bind(dto);

            itemView.setOnLongClickListener(v -> {
                onLongClickListener.onLongClick(dto.getPost());
                return true;
            });
        }


        void bindOther(PostDto dto, ImageLoader imageLoader) {
            bind(dto);

            final String previewImagePath = dto.getAuthorAvatar();
            if (mIvPreviewImage != null && mIvStatus != null && previewImagePath != null
                    && !previewImagePath.isEmpty()) {
                imageLoader.displayCircularImage(previewImagePath, mIvPreviewImage);
                mIvStatus.setImageResource(dto.getStatus().getDrawableId());
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

    @FunctionalInterface
    public interface OnLongClickListener {
        void onLongClick(Post post);
    }
}
