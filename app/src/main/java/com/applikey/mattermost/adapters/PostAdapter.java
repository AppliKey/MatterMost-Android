package com.applikey.mattermost.adapters;

import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

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

    private ImageLoader mImageLoader;

    public PostAdapter(String currentUserId, ImageLoader imageLoader) {
        this.mCurrentUserId = currentUserId;
        this.mImageLoader = imageLoader;
    }

    public void updateDataSet(List<PostDto> data) {
        this.mData.clear();
        this.mData.addAll(data);
        notifyDataSetChanged();
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

        boolean showAuthor = position == 0 ||
                !hasSameAuthor(mData.get(position - 1).getPost(), dto.getPost());

        boolean showTime = position == mData.size() - 1 ||
                !oneTimePosts(dto.getPost(), mData.get(position + 1).getPost()) ||
                !hasSameAuthor(dto.getPost(), mData.get(position + 1).getPost());

        boolean showDate = true;

        if (isMy(dto.getPost())) {
            holder.bindOwn(dto, showAuthor, showTime, showDate);
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

        void bindOwn(PostDto dto, boolean showAuthor, boolean showTime, boolean showDate) {
            mTvTimestamp.setText(TimeUtil.formatTimeOrDateTime(dto.getPost().getCreatedAt()));
            mTvName.setText(dto.getAuthorName());
            mTvMessage.setText(dto.getPost().getMessage());

            mTvName.setVisibility(showAuthor ? View.VISIBLE : View.GONE);
            mTvTimestamp.setVisibility(showTime ? View.VISIBLE : View.GONE);
        }

        void bindOther(PostDto dto, boolean showAuthor, boolean showTime,
                boolean showDate, ImageLoader imageLoader) {
            bindOwn(dto, showAuthor, showTime, showDate);

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

    private boolean hasSameAuthor(Post post, Post nextPost) {
        return post.getUserId().equals(nextPost.getUserId());
    }

    private boolean oneTimePosts(Post post, Post nextPost) {
        return TimeUtil.sameTime(post.getCreatedAt(), nextPost.getCreatedAt());
    }
}
